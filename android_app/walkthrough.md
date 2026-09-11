# AI CROP CARE — Android Application Walkthrough

## Phase 2 — Native Android App Foundation & Basic UI

### Summary
Built the complete native Android foundation using:
- **Kotlin** & **Jetpack Compose** (Material 3)
- **Navigation Compose** with custom `AppNavigation` and persistent `NavigationBar`
- **5 Native Screens:**
  1. `SplashScreen`: Branded animated leaf pulse with automatic 1.5s transition to Home.
  2. `HomeScreen`: Welcome hero banner, prominent "Scan Your Crop Now" button, farmer daily advisory, and 5 service cards (*Scan Crop, Disease Detection, Treatment Advice, Scan History, Krishi AI*).
  3. `ScanCropScreen`: Leaf image upload, camera trigger, preview box, and Phase 2 notice.
  4. `HistoryScreen`: Empty state illustration, descriptive text, and "Start New Scan" CTA.
  5. `ChatbotScreen`: Krishi AI conversation interface with message bubbles, suggestion chips, input box, and placeholder replies.

---

## Phase 3 — Backend API Connection

### 1. API & Network Architecture
The Android application connects to the Python Flask backend via a modular REST API architecture:

```
Android Compose UI (HomeScreen)
        │
        ▼
ConnectionViewModel (StateFlow: Idle / Loading / Success / Error)
        │
        ▼
ApiRepository (Dispatchers.IO with Kotlin Result wrapper)
        │
        ▼
RetrofitClient (OkHttp client with 10s timeout, logging & Gson converter)
        │
        ▼ (HTTP GET / JSON)
Flask Backend Server (backend/app.py)
```

### 2. Backend Endpoints
- `GET /` — Verification text: `"AI Crop Care Backend is running"`
- `GET /api/health` — Health check JSON: `{"status": "success", "message": "AI Crop Care backend is running"}`
- `GET /api/mobile/test` — Mobile test JSON: `{"status": "success", "message": "Android API connection successful", "app": "AI CROP CARE"}`

---

## Phase 4 — Login & Register Authentication

### 1. Architecture & Flow
```
App Launch ➔ SplashScreen
                  │
        Session Check (Token in SharedPreferences)
                 / \
   [Valid Token]    [No / Invalid Token]
        │                     │
        ▼                     ▼
    HomeScreen           LoginScreen ◀───▶ RegisterScreen
        │                     │
     (Logout) ───────────────▶│
```

### 2. Backend Authentication APIs
Implemented in [`backend/app.py`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/backend/app.py) & [`backend/user_db.py`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/backend/user_db.py):

| Endpoint | Method | Request Body | Description |
| :--- | :--- | :--- | :--- |
| `/api/auth/register` | `POST` | `{"username": "...", "email": "...", "password": "..."}` | Validates unique username/email, hashes password with PBKDF2/SHA256, generates session token, returns HTTP 201. |
| `/api/auth/login` | `POST` | `{"username": "...", "password": "..."}` | Verifies credentials, generates session token, returns HTTP 200 with token. |
| `/api/auth/me` | `GET` | Header: `Authorization: Bearer <token>` | Verifies active session token, returns user information. |
| `/api/auth/logout` | `POST` | Header: `Authorization: Bearer <token>` | Invalidates session token in SQLite database, returns HTTP 200. |

### 3. SQLite User Database Schema
Located at `database/aicropcare.db`:
```sql
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auth_tokens (
    token TEXT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
```

### 4. Android Authentication Architecture
- **Data Models:** [`network/AuthModels.kt`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/android_app/app/src/main/java/com/example/aicropcare/network/AuthModels.kt) (`RegisterRequest`, `LoginRequest`, `UserDto`, `AuthResponse`).
- **API Service:** [`network/ApiService.kt`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/android_app/app/src/main/java/com/example/aicropcare/network/ApiService.kt) (`register`, `login`, `getMe`, `logout`).
- **Session Manager:** [`data/preferences/SessionManager.kt`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/android_app/app/src/main/java/com/example/aicropcare/data/preferences/SessionManager.kt) storing session token, user details, and login state.
- **Repository:** [`repository/AuthRepository.kt`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/android_app/app/src/main/java/com/example/aicropcare/repository/AuthRepository.kt) dispatching async IO network calls and parsing HTTP error responses.
- **ViewModel:** [`viewmodel/AuthViewModel.kt`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/android_app/app/src/main/java/com/example/aicropcare/viewmodel/AuthViewModel.kt) exposing `StateFlow<AuthUiState>` (`Idle`, `Loading`, `Success`, `Error`).
- **Screens:**
  - [`ui/screens/LoginScreen.kt`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/android_app/app/src/main/java/com/example/aicropcare/ui/screens/LoginScreen.kt): Clean agricultural login form with show/hide password toggle, loading spinner, and link to register.
  - [`ui/screens/RegisterScreen.kt`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/android_app/app/src/main/java/com/example/aicropcare/ui/screens/RegisterScreen.kt): Registration form with email format validation and password confirmation matching.
- **Navigation & Logout:** [`navigation/AppNavigation.kt`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/android_app/app/src/main/java/com/example/aicropcare/navigation/AppNavigation.kt) with session restoration in `SplashScreen` and logout dialog in `AppHeader`.

---

## 🧪 Testing & Build Results

### 1. Backend Authentication Tests
- Register valid user: **HTTP 201 Created**
- Register duplicate username/email: **HTTP 409 Conflict**
- Register missing fields: **HTTP 400 Bad Request**
- Login correct credentials: **HTTP 200 OK with session token**
- Login invalid credentials: **HTTP 401 Unauthorized**
- Verify session via `/api/auth/me`: **HTTP 200 OK with User data**
- Verify `/api/auth/me` without token: **HTTP 401 Unauthorized**
- Logout via `/api/auth/logout`: **HTTP 200 OK (Token Invalidated)**

### 2. Android Build Tests
- `compileDebugSources`: **BUILD SUCCESSFUL** (0 compilation errors).
- `assembleDebug`: **BUILD SUCCESSFUL** (0 errors).
- APK Location: `android_app/app/build/outputs/apk/debug/app-debug.apk` (22.6 MB).
