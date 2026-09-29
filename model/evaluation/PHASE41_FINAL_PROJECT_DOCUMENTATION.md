# AI CROP CARE — FINAL PROJECT DOCUMENTATION

## Project Title
AI CROP CARE

## Project Overview
The AI Crop Care system helps users identify crop and leaf diseases using AI-based image analysis and provides treatment and advisory information. It is designed as a native Android application that connects to a cloud-based AI backend to deliver fast, reliable, and preliminary agricultural diagnosis and expert advisory.

## Problem Statement
Farmers and agricultural workers face significant difficulty identifying crop diseases manually in the field. There is an urgent need for faster preliminary identification of plant ailments and a need for accessible treatment and advisory information. Traditional manual inspection has limitations, as it requires highly specialized knowledge, takes time, and can result in crop losses if diseases spread while awaiting expert consultation.

## Proposed Solution
AI CROP CARE provides a smart, accessible mobile solution that allows users to:
- Capture new crop images directly or select existing images from their gallery.
- Submit images for AI analysis to a specialized backend.
- Receive immediate preliminary disease information.
- View confidence and severity information where available.
- Receive actionable treatment advice and safety precautions.
- Save their scan history locally for offline reference.
- Communicate with an AI chatbot (Krishi AI) to ask follow-up agricultural questions.

## Objectives
- To develop a user-friendly Android application for farmers to capture crop conditions.
- To integrate a robust cloud-based AI vision engine for preliminary disease diagnosis.
- To provide actionable treatment and advisory information based on the identified disease.
- To enable an interactive AI chatbot for general farming assistance.
- To ensure local data privacy by storing scan history on the device.

## Major Features
- **User Authentication:** Local session-based login and registration.
- **Dashboard:** A central hub to access all core services.
- **Camera Capture & Gallery Upload:** Native integration for capturing or selecting crop images.
- **AI Disease Detection:** AI-driven analysis of uploaded leaf/crop images.
- **Disease Result & Treatment Recommendation:** Display of the identified disease alongside cultural management, recommended products, and safety precautions.
- **Scan History:** Local preservation of previous diagnoses.
- **Chatbot:** Krishi AI integration for dynamic agricultural queries.
- **Multilingual Support:** Support for English and Tamil.
- **Farming Reminders:** Scheduling and tracking agricultural tasks.
- **Crop Health/Progress:** Tracking statistics for scanned crops.

---

## Technology Stack

| Category | Technology | Purpose |
|----------|------------|---------|
| **Android UI** | Kotlin & Jetpack Compose | Core programming and declarative UI building |
| **Design System** | Material 3 | Consistent, modern, and accessible design components |
| **Camera** | CameraX | Native high-quality image capture |
| **Networking** | Retrofit & OkHttp | REST API communication with the backend |
| **Image Loading** | Coil | Efficient asynchronous image loading and caching |
| **Database** | SQLite | Local on-device storage for scan history |
| **Backend** | Python Flask | API server bridging the app and the AI engine |
| **AI Engine** | Gemini Vision | Advanced cloud-based image analysis and NLP |
| **Deployment** | Render | Production hosting for the Flask backend |

---

## System Architecture

**Data Flow:**
1. The user captures or selects an image via the **Android UI**.
2. The image is compressed and sent via a **Retrofit API request** over HTTPS.
3. The **Flask backend** receives the image and securely forwards it to the **Gemini Vision** API along with specialized agricultural prompts.
4. Gemini Vision analyzes the image and returns a **Structured JSON response** containing the disease name, severity, and treatment plan.
5. The Flask backend forwards this JSON back to the Android app.
6. The Android app displays the **Result Screen** and **Treatment Recommendation**.
7. The result is automatically saved to the **Local SQLite history** for future reference.

**Architecture Diagram:**
```text
+-------------------+       HTTPS        +---------------------+
|   Android App     | -----------------> |    Flask Backend    |
| (Jetpack Compose) |                    |  (REST API Server)  |
|                   | <----------------- |                     |
+-------------------+                    +---------------------+
          |                                         |
          |                                         | HTTPS
          v                                         v
+-------------------+                    +---------------------+
| Local SQLite DB   |                    | Gemini Vision API   |
|  (Scan History)   |                    | (Disease Detection) |
+-------------------+                    +---------------------+
```

