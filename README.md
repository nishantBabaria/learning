# Learning Dashboard Mobile Application

A clean, modern Android Learning Dashboard built with **Kotlin**, **Jetpack Compose**, **Room Database**, **Coroutines**, and **StateFlow**.

---

## Live Postman Mock API Integration
The application connects directly to a live **Postman Cloud Mock Server**:
* **Base URL:** `https://4a16ed63-e380-4d43-9844-3c66f8e65e64.mock.pstmn.io/`
* **Login Endpoint (`POST`):** `https://4a16ed63-e380-4d43-9844-3c66f8e65e64.mock.pstmn.io/login`
* **Courses Endpoint (`GET`):** `https://4a16ed63-e380-4d43-9844-3c66f8e65e64.mock.pstmn.io/courses`

OkHttp and Retrofit execute live HTTP requests across the network. When internet is disconnected, native network exceptions (`UnknownHostException` / `ConnectException`) trigger the Room Database offline cache fallback.

---

## 1. Architecture
**Chosen Architecture:** MVVM (Model-View-ViewModel) + Clean Data Layer / Repository Pattern + Lazy Dependency Injection Container (`AppContainer`).

* **UI Layer:** Declarative Jetpack Compose UI screens observing UI State standard `StateFlow` primitives.
* **ViewModel Layer:** Handles presentation logic, input validation, and exposes unidirectional `StateFlow` to composables.
* **Repository Layer:** Acts as a single source of truth (`CourseRepository`), orchestrating network requests (Remote Mock API) and local database persistence (Room DB).
* **Why chosen:**
  * Clean separation of concerns between UI, business rules, and data sources.
  * Easy testability with mockable repository and DAOs.
  * Standard Android architecture ensuring scalability and maintainability.

---

## 2. Offline Support
* **Local Persistence:** Powered by **Room Database** (`AppDatabase`, `CourseDao`, `CourseEntity`, `LessonEntity`, `CourseWithLessons`).
* **Cache Strategy:**
  * When online, courses and lessons are fetched from the live Postman remote API and synced into Room database via `@Insert(onConflict = OnConflictStrategy.REPLACE)`.
  * The UI collects a reactive `Flow` directly from Room DB.
  * If the network is turned off or remote API fails, the app catches network exceptions and smoothly falls back to displaying cached data from Room DB without crashing.
  * Toggling lesson completion updates Room DB locally in a single atomic database transaction (`@Transaction`), automatically recalculating course progress percentage and emitting updated state to the UI.

---

## 3. Security
* **EncryptedSharedPreferences / Encrypted DataStore:** Sensitive authentication credentials, OAuth tokens, and JWT tokens are stored in **EncryptedSharedPreferences** (`SecurePreferencesManager`) backed by the **Android Keystore System** (`MasterKeys`).
* **Biometric Auth & Token Rotation:** Pair token storage with biometric authentication (`BiometricPrompt`) and short-lived access tokens with automatic silent refresh mechanisms in production.

---

## 4. Scale (1 Million Users + Hundreds of Courses)
1. **Paging & Pagination:** Implement Jetpack **Paging 3** library with `RemoteMediator` to load courses incrementally from API to Room DB.
2. **Optimistic Local UI Updates & Sync Queue:** Store offline lesson completions in a local WorkManager sync queue, pushing changes to backend API when connectivity is restored.
3. **HTTP Caching & CDN:** Utilize OkHttp HTTP caching headers (`ETag`, `Cache-Control`) and CDN for static course content and media.
4. **Database Indexing & Query Optimization:** Index foreign keys (`courseId`) in Room database tables for sub-millisecond query lookups.

---

## 5. Second Platform Implementation (iOS / macOS)
To build the identical app on **iOS / macOS**:
* **UI Framework:** Use **SwiftUI** with `NavigationStack` for navigation.
* **Architecture:** **MVVM** with `ObservableObject` / `@Observable` macro and `@Published` state properties.
* **Offline Storage:** Use **SwiftData** or **Core Data** with `@FetchRequest` / `AsyncSequence` streams.
* **Networking:** `URLSession` with `async/await` decoding JSON via `Codable`.
* **Testing:** **XCTest** framework testing ViewModels and Repositories using mock protocol implementations.
