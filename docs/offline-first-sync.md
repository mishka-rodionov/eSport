# Offline-first синхронизация модуля `:feature:center`

## Цель

Организатор соревнований по ориентированию работает на устройстве в полевых условиях
(лес, удалённые локации, нестабильный интернет). Все мутации в `:feature:center`
(создание соревнования, регистрация участников, считывание чипов, результаты, жеребьёвка)
обязаны происходить мгновенно вне зависимости от наличия сети, а серверная синхронизация —
выполняться фоном при появлении соединения.

## Принципы

1. **Локалка first.** Любая правка сначала уходит в Room с `isSynced = false`. RPC из ViewModel
   и Interactor удалены — выгрузкой занимается только `SyncCenterWorker`.
2. **Network-callback enqueue.** При появлении интернета `ConnectivityManager.NetworkCallback`
   ставит UniqueWork (KEEP) с `NetworkType.CONNECTED` и `ExponentialBackoff(30s)`.
3. **Server-wins.** Конфликт детектится по `serverUpdatedAt` против `updated_at`. При HTTP 409
   клиент перезаписывает локалку серверной записью с сохранением локального PK.
4. **Soft-delete с физическим purge.** На клиенте пометка `isDeleted = true, isSynced = false`,
   на сервере — физическое удаление. Worker после успешного DELETE удаляет запись локально.

---

## Sync-trait

Каждая синхронизируемая сущность несёт одинаковый набор полей:

| Поле               | Назначение                                                                 |
| ------------------ | -------------------------------------------------------------------------- |
| `remoteId`         | Серверный PK (Long или String). Null = запись ещё не выгружалась.          |
| `isSynced`         | true, если последнее изменение успешно ушло на сервер.                     |
| `isDeleted`        | true = запись помечена на удаление, ждёт DELETE-выгрузки.                  |
| `lastModified`     | Клиентский timestamp последней правки.                                     |
| `serverUpdatedAt`  | Серверный `updated_at` из последнего успешного sync. Используется для 409. |
| `syncError`        | Текст последней ошибки (для диагностики).                                  |

Применено к: `Competition (@Embedded)`, `ParticipantGroupEntity`, `DistanceEntity`,
`OrganizerEntity`, `StageEntity`, `OrienteeringParticipantEntity`, `OrienteeringResultEntity`.

Миграция Room: `MIGRATION_36_37` в `data/local/.../database/Migrations.kt`, `DB_VERSION = 37`.

---

## Архитектура клиента

```
                       UI (Compose Screens)
                              │
                              ▼
                       ViewModels (StateFlow + Action)
                              │
                              ▼
              OrienteeringCompetitionInteractor
                              │
                              ▼
       ┌────────────────────────────────────┐
       │  OrienteeringCompetitionLocalRepo  │   (markUnsynced = true ←→ false)
       └─────────────────┬──────────────────┘
                         │
                  Room DAOs / Entities

   (фоновая выгрузка вне UI-цепочки)
       SyncCenterWorker (CoroutineWorker)
              │
              ▼
       SyncOrchestrator ◀── ConflictResolver
              │                 (Gson parse + map)
              ▼
       OrienteeringCompetitionRemoteRepository
              │
              ▼
       Retrofit DataSource (OkHttp)
```

### Wrapper `markUnsynced` в LocalRepository

`OrienteeringCompetitionLocalRepositoryImpl` все мутирующие методы (`save*`, `update*`)
принимают `markUnsynced: Boolean = true`:

- `true` (дефолт) — путь UI/Interactor: перед записью копируется
  `isSynced = false, lastModified = now, syncError = null`.
- `false` — путь Worker'а и server→local pull: пишется как есть.

Это защищает от двух типов ошибок: забыть пометить правку при изменении из UI и затереть
`isSynced=true` обратно в false при server-pull.

### SyncOrchestrator pipeline

Шесть последовательных шагов:

```
1. syncCompetitions()       remoteId родителей нет → пропуск
2. syncDistances()          (требует competition.remoteId)
3. syncGroups()             (требует competition + distance remoteId)
4. syncParticipants()       (требует competition + group remoteId)
5. syncResults()            (требует competition + group remoteId)
6. syncDeletes()            обратный порядок: results → participants →
                            groups → distances → competitions
```

Если зависимость без `remoteId` — запись пропускается до следующего пробега.
Worker возвращает `Result.retry()` (Outcome.Partial), и WorkManager попробует ещё раз
с экспоненциальным backoff.

### Worker Result правила