---

## Module Description
- **Splash:** Initial loading screen that verifies session state and routes the user.
- **Authentication:** Handles user login and registration securely using session management.
- **Home/Dashboard:** Provides quick navigation to Scan, History, Chatbot, Reminders, and Health Progress.
- **Camera/Gallery:** Integrates CameraX for live capture and the Android Photo Picker for existing images.
- **Disease Detection:** Uploads the image and displays a loading state while awaiting backend analysis.
- **Result:** Displays the parsed JSON response including disease name, severity, and confidence.
- **Treatment:** Detailed breakdown of cultural management, products to use, things to avoid, and safety precautions.
- **History:** Displays a chronological list of past scans stored in SQLite.
- **Chatbot:** An interactive chat interface communicating with the backend `/api/chatbot` endpoint.
- **Reminders:** Allows users to schedule farming tasks (e.g., irrigation, inspection).
- **Crop Health:** Provides summary statistics and visual progress of diagnosed crops.
- **Settings/Language:** Allows toggling between English and Tamil localized strings.

---

## Gemini AI Explanation
**Why Gemini Vision is used:** Gemini Vision provides state-of-the-art multimodal capabilities, meaning it can analyze both an image (the crop leaf) and a complex text prompt simultaneously to return highly accurate, structured agricultural advice.
**How it works:** The Android app sends the image over HTTPS to the Flask backend. The backend securely holds the API key and forwards the image to Gemini. The prompt strictly instructs Gemini to return a JSON object (disease name, severity, treatment, etc.). The backend relays this JSON to the app.
**Display:** Android parses this JSON using Gson and renders it beautifully into distinct UI cards (e.g., green for safety, red for things to avoid).
**Note:** Gemini acts as a powerful preliminary advisory tool, but it is not 100% infallible; users are advised to verify severe issues with local experts.

---

## Backend Explanation
The Python Flask REST API serves as a secure middleware.
- **Endpoints:** `/api/ai/predict` receives multipart image uploads, and `/api/chatbot` receives chat history arrays.
- **Flow:** It validates requests, handles Gemini API communication, enforces JSON schemas, and catches errors, returning standard HTTP codes (200 OK, 400/500 Errors).
- **Deployment:** It is deployed on Render, providing a secure, always-on HTTPS endpoint.

---

## Database Explanation
The application uses an **Android SQLite database** for local history storage.
- **Why local:** It ensures privacy, requires no cloud database costs, and allows farmers to view their past scan results even when offline.
- **Stored Information:** It stores the disease name, timestamp, severity, treatment data, and the local file path to the scanned image.
- **Retrieval:** Users access this via the "Scan History" module, which queries the SQLite database and displays the records in a Jetpack Compose list.

---

## Security
- **HTTPS:** All communication between Android and the Flask backend is encrypted via HTTPS.
- **No Hardcoded API Keys:** The Gemini API key is completely isolated on the server-side environment variables (Render). The Android app contains zero API keys.
- **Obfuscation:** The release build uses R8/ProGuard minification to obfuscate code and remove unused resources.
- **Authentication:** Session tokens/preferences manage user login state.
- **Safe APK:** The final APK contains no credentials or debug endpoints.

---

## Testing and Verification
The system underwent rigorous phase-based verification:
- **Phase 34 (Android Production Audit):** PASSED.
- **Phase 35 (Physical-device testing):** BLOCKED because no physical device was connected to the automated test environment.
- **Phase 35B (Emulator testing):** BLOCKED because the emulator environment could not be provisioned natively.
- **Phase 36 (API Integration):** PASSED. Verified strict Retrofit to Flask contracts.
- **Phase 37 (Production Security Hardening):** PASSED. Secrets wiped, minification enabled, cleartext traffic disabled.
- **Phase 38 (Production Functional Verification):** PASSED.
- **Phase 39 (UI/UX Polish):** PASSED.
- **Phase 40 (Final APK Preparation):** PASSED.
*(Note: Blocked physical/emulator tests reflect test-environment constraints, not application failures. All PC-side compilations and integration verifications passed perfectly.)*

