# Дизайн-система Competra — «Лес»

> Единый источник правды по визуальному языку для **обоих** клиентов:
> `competra-android` (Jetpack Compose) и `competra-web` (Compose Multiplatform/wasm).
> Меняешь токен здесь → зеркалишь в оба проекта. Не правь цвета/отступы «по месту».

Направление: **«Лес»** — природный аутдор. Хвойный зелёный как основной,
терракота как акцент, тёплый кремовый фон. Спокойно, премиально, «на природе».

Поддерживаются **светлая и тёмная** темы. Динамические цвета Android (Material You)
отключены — палитра у нас фирменная.

---

## 1. Цвета (Material 3 colorScheme)

### Светлая тема
| Роль | HEX | Назначение |
|---|---|---|
| primary | `#2E5D43` | Хвойный — основные действия, активные элементы |
| onPrimary | `#FFFFFF` | Текст/иконки на primary |
| primaryContainer | `#B6E5C9` | Светлый зелёный фон (бейджи, чипы) |
| onPrimaryContainer | `#0A2417` | Текст на primaryContainer |
| secondary | `#C8643C` | Терракота — акцент, второстепенные CTA |
| onSecondary | `#FFFFFF` | Текст на secondary |
| secondaryContainer | `#FCDBC8` | Светлая терракота-подложка |
| onSecondaryContainer | `#3A1A0C` | Текст на secondaryContainer |
| tertiary | `#6B7A5A` | Приглушённая олива (доп. акцент) |
| background | `#F4F1E8` | Кремовый фон экрана |
| onBackground | `#21302A` | Основной текст |
| surface | `#FFFFFF` | Карточки, листы, поверхности |
| onSurface | `#21302A` | Текст на surface |
| surfaceVariant | `#E5E2D6` | Разделители, второстепенные поверхности |
| onSurfaceVariant | `#4C5A50` | Второстепенный текст, иконки |
| outline | `#7E877C` | Обводки, бордеры |
| outlineVariant | `#C9CEC3` | Тонкие разделители |
| error | `#B3261E` | Ошибки |
| onError | `#FFFFFF` | Текст на error |

### Тёмная тема
| Роль | HEX | Назначение |
|---|---|---|
| primary | `#7FCFA0` | Светлый хвойный (на тёмном фоне) |
| onPrimary | `#06311D` | |
| primaryContainer | `#1E4A34` | |
| onPrimaryContainer | `#9CEBC0` | |
| secondary | `#E8A07C` | Светлая терракота |
| onSecondary | `#4A2210` | |
| secondaryContainer | `#693924` | |
| onSecondaryContainer | `#FCDBC8` | |
| tertiary | `#AEBE96` | |
| background | `#121A15` | |
| onBackground | `#E1E6DD` | |
| surface | `#1B271F` | |
| onSurface | `#E1E6DD` | |
| surfaceVariant | `#3F4A42` | |
| onSurfaceVariant | `#BFCABE` | |
| outline | `#899389` | |
| outlineVariant | `#3F4A42` | |
| error | `#F2B8B5` | |
| onError | `#601410` | |

### Статусы соревнования (семантика)
Единые цвета для `CompetitionStatus` (бейджи, чипы):
| Статус | Светлая | Тёмная | Смысл |
|---|---|---|---|
| Черновик / запланировано | `onSurfaceVariant` | `onSurfaceVariant` | нейтральный |
| Регистрация открыта | `primary` (зелёный) | `primary` | можно записаться |
| Идёт | `secondary` (терракота) | `secondary` | активно сейчас |
| Завершено | `outline` (серый) | `outline` | в прошлом |

---

## 2. Типографика
База — стандартная Material 3 type scale, шрифт системный (`FontFamily.Default`).
Акценты по весам:
- `headlineSmall` / `titleLarge` — **Bold**, заголовки экранов и карточек
- `titleMedium` — **SemiBold**, подзаголовки
- `bodyLarge` / `bodyMedium` — Normal, основной текст
- `bodySmall` — мета-информация (дата, место)
- `labelLarge` — Medium, кнопки и ссылки-действия
- `labelMedium` / `labelSmall` — Medium, бейджи и подписи

---

## 3. Отступы (8pt-ритм)
Используем существующий `Dimens` (значения в dp):
- `SIZE_QUARTER` = 4 — микро-зазоры (иконка↔текст)
- `SIZE_HALF` = 8 — между карточками, мелкие отступы
- `SIZE_HALFER` = 12 — внутренние зазоры
- `SIZE_BASE` = 16 — стандартный padding контента/карточек
- `SIZE_BASE_HALF` = 24 — секции
- `SIZE_DOUBLE` = 32 — крупные блоки, пустые состояния

---

## 4. Формы
Используем существующий `shapes` (`theme/Shapes.kt`):
- extraSmall 4 · small 8 · medium 12 · **large 16**
- Карточки и крупные поверхности — `large` (16dp)
- Кнопки/поля — `small`/`medium`
> ВАЖНО: `shapes` нужно передать в `MaterialTheme(...)` — сейчас не передаётся.

---

## 5. Компоненты (спецификации)
- **Card**: `surface` фон, радиус 16dp, elevation 1–2dp (не 4 — чище), padding 16dp.
- **Кнопка основная**: filled, `primary`. Вторичная/CTA: `secondary` (терракота).
- **Бейдж/чип статуса**: `primaryContainer`/семантика статуса, скругление 16dp, padding 8×4.
- **Пустое состояние**: иконка `onSurfaceVariant` 64dp + заголовок `titleMedium` SemiBold + подзаголовок `bodyMedium` `onSurfaceVariant`, по центру.
- Переиспользовать существующие `DSButton`, `DSText`, `DSTextInput` вместо «голых» Material-виджетов.

---

## 6. Где живут токены
- **Android**: цветовые схемы — в `core/designsystem` (а не в `app/ui/theme`); `app` их потребляет. `Dimens`, `Shapes` — уже здесь.
- **Web** (`competra-web`): `web/.../theme/Theme.kt` — зеркалит те же HEX в `lightColorScheme`/`darkColorScheme`.
- При изменении любого токена — обнови обе стороны и этот документ.
