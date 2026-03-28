# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew build                  # Full build (all modules)
./gradlew clean build            # Clean + full build

./gradlew test                   # Unit tests (all modules)
./gradlew :feature:center:test   # Unit tests for a specific module
./gradlew connectedAndroidTest   # Instrumented tests (requires device/emulator)
./gradlew lint                   # Static analysis
```

## Architecture

**Clean Architecture + MVVM + Multi-module**. Layer dependency direction: `:feature` → `:domain` ← `:data`. Features never depend on each other.

### Module Map

| Module | Role |
|---|---|
| `:app` | Entry point, DI wiring, bottom navigation, NFC dispatch |
| `:domain` | Pure Kotlin: models, repository interfaces, use cases |
| `:data:local` | Room database (DAOs, entities, type converters) |
| `:data:remote` | Retrofit services, data sources, OkHttp interceptors |
| `:data:navigation` | Navigation state/contracts |
| `:feature:center` | Competition creation, participants, draw, chip distribution, results |
| `:feature:events` | Events/competitions list and detail views |
| `:feature:profile` | User profile |
| `:core:designsystem` | Material 3 theme, colors, typography |
| `:core:nfchelper` | NFC read/write abstraction (MifareClassic, MifareUltralight, ParticipantCard, MasterCard) |
| `:core:ui` | Shared composables |
| `:core:resources` | Strings, drawables |
| `:utils` | Kotlin extension functions |

### Feature Module Structure

Each feature module follows this pattern:
```
feature/<name>/
  data/
    State.kt          # UI state data class
    Action.kt         # User action sealed class
    Interactor*.kt    # Business logic helpers
  presentation/
    *Screen.kt        # Composable screens
    *ViewModel.kt     # ViewModel with StateFlow<State>
```

### Dependency Injection

Koin is used throughout. All modules are registered in `SportApp.onCreate()` in `:app`. Each module has its own `*Module.kt` file. When adding a new dependency, wire it via a Koin module — no manual instantiation.

### Navigation

Type-safe Compose Navigation. Each feature exposes a navigation graph extension function (e.g., `centerGraph(...)`, `eventsGraph(...)`). The `NavHost` in `:app` composes all graphs.

### State Management

Features use a unidirectional State/Action pattern:
- ViewModel exposes `StateFlow<State>`
- UI sends `Action` objects to the ViewModel
- ViewModel mutates state via `_state.update { ... }`

## Key Technologies

- **Kotlin 2.3 / JDK 17** — use `jvmToolchain(17)`
- **Jetpack Compose + Material 3** — all UI is Compose, no XML layouts
- **Koin 4.1** — DI
- **Room 2.8 + KSP** — local persistence; KSP processes annotations at build time
- **Retrofit 3 + OkHttp 5** — networking; Chucker interceptor available in debug builds
- **Coroutines + Flow/StateFlow** — all async work
- **DataStore + Security Crypto** — for sensitive key-value storage

## Code Conventions (from AGENTS.md)

- Do not delete commented-out lines
- Add KDoc to public declarations
- Follow existing package/module boundaries — do not add Android dependencies to `:domain`
- Use `EncryptedSharedPreferences` or `SQLCipher` for sensitive data (never plain SharedPreferences for secrets)
- Prefer official Jetpack/AndroidX libraries; for images use Coil or Glide

## NFC

The app uses NFC to read/write participant chips for orienteering competitions. `core:nfchelper` abstracts tag types. NFC dispatch is handled in `MainActivity` (`onResume`/`onPause` reader mode, `onNewIntent` for tag discovery). The manifest declares `TECH_DISCOVERED` intent filter for NFC.

## SDK Targets

- `minSdk 26` / `targetSdk 34` / `compileSdk 36`
- Physical NFC device required for NFC features; emulator cannot emulate NFC hardware