---

## Final APK Information
This section uniquely identifies the verified production build of AI Crop Care.
- **Filename:** `AI_Crop_Care.apk`
- **Path:** `release/AI_Crop_Care.apk`
- **Package:** `com.example.aicropcare`
- **Version:** `1.0 (1)`
- **Size:** `3,958,878 bytes` (~3.96 MB)
- **SHA-256:** `39B9BA050153123861173970AA8F731850363E9B4D301092C782EE077F324DBF`

---

## Research Note
The AI Crop Care project repository contains separate experimental machine learning research (Phase 33), including custom-trained MobileNetV3 architectures on a protected Out-Of-Distribution (OOD) dataset.
**Crucially:** THE RESEARCH MOBILENET MODEL IS NOT THE PRODUCTION DIAGNOSIS ENGINE.
The production Android application is strictly integrated with the Gemini Vision architecture via the Flask backend. The MobileNet research remains safely isolated for separate academic evaluation and is not deployed in the live APK.

---

## Viva Questions and Answers

### A. Basic Project Questions
**1. QUESTION: What is AI Crop Care?**
SIMPLE ANSWER: It is an Android app that uses AI to detect crop diseases from photos and gives farmers treatment advice.

**2. QUESTION: Why did you choose this project?**
SIMPLE ANSWER: Because manual crop disease detection is slow and hard for farmers, leading to crop loss. This app makes preliminary diagnosis fast and accessible.

**3. QUESTION: What problem does it solve?**
SIMPLE ANSWER: It solves the lack of immediate agricultural expertise by providing instant AI-based disease identification and treatment steps.

**4. QUESTION: What technologies did you use?**
SIMPLE ANSWER: Kotlin, Jetpack Compose, CameraX for Android; Python, Flask for the backend; Gemini Vision for AI; and SQLite for the local database.

**5. QUESTION: Explain your project in one minute.**
SIMPLE ANSWER: (See One-Minute Project Explanation below).

### B. Android Questions
**6. QUESTION: Why did you use Android instead of a website?**
SIMPLE ANSWER: Because farmers take photos in the field using their mobile phones, making an Android app much more practical.

**7. QUESTION: What is CameraX?**
SIMPLE ANSWER: CameraX is an Android Jetpack library that makes it easy to add high-quality camera features to the app.

**8. QUESTION: What is Material 3?**
SIMPLE ANSWER: It is Google's latest design system that provides modern, accessible UI components like buttons and cards.

**9. QUESTION: How do you load images in the app?**
SIMPLE ANSWER: I use Coil, which is a fast, lightweight image loading library for Android backed by Kotlin Coroutines.

### C. Kotlin Questions
**10. QUESTION: Why Kotlin instead of Java?**
SIMPLE ANSWER: Kotlin is modern, concise, null-safe, and is the officially recommended language by Google for Android development.

**11. QUESTION: What are Kotlin Coroutines?**
SIMPLE ANSWER: They are lightweight threads used to perform background tasks like network calls without freezing the app UI.

### D. Jetpack Compose Questions
**12. QUESTION: Why Jetpack Compose instead of XML?**
SIMPLE ANSWER: Jetpack Compose is a modern declarative UI toolkit that makes building Android UI faster and with less code than traditional XML layouts.

**13. QUESTION: What is a Composable function?**
SIMPLE ANSWER: It is a Kotlin function annotated with `@Composable` that defines a piece of the user interface.

**14. QUESTION: What is State in Jetpack Compose?**
SIMPLE ANSWER: State represents data that can change over time. When state changes, Compose automatically updates the UI.

### E. Backend Questions
**15. QUESTION: What is the difference between frontend and backend?**
SIMPLE ANSWER: The frontend is the Android app the user interacts with. The backend is the server that processes logic and talks to the AI.

**16. QUESTION: What is a REST API?**
SIMPLE ANSWER: It is a set of rules that allows the Android app to communicate with the Flask server over HTTP using methods like GET and POST.

**17. QUESTION: What is JSON?**
SIMPLE ANSWER: JSON is a lightweight text format used to send structured data, like the disease name and treatment, between the server and the app.

