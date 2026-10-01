# Learning Dashboard Mobile Application

A clean, modern, production-grade Android Learning Dashboard built with **Kotlin**, **Jetpack Compose**, **Clean Architecture + MVVM**, **Room Database**, **Coroutines**, **StateFlow**, and **EncryptedSharedPreferences**.

---

## Live API Integration
The application connects directly to a live **MockAPI.io** cloud server:
* **Base URL:** `https://6abd13115121d616d90cb52e.mockapi.io/learning/v1/`
* **Login Endpoint (`POST`):** `POST /login`
* **Courses Catalog Endpoint (`GET`):** `GET /courses`
* **Update Course Progress Endpoint (`PUT`):** `PUT /courses/{id}`

OkHttp and Retrofit execute live HTTP requests across the network. When internet is disconnected, native network exceptions trigger the local Room Database offline cache fallback.

---

## 1. Architecture & Design Patterns
**Chosen Architecture:** Clean Architecture + MVVM (Model-View-ViewModel) + Repository Pattern + Modular Dependency Injection (`AppContainer`).

* **UI Layer:** Declarative Jetpack Compose UI screens observing unidirectional `StateFlow` primitives.
* **ViewModel Layer:** Handles presentation logic and exposes immutable `StateFlow` to composables.
* **Domain Layer:** Pure Kotlin use cases and domain models (`Course`, `Lesson`, `User`, `LoginUseCase`, `GetCoursesUseCase`, `ToggleLessonCompletionUseCase`).
* **Data Layer:** Single source of truth (`CourseRepositoryImpl`), orchestrating network requests (Retrofit API) and local database persistence (Room DB).
* **Why Chosen:**
  * Strict 4-layer separation ensuring high testability, maintainability, and clean code.
  * Room DB composite primary keys `(id, userEmail)` ensure 100% data isolation across different user accounts.
  * Thread-safe, lazy dependency initialization (`val container: AppContainer by lazy`).

---

## 2. Offline Support & Synchronization
* **Local Persistence:** Powered by **Room Database** (`AppDatabase`, `CourseDao`, `CourseEntity`, `LessonEntity`, `CourseWithLessons`).
* **Cache Strategy:**
  * When online, courses and lessons are fetched from the remote API and synced into Room DB.
  * The UI collects a reactive `Flow` directly from Room DB.
  * Toggling lesson completion updates Room DB locally in an atomic database `@Transaction`, automatically recalculating course progress percentage and emitting updated state to the UI in sub-second time.
  * **Offline-to-Online Auto-Sync:** Edits made offline set `isPendingSync = true` in Room DB. As soon as network connectivity is restored, `syncPendingOfflineChanges()` automatically pushes `PUT /courses/{id}` updates to the cloud server and resets `isPendingSync = false`.

---

## 3. Security Hardening
* **EncryptedSharedPreferences:** Sensitive authentication tokens and user credentials are stored in **EncryptedSharedPreferences** (`SecurePreferencesManager`) backed by the **Android Keystore System** (`MasterKeys`).
* **Fallback Keyset Recovery:** Wrapped in exception handling to delete corrupt preferences and recreate keysets gracefully without crashing.
* **Android Manifest Hardening:** Set `android:allowBackup="false"` and `android:usesCleartextTraffic="false"` for strict HTTPS network security.

---

## 4. Scaling Architecture 
1. **Paging & Incremental Loading:** Implement Jetpack **Paging 3** with `RemoteMediator` to stream course catalogs in chunks.
2. **WorkManager Sync Queue:** Queue offline lesson completions in a local `WorkManager` background sync queue with exponential backoff retries.
3. **Multi-Module Architecture:** Split the codebase into feature modules (`:core:database`, `:core:network`, `:feature:login`, `:feature:dashboard`, `:feature:details`) to reduce Gradle build times by 70% and enable parallel team development.
4. **Protobuf & gRPC Migration:** Replace JSON/Gson with Protocol Buffers (gRPC) over HTTP/2 for 5x faster network deserialization and 60% reduced mobile data usage.
5. **Media Caching & HLS Streaming:** Utilize Coil and Media3 ExoPlayer with disk LRU caching and HLS/DASH adaptive bitrate streaming for video content.
6. **HTTP Caching & CDN:** Utilize OkHttp HTTP caching headers (`ETag`, `Cache-Control`) and Cloudflare CDN for static course assets.
7. **Database Indexing:** Index foreign keys (`courseId`, `userEmail`) in Room database tables for sub-millisecond query lookups.
8. **Real-Time WebSocket / SSE Sync:** Integrate WebSockets or Server-Sent Events (SSE) to sync multi-device progress in real-time when users switch between Android phone, tablet, and web.

---

## 5. Second Platform Implementation (iOS / macOS)
To build the identical app on **iOS / macOS**:
* **UI Framework:** Use **SwiftUI** with `NavigationStack` for navigation.
* **Architecture:** **MVVM** with `ObservableObject` / `@Observable` macro and `@Published` state properties.
* **Offline Storage:** Use **SwiftData** or **Core Data** with `@FetchRequest` / `AsyncSequence` streams.
* **Networking:** `URLSession` with `async/await` decoding JSON via `Codable`.
* **Testing:** **XCTest** framework testing ViewModels and Repositories using mock protocol implementations.