| Outcome / Exception       | WorkManager Result | Действие                                      |
| ------------------------- | ------------------ | --------------------------------------------- |
| `Outcome.AllDone`         | `success`          | Все unsynced обработаны.                      |
| `Outcome.Partial`         | `retry`            | Остались зависимости без remoteId — повтор.   |
| `IOException` / 5xx       | `retry`            | Transient. WorkManager применит backoff.      |
| `ConflictException` (409) | success (внутри)   | Server-wins, локалка перезаписана.            |
| Прочие 4xx                | success (внутри)   | `syncError = msg` записывается в запись.      |
| Любое другое              | `failure`          | Не должно случаться при нормальной работе.    |

### Триггер запуска

`SportApp.onCreate()`:
1. `startKoin { workManagerFactory(); modules(..., syncModule) }`
2. `SportApp` реализует `Configuration.Provider` (factory от Koin).
3. В Manifest отключён дефолтный `WorkManagerInitializer` (через `androidx.startup`).
4. `NetworkAvailabilityObserver.start { SyncBootstrap.enqueue(this) }` — на каждое
   `onAvailable` ConnectivityManager-а ставит UniqueWork (`KEEP`).
5. `SyncBootstrap.enqueue(this)` — на старте, на случай если сеть уже есть
   и есть unsynced записи.

```kotlin
OneTimeWorkRequestBuilder<SyncCenterWorker>()
    .setConstraints(NetworkType.CONNECTED)
    .setBackoffCriteria(EXPONENTIAL, 30s)
```

---

## Conflict resolution (server-wins)

### Серверная сторона (`backend`)

При upsert каждый сервис сравнивает `req.serverUpdatedAt` с `current.updated_at`:

```kotlin
// в каждом upsert
if (existing != null && req.serverUpdatedAt != null &&
    req.serverUpdatedAt < existing[Table.updatedAt]
) {
    throw ConflictException(existing.toResponse())
}
```

Routing-helper `ApplicationCall.respondConflictAware { ... }` ловит исключение
и отвечает:

```
HTTP 409 Conflict
{
  "status": 0,
  "result": <актуальная серверная запись (XxxResponse)>,
  "errors": [{ "code": 409, "message": "Server record is newer" }]
}
```

### Клиентская сторона

`ResultCall` (Retrofit `CallAdapter`) превращает HTTP 409 в `ConflictException(httpCode, serverPayload)`.
`serverPayload` — сырой JSON-body ответа.

`SyncOrchestrator.handleResult` различает три ветки:

```
success     → onSuccess(value)
IOException → return true  (transient retry)
ConflictException(payload) → onConflict(payload)  (server-wins)
прочее      → onPermanentError(message)  (syncError = msg)
```

`ConflictResolver` парсит payload через Gson:

```
JsonObject envelope = gson.fromJson(payload)
JsonElement resultJson = envelope["result"]
XxxResponse response = gson.fromJson(resultJson, XxxResponse::class.java)
domainEntity = response.toDomain()
localRepo.updateXxx(domainEntity.copy(<сохраняем локальный PK>), markUnsynced = false)
```

Ключевой момент: при перезаписи **сохраняется локальный PK** (`localCompetitionId`,
`groupId`, `distanceId`, `id`), чтобы не сломать FK на участников/результаты.

---

## Soft-delete pipeline

Шаг 6 `syncDeletes()` идёт в **обратном** порядке родительских зависимостей:

```
Results → Participants → Groups → Distances → Competitions
```

Для каждой пометки `isDeleted = true, isSynced = false`:

```
если remoteId == null:
    → запись никогда не уходила на сервер → purgeLocally
если remoteId != null:
    → DELETE /event/orienteering/<entity>/{remoteId}
        success    → purgeLocally
        404 / 4xx  → "уже удалено" → purgeLocally (не зацикливаемся)
        IOException → retry на следующем запуске Worker
```

`purgeLocally` физически удаляет запись из Room (без soft-delete флага).

Особенность для `competitions`: `localRepository.deleteCompetition(id)` каскадно
удаляет всё связанное (results, participants, groups, distances) одной операцией.

---

## Архитектура сервера (Ktor + Exposed)

### Изменения схемы БД

Каждая синхронизируемая таблица получила колонку `updated_at: Long DEFAULT 0`:

```
competitions, orienteering_competitions, participant_groups,
orienteering_participants, orienteering_results, distances,
organizers, stages, split_times
```

Сервисы проставляют `updatedAt = System.currentTimeMillis()` на каждом insert/update.
Response DTO отдают `updatedAt` — клиент маппит в `entity.serverUpdatedAt`.

### Эндпоинты