**18. QUESTION: How does Android communicate with Flask?**
SIMPLE ANSWER: Android uses a library called Retrofit to send HTTP requests to the Flask backend.

**19. QUESTION: What is Retrofit?**
SIMPLE ANSWER: Retrofit is a type-safe HTTP client for Android that makes it easy to consume REST APIs.

### F. Flask Questions
**20. QUESTION: Why Flask?**
SIMPLE ANSWER: Flask is a lightweight Python web framework. It is fast to set up and perfectly suited for creating simple REST APIs for AI integration.

**21. QUESTION: What is the role of Render?**
SIMPLE ANSWER: Render is the cloud platform used to host and deploy the Flask backend so the app can access it over the internet via HTTPS.

### G. AI/Gemini Questions
**22. QUESTION: Why Gemini Vision?**
SIMPLE ANSWER: Gemini Vision is highly advanced, capable of analyzing both images and text prompts to provide accurate, structured JSON responses for agriculture.

**23. QUESTION: How does image diagnosis work?**
SIMPLE ANSWER: The app sends the image to Flask, Flask sends it to Gemini, Gemini analyzes it and returns a JSON response, which Flask sends back to the app.

**24. QUESTION: What happens after the user uploads an image?**
SIMPLE ANSWER: A loading screen appears, the image goes to the backend, AI processes it, and then the app shows the disease name and treatment plan.

**25. QUESTION: What happens if the image cannot be analyzed?**
SIMPLE ANSWER: The backend catches the error and returns a failure message, and the Android app displays a clean error state asking the user to try again.

### H. Database Questions
**26. QUESTION: What is SQLite?**
SIMPLE ANSWER: SQLite is a lightweight, local database engine built directly into the Android operating system.

**27. QUESTION: Why is scan history stored locally?**
SIMPLE ANSWER: To protect user privacy, save cloud database costs, and allow farmers to view their past results offline.

**28. QUESTION: What data is stored in the database?**
SIMPLE ANSWER: It stores the disease name, timestamp, severity, treatment text, and the local file path to the scanned image.

### I. Security Questions
**29. QUESTION: How is the application secured?**
SIMPLE ANSWER: Using HTTPS for all network traffic, local session management, and ProGuard/R8 minification for the release APK.

**30. QUESTION: Where is the Gemini API key stored?**
SIMPLE ANSWER: It is stored securely as an environment variable on the backend server (Render).

**31. QUESTION: Is the API key stored inside the APK?**
SIMPLE ANSWER: No, the Android app contains no API keys. This prevents hackers from stealing the key by reverse-engineering the app.

**32. QUESTION: What is authentication?**
SIMPLE ANSWER: It is the process of verifying a user's identity, allowing them to log in and securely access the app's features.

**33. QUESTION: What is session/token management?**
SIMPLE ANSWER: It is how the app remembers that a user is logged in even if they close and reopen the app.

### J. Testing Questions
**34. QUESTION: What did you test?**
SIMPLE ANSWER: I tested UI layout rendering, Retrofit API integration, Flask backend health, and the final Release APK compilation.

**35. QUESTION: Why was physical-device testing blocked?**
SIMPLE ANSWER: Because a physical Android phone was not connected to the automated test environment.

**36. QUESTION: Why was emulator testing blocked?**
SIMPLE ANSWER: The test environment could not download or provision the necessary Android system images.

**37. QUESTION: What is MobileNet?**
SIMPLE ANSWER: MobileNet is a lightweight machine learning architecture designed for mobile devices.

**38. QUESTION: Is MobileNet used in your production application?**
SIMPLE ANSWER: No. The production app uses Gemini Vision. MobileNet was explored only as an isolated academic research experiment.

**39. QUESTION: What is the final APK?**
SIMPLE ANSWER: It is the compiled Android Package file (AI_Crop_Care.apk) that users install on their phones.

**40. QUESTION: How do you verify the APK?**
SIMPLE ANSWER: I verify its package name, version, file size, and calculate its SHA-256 hash to prove it is the exact unchanged build.

**41. QUESTION: What is SHA-256?**
SIMPLE ANSWER: It is a cryptographic hashing algorithm that creates a unique digital fingerprint for a file, proving its integrity.

---

