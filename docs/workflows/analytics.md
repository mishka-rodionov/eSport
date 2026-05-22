# Workflow: аналитика при создании экранов и фич

> Триггер из [CLAUDE.md](../../CLAUDE.md). Прочитай этот файл **до** того, как начнёшь писать код.

В приложении подключён Яндекс AppMetrica через модуль `:core:analytics`. Чтобы новые экраны и фичи не оказывались «слепыми» (пользователь ими пользуется, а в аналитике их нет), при их добавлении нужно явно регистрировать события. Конвенции имён см. в [skills/analytics-events.md](../skills/analytics-events.md).

## Когда применять

Этот workflow обязателен, если ты:

- Создаёшь новый Composable-экран (новый `*Screen.kt` + новый route в navigation-graph).
- Добавляешь новое пользовательское действие: кнопку, форму, фильтр, бизнес-операцию.
- Добавляешь новый feature-модуль.

Не применять (исключения):

- Чисто визуальные действия без бизнес-эффекта: открыть/закрыть диалог, развернуть аккордеон, переключить tab.
- Точечный bug-fix существующего поведения.
- Рефакторинги без изменения пользовательского API.

## Правило 1 — новый экран всегда добавляется в словарь экранов

При создании нового экрана выполни **все три шага**:

### 1. Добавь кейс в `AnalyticsScreen`

Файл: [`core/analytics/src/main/java/com/competra/analytics/AnalyticsScreen.kt`](../../core/analytics/src/main/java/com/competra/analytics/AnalyticsScreen.kt).

```kotlin
enum class AnalyticsScreen(val screenName: String) {
    // ...
    ProfileSettings("profile_settings"),   // ← новый кейс
}
```

Имя экрана — `snake_case`, формат `<area>_<screen>`. См. [skills/analytics-events.md → Имена экранов](../skills/analytics-events.md#имена-экранов).

### 2. Добавь ветку в `AnalyticsScreen.fromRoute()`

```kotlin
route.contains("ProfileSettingsRoute") -> ProfileSettings
```

`contains` — потому что Compose Navigation 2.8 пишет в `destination.route` FQN класса route'а с параметрами, а нам нужно матчить по короткому имени.

### 3. Убедись, что экран открывается под `TrackNavScreens`

Для трёх основных табов это уже сделано в `MainActivity.kt:263` — там вокруг `rememberNavController()` стоит `TrackNavScreens(navController, analyticsTracker)`. Если новый экран открывается в стандартном flow (через один из трёх табных NavController'ов) — ничего больше делать не надо.

Если новый экран открывается в **отдельном NavHost** (вложенный nav, диалог-as-screen и т.п.) — добавь туда `TrackNavScreens(navController, koinInject<AnalyticsTracker>())`.

> Без шагов 1+2 экран будет открываться, но `screen_view` не уйдёт — экран будет «слепым» в кабинете AppMetrica.

## Правило 2 — новое действие требует решения про аналитику

Когда добавляешь новое пользовательское действие (новый `Action` в sealed class, новая кнопка, новая бизнес-операция), **до того как написать код**:

### 1. Спроси у пользователя

Используй `AskUserQuestion` с примерно такой формулировкой:

> «Добавляешь действие "Поделиться соревнованием". Нужно ли аналитическое событие на это действие?»

Предложи 2 опции:
- **Да, добавляем событие.** Предложи имя по конвенциям и параметры. Пример: `event_share_clicked(event_id: Long, share_target: ShareTarget)`.
- **Нет, без аналитики.**

Если действие важное (бизнес-эффект, воронка) — поставь «добавляем» первым с пометкой «(Recommended)».

### 2. Если пользователь согласился — добавь событие в словарь

Файл: [`core/analytics/src/main/java/com/competra/analytics/AnalyticsEvent.kt`](../../core/analytics/src/main/java/com/competra/analytics/AnalyticsEvent.kt).

**Без параметров:**

```kotlin
data object EventShareClicked : AnalyticsEvent("event_share_clicked")
```

**С параметрами:**

```kotlin
enum class ShareTarget { TELEGRAM, WHATSAPP, OTHER }

class EventShareClicked(eventId: Long, target: ShareTarget) : AnalyticsEvent(
    "event_share_clicked",
    mapOf("event_id" to eventId, "share_target" to target.name.lowercase()),
)
```

Категориальные значения — обязательно через enum, не строковые литералы. См. [skills/analytics-events.md → Параметры](../skills/analytics-events.md#параметры-событий).

### 3. Вызови событие в ViewModel

В соответствующей ветке `onAction(...)`:

```kotlin
is EventDetailsAction.ShareClick -> {
    analytics.trackEvent(AnalyticsEvent.EventShareClicked(eventId, target))
    // ... остальная логика
}
```

Если ViewModel ещё не получает `AnalyticsTracker` — добавь параметр в конструктор. Koin резолвит автоматически через `viewModelOf(::Name)`, т.к. `analyticsCoreModule` уже зарегистрирован в `CompetraApp`.

## Правило 3 — новый feature-модуль

В `build.gradle.kts` нового модуля нужна зависимость:

```kotlin
implementation(project(":core:analytics"))
```

Без неё ViewModel в новом модуле не сможет инжектить `AnalyticsTracker`. Текущие модули с этой зависимостью: `feature/events`, `feature/profile`, `feature/center`, `core/eventdetails`.

## Что делать, если не уверен

- **Не уверен в имени события** → предложи пользователю 2-3 варианта в `AskUserQuestion`, обоснуй каждый. Не угадывай.
- **Не уверен, нужно ли событие вообще** → задай вопрос. Лучше задать лишний раз, чем добавить шум в аналитику.
- **Не уверен в параметрах** → предложи минимальный набор (только ID сущности), остальное можно добавить позже без переименования события.

## Связанные файлы

- Словарь экранов: [`core/analytics/.../AnalyticsScreen.kt`](../../core/analytics/src/main/java/com/competra/analytics/AnalyticsScreen.kt)
- Словарь событий: [`core/analytics/.../AnalyticsEvent.kt`](../../core/analytics/src/main/java/com/competra/analytics/AnalyticsEvent.kt)
- Интерфейс трекера: [`core/analytics/.../AnalyticsTracker.kt`](../../core/analytics/src/main/java/com/competra/analytics/AnalyticsTracker.kt)
- Реализация: [`core/analytics/.../AppMetricaAnalyticsTracker.kt`](../../core/analytics/src/main/java/com/competra/analytics/AppMetricaAnalyticsTracker.kt)
- Авто-трекинг экранов: [`core/analytics/.../NavTracking.kt`](../../core/analytics/src/main/java/com/competra/analytics/NavTracking.kt)
- Подключение в навигации: [`app/.../presentation/main/MainActivity.kt`](../../app/src/main/java/com/competra/app/presentation/main/MainActivity.kt) (вокруг строки 263)
- Конвенции имён: [`docs/skills/analytics-events.md`](../skills/analytics-events.md)