```
POST   /event/orienteering/save/competitions    upsert competition
POST   /event/orienteering/save/participantGroup batch upsert групп
POST   /event/orienteering/save/participant      single upsert участника
POST   /event/orienteering/save/participants     batch upsert участников
POST   /event/orienteering/save/result           single upsert результата
POST   /event/orienteering/save/results          batch upsert результатов  (новое)
POST   /event/orienteering/save/distances        batch upsert дистанций

DELETE /event/orienteering/competitions/{id}     (новое)
DELETE /event/orienteering/participantGroups/{id} (новое)
DELETE /event/orienteering/participants/{id}     (новое)
DELETE /event/orienteering/results/{id}          (новое)
DELETE /event/orienteering/distances/{id}        (новое)

GET    /event/orienteering/competitions          (server → local pull)
GET    /event/orienteering/distances             (server → local pull)
GET    /event/orienteering/participantGroups     (server → local pull)
GET    /event/orienteering/participants/competition (server → local pull)
GET    /event/orienteering/results/competition   (server → local pull)
```

### Маппинг ID

| Сущность      | Local PK             | Server PK             | Связь                      |
| ------------- | -------------------- | --------------------- | -------------------------- |
| Competition   | `localCompetitionId` (Long autogen) | `Competitions.id` (Long autogen) | хранится в `Competition.remoteId` |
| OrienteeringCompetition | (часть Competition) | `OrienteeringCompetitions.id` (String UUID, equals client UUID) | `Competition.id` |
| ParticipantGroup | `groupId` (Long autogen) | `ParticipantGroups.id` (Long autogen) | `remoteId: Long?` |
| Distance      | `id` (Long autogen)  | `Distances.id` (Long autogen) | `remoteId: Long?`        |
| Participant   | `id` (String UUID)   | `OrienteeringParticipants.id` (String) | **PK один и тот же** |
| Result        | `id` (Long autogen)  | `OrienteeringResults.id` (String) | `remoteId: String?`     |

`SyncOrchestrator` перед отправкой каждой записи на сервер заменяет локальные
`competitionId` / `groupId` / `distanceId` на серверные `remoteId` через
helper-методы `getCompetitionRemoteId`, `getGroupRemoteId`, `getDistanceRemoteId`.

---

## Карта ключевых файлов

### Android

```
app/src/main/java/com/rodionov/sportsenthusiast/
  SportApp.kt                                 — Configuration.Provider, Koin + workManagerFactory
  AndroidManifest.xml                         — отключение дефолтного WorkManagerInitializer

core/sync/src/main/java/com/rodionov/core/sync/
  SyncCenterWorker.kt                         — CoroutineWorker
  SyncOrchestrator.kt                         — pipeline 6 шагов
  ConflictResolver.kt                         — server-wins parse + apply
  NetworkAvailabilityObserver.kt              — ConnectivityManager.NetworkCallback
  SyncBootstrap.kt                            — enqueueUniqueWork(KEEP)
  di/syncModule.kt                            — Koin (workerOf + singleOf)

data/local/src/main/java/com/rodionov/local/
  database/Migrations.kt                      — MIGRATION_36_37
  database/SEDatabase.kt                      — DB_VERSION = 37
  entities/orienteering/*.kt                  — sync-trait поля
  dao/**/*.kt                                 — getUnsynced() / getMarkedForDeletion()
  repository/OrienteeringCompetitionLocalRepositoryImpl.kt — markUnsynced wrapper

data/remote/src/main/java/com/rodionov/remote/
  network/retrofit/ResultCall.kt              — 409 → ConflictException
  datasource/orienteering/OrienteeringCompetitionRemoteDataSource.kt — endpoints
  repository/orienteering/OrienteeringCompetitionRemoteRepositoryImpl.kt — delete*Remotely
  request/orienteering/*.kt                   — serverUpdatedAt в Request DTO
  request/mappers/*.kt                        — Domain → Request
  response/orienteering/*.kt                  — updatedAt в Response DTO
  response/mappers/*.kt                       — Response → Domain (serverUpdatedAt)

domain/src/main/java/com/rodionov/domain/
  exception/ConflictException.kt              — несёт serverPayload
  models/Competition.kt                       — sync-trait в @Embedded
  models/orienteering/*.kt                    — sync-trait
  repository/orienteering/OrienteeringCompetitionLocalRepository.kt — markUnsynced + getUnsynced* + purge*Locally
  repository/orienteering/OrienteeringCompetitionRemoteRepository.kt — delete*Remotely

feature/center/src/main/java/com/rodionov/center/data/interactors/
  OrienteeringCompetitionInteractor.kt        — eager-RPC удалены, остались только локалка + server→local pull
```

### Backend (Ktor + Exposed)

```
src/main/kotlin/data/
  exception/ConflictException.kt              — currentResponse в теле
  database/entity/*.kt                        — updatedAt колонка
  services/*.kt                               — upsert проставляет updatedAt + 409 check
  routing/OrienteeringRouting.kt              — respondConflictAware helper, batch results, DELETE endpoints
  requests/orienteering/*.kt                  — serverUpdatedAt поле
  response/orienteering/*.kt                  — updatedAt поле
```

---