## One-Minute Project Explanation
"Hello, my project is **AI Crop Care**. The problem we are addressing is that farmers often struggle to identify crop diseases quickly, which can lead to severe crop loss. 
Our solution is a mobile Android application built using Kotlin and Jetpack Compose. A user can take a photo of a sick plant using the app's CameraX feature. 
The app sends this image securely via a Python Flask backend to a powerful AI called Gemini Vision. Gemini analyzes the leaf and instantly returns the disease name, severity, and a step-by-step treatment plan. 
The app then displays this to the farmer and saves the result in a local SQLite database for offline viewing. We also included an AI chatbot for general farming questions. 
Ultimately, this project empowers farmers with instant, accessible agricultural expertise right in their pockets."

---

## 5-Minute Project Presentation Script

**1. Introduction**
"Good morning respected examiners. Our project is titled 'AI Crop Care', an intelligent mobile application designed to assist farmers and agricultural workers."

**2. Problem & Existing Approach**
"Currently, when crops fall ill, farmers must rely on manual inspection or wait for an agricultural expert to visit. This traditional approach is slow, error-prone, and highly inaccessible in rural areas, often leading to rapid disease spread and financial loss."

**3. Proposed Solution & Features**
"To solve this, we developed a smart Android application. It allows users to instantly take a picture of a crop leaf. The app analyzes the image, detects the exact disease, provides severity metrics, and most importantly, offers a detailed, actionable treatment plan. We also included a scan history database and a farming chatbot."

**4. Architecture & Technologies**
"Our architecture is cleanly separated. The frontend is a native Android app built with Kotlin and Jetpack Compose for a modern UI. When an image is captured via CameraX, it is sent over HTTPS using Retrofit to our backend.
Our backend is a Python Flask REST API deployed on Render. The backend acts as a secure bridge, forwarding the image to the Gemini Vision AI API. Gemini returns a structured JSON diagnosis, which the app parses and displays. Finally, the scan is saved to a local SQLite database on the phone."

**5. Demo Flow (Verbal)**
"If we were to open the app, you'd see a dashboard. You click 'Scan Crop', snap a photo, and hit submit. Within seconds, the screen transitions to a detailed result page showing the disease, safety precautions, and recommended actions."

**6. Security & Testing**
"Security is paramount. The Gemini API key is never stored on the mobile device—it lives entirely on the backend server. We thoroughly tested our UI rendering, our Retrofit integration contracts, and our backend health, culminating in a highly optimized and minified Release APK."

**7. Conclusion**
"In conclusion, AI Crop Care bridges the gap between advanced artificial intelligence and everyday agriculture, providing a fast, secure, and user-friendly tool to help secure crop yields. Thank you, I am happy to answer any questions."

---

## Limitations
- **Environment Limitations:** Physical Android-device testing and emulator testing were blocked by unavailable test environments during development. Testing was constrained to robust codebase integration audits and backend connectivity verifications.
- **AI Advisory Nature:** AI-generated disease advice should be treated as preliminary guidance. The system is an assistive tool, not an absolute authority, and users should seek appropriate agricultural expertise where necessary. No medical claims are made regarding chemical treatments.
- **Network Dependency:** The core diagnosis feature requires an active internet connection to communicate with the Flask backend.

---

## Future Scope
- **Larger Real-World Datasets:** Training internal models on much larger, region-specific agricultural image sets.
- **Improved Field-Condition Robustness:** Enhancing the AI to handle extreme lighting, blur, and diverse background noise.
- **Offline Assistance:** Exploring technically feasible on-device offline models for areas with zero connectivity.
- **More Crop Coverage:** Expanding the taxonomy of supported plants and diseases.
- **Better Advisory Integration:** Linking the app directly to local agricultural authorities or chemical vendors.

---

## Final Conclusion
AI CROP CARE successfully demonstrates the integration of advanced multimodal AI within a mobile ecosystem to solve real-world agricultural problems. By combining the accessibility of a native Kotlin Android application with the analytical power of Gemini Vision via a secure Python Flask backend, the system delivers rapid crop disease identification and treatment advisory support. With features like local SQLite history and an interactive chatbot, the project lays a strong foundation for future improvements in smart farming technology.
