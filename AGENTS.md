# AGENTS.md

## Agent
**Gemini (Android Studio AI)**

---
## General

- Write all your answers in Russian
- Do not delete commented lines
- Stick to the existing project architecture
- Add kdoc


## Purpose

Gemini assists Android developers by providing:

- Context-aware code suggestions
- Refactoring recommendations
- Bug detection hints
- Documentation generation
- Unit and UI test code creation

---

## Project Context

- **Language:** Kotlin
- **Architecture:** MVVM / Clean Architecture
- **UI Framework:** Jetpack Compose
- **Networking:** Retrofit + OkHttp
- **Database:** Room
- **Target SDK:** API 36
- **Minimum SDK:** API 26

---

## Behavior Guidelines

### Code Quality & Style

- Follow **Kotlin coding conventions**.
- Use **Jetpack Compose idioms** for UI.
- Avoid boilerplate; suggest **modern Android patterns**.
- Use **Coroutines + Flow / StateFlow** for asynchronous operations.

---

### Code Generation Rules

- Suggest **code snippets** rather than full implementations unless explicitly requested.
- Include **inline comments** explaining complex logic.
- Suggest **default error handling** for network and database operations.
- Include **dependency injection patterns** (Koin) where applicable.

---

### API & Library Usage

- Recommend **official Android libraries** first (Jetpack, AndroidX).
- Prefer:
    - **Room** for persistence
    - **Retrofit** for networking
    - **Coil / Glide** for image loading
- Warn about **deprecated APIs** and suggest modern alternatives.

---

### Testing Guidelines

- Recommend **unit tests** for:
    - ViewModels
    - Repositories
    - Business logic

- Suggest **instrumented tests** for Compose UI using **Compose Testing Library**.

- Generate mocked dependencies using:
    - **Mockito**
    - **MockK**

---

### Security & Privacy

- Never hardcode secrets (API keys, passwords).
- Suggest encrypted storage for sensitive data:
    - **EncryptedSharedPreferences**
    - **SQLCipher**

---

### Documentation & Learning

- Provide **short explanations of APIs** when generating code.
- Include **links to official Android documentation** when applicable.
- Suggest **best practices** for:
    - Performance
    - Memory usage
    - Maintainability