## Сценарии

### 1. Normal flow (online)

```
UI: добавить участника
  → ViewModel(action) → Interactor.saveParticipant()
  → localRepo.saveParticipant(participant)        // markUnsynced=true → isSynced=false
  → SyncBootstrap.enqueue() (если ещё не enqueued)
                                                   ┌──── Worker ────┐
                                                   │ Pipeline       │
                                                   │ syncParticipants():
                                                   │   POST /save/participants
                                                   │   → 200 OK + updatedAt
                                                   │ localRepo.updateParticipants(
                                                   │   participants.copy(isSynced=true,
                                                   │                     serverUpdatedAt=...),
                                                   │   markUnsynced=false)
                                                   └────────────────┘
```

### 2. Offline → Online

```
[airplane mode]
UI: считал чип финиширующего → saveParticipantResult()
  → localRepo: result сохранён, isSynced=false
[airplane mode off]
ConnectivityManager.onAvailable() → SyncBootstrap.enqueue()
SyncCenterWorker.doWork() → Outcome.AllDone → result isSynced=true
```

### 3. Конфликт (server-wins)

```
device A: правит имя участника offline
device B: правит то же имя online (updated_at = T2)
device A: сеть → POST с serverUpdatedAt = T0 (T0 < T2)
server: ConflictException(participantResponse) → HTTP 409 + currentResponse
client: ResultCall → ConflictException(payload)
SyncOrchestrator.handleResult → onConflict
ConflictResolver.applyParticipantConflict():
  - parse payload.result → OrienteeringParticipantResponse
  - response.toDomain().copy(competitionId=local, groupId=local)
  - localRepo.updateParticipants(domain, markUnsynced=false)
```

Локальная правка с устройства A теряется. Это явное условие — сценарий
«один организатор на одно соревнование» допускает потерю.

### 4. Soft-delete

```
UI: удалить участника
  → ViewModel(action) → localRepo.update(participant.copy(isDeleted=true))
                                                  // markUnsynced=true → isSynced=false
Worker → syncDeletes() → syncParticipantDeletes():
  participant.remoteId != null
    → DELETE /participants/{id}
    → 200 OK
    → localRepo.deleteParticipant(participant.id)  // physical purge
```

---

## Верификация (ручной чек-лист)

1. **Миграция.** Apk-апгрейд поверх v36 → Database Inspector → новые колонки и `DB_VERSION = 37`.
2. **Offline create.** Airplane mode → добавить участника → `isSynced=0, lastModified>0`.
3. **Online sync.** Включить сеть → Logcat `WM-Processor` фиксирует Worker → 1–3s → `isSynced=1`.
4. **Server-wins.** Online создать участника, offline-правка имени, через psql изменить
   `last_name` и `updated_at = now`, online → имя локально = серверное.
5. **Soft-delete.** Удалить online → запись исчезает локально и с сервера.
   Удалить offline → `isDeleted=1`, online → запись исчезает с обеих сторон.
6. **ID-зависимости.** Offline создать `competition + groups + participants` → online →
   один пробег публикует всё последовательно.
7. **Backoff.** Остановить backend → создать запись offline → online → Logcat показывает
   `Result.retry()` с экспоненциально растущими задержками.

---

## Edge cases и ограничения

- **Один организатор на одно соревнование.** Server-wins без интерактивного UI разрешения
  конфликтов; локальная правка может быть потеряна.
- **Batch participants 409.** Сервер бросает на первой конфликтной записи, клиент
  получает один Response в payload — применяется server-wins только для первого встретившегося,
  остальные обрабатываются на следующем пробеге Worker'а.
- **CommonModel envelope.** Все backend-ответы оборачиваются в `CommonModel<T>` с полями
  `status / result / errors`. Конфликтный 409 кладёт актуальную запись в `result`.
- **Soft-delete только для новых записей.** Если запись уже синхронизирована и помечена
  `isDeleted`, Worker отправит DELETE. Если запись никогда не уходила на сервер, она
  просто purges локально.
- **DELETE 404.** Трактуется как «уже удалено» — purge локально, чтобы не зацикливать
  Worker на каждой попытке.

---

## Что осталось на будущие итерации

- Полное **тестирование** (unit + instrumentation) `SyncOrchestrator` и `ConflictResolver`.
- **UI для конфликтов** (если потребуется ручное разрешение вместо server-wins).
- **Метрики/телеметрия** успешности sync (количество retry, среднее время до синхронизации).
- **Sync для других модулей** (`:feature:events`, `:feature:profile`) на той же инфраструктуре —
  `:core:sync` спроектирован переиспользуемым.
- **Перевод PK `OrienteeringResultEntity` на String UUID** (Вариант B из плана) —
  устранит двойной маппинг `id (Long) ↔ remoteId (String)`.
