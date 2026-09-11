# AI CROP CARE
### AI Crop Disease Detection and Treatment Assistant

**AI CROP CARE** is a modern, farmer-friendly intelligent application engineered to help farmers detect crop diseases early, estimate infection severity, and receive actionable treatment guidance.

---

## 📌 Project Overview

- **Application Name:** AI CROP CARE
- **Subtitle:** AI Crop Disease Detection and Treatment Assistant
- **Current Development Status:** **Phase 1: Project Foundation and Basic UI**
- **Target Users:** Farmers, Agricultural Extension Workers, and Agronomists

---

## 🛠️ Technology Stack

| Layer | Technology | Status in Phase 1 |
| :--- | :--- | :--- |
| **Frontend** | HTML5, CSS3, JavaScript (ES6) | ✅ Fully implemented (5 responsive UI pages) |
| **Backend** | Python 3, Flask | ✅ Running foundation with health API |
| **AI / ML** | TensorFlow / Keras (CNN & Transfer Learning) | ⏳ Planned for Phase 3 |
| **Database** | SQLite | ⏳ Planned for Phase 2 |
| **Language Support** | English & Tamil | ⏳ Planned for Future Phase |

---

## 📂 Project Structure

```
AI-CROP-CARE/
│
├── backend/
│   ├── app.py                 # Flask server application
│   ├── requirements.txt       # Python backend dependencies
│   └── routes/                # Modular route blueprints package
│       └── __init__.py
│
├── frontend/
│   ├── index.html             # Landing page
│   ├── dashboard.html         # Farmer dashboard & quick service cards
│   ├── scan.html              # Leaf image upload & scanner UI
│   ├── history.html           # Scan history and records UI
│   ├── chatbot.html           # Krishi AI chatbot interface
│   ├── css/
│   │   └── style.css          # Agriculture-themed responsive styles
│   └── js/
│       └── app.js             # Client interactivity, preview & mock handlers
│
├── uploads/                   # Storage for user-uploaded crop leaf photos
├── models/                    # Directory for trained AI/CNN models
├── database/                  # SQLite database storage
├── README.md                  # Project documentation and setup guide
└── .gitignore                 # Standard version control ignore rules
```

---

## 🚀 How to Run the Application

### 1. Running the Flask Backend

#### Prerequisites:
- Python 3.8+ installed

#### Step-by-step:
1. Open a terminal / command prompt and navigate to the project root:
   ```bash
   cd c:\Users\BRINDHA\OneDrive\Desktop\crop_disease_assistant
   ```

2. (Optional) Activate your virtual environment:
   ```bash
   # Windows PowerShell
   .\venv\Scripts\Activate.ps1
   # or Windows Command Prompt
   .\venv\Scripts\activate.bat
   ```

3. Install minimal Phase 1 dependencies:
   ```bash
   pip install -r backend/requirements.txt
   ```

4. Start the Flask server:
   ```bash
   python backend/app.py
   ```

5. The backend will start at:
   - **Root URL:** [http://127.0.0.1:5000/](http://127.0.0.1:5000/) (Displays `"AI Crop Care Backend is running"`)
   - **Health Check API:** [http://127.0.0.1:5000/api/health](http://127.0.0.1:5000/api/health) (Returns JSON status)

---

### 2. Running & Viewing the Frontend

You can run the frontend in either of two easy ways:

#### Method A: Direct Browser (No server required)
Double-click or open any HTML file directly in your browser:
- `frontend/index.html` — Landing Page
- `frontend/dashboard.html` — Farmer Dashboard
- `frontend/scan.html` — Scan Crop Page
- `frontend/history.html` — Scan History Page
- `frontend/chatbot.html` — Krishi AI Chatbot Page

#### Method B: Served via Flask Server
While `backend/app.py` is running, access:
- [http://127.0.0.1:5000/frontend/index.html](http://127.0.0.1:5000/frontend/index.html)
- [http://127.0.0.1:5000/frontend/dashboard.html](http://127.0.0.1:5000/frontend/dashboard.html)
- [http://127.0.0.1:5000/frontend/scan.html](http://127.0.0.1:5000/frontend/scan.html)
- [http://127.0.0.1:5000/frontend/history.html](http://127.0.0.1:5000/frontend/history.html)
- [http://127.0.0.1:5000/frontend/chatbot.html](http://127.0.0.1:5000/frontend/chatbot.html)

---

## 📋 Phase Roadmap

- [x] **Phase 1: Project Foundation and Basic UI** *(Current)*
  - Modern agricultural theme and responsive UI design
  - Landing page, Dashboard, Scan UI, History placeholder, Krishi AI Chatbot UI
  - Flask backend with health endpoints
  - Project directory structure and documentation
- [ ] **Phase 2: User Authentication & Database Integration (SQLite)**
- [ ] **Phase 3: AI Crop Disease Detection Model (TensorFlow/Keras CNN)**
- [ ] **Phase 4: Treatment Advisory Engine & Damage Estimation**
- [ ] **Phase 5: Full AI Chatbot Integration & Bilingual Support (English & Tamil)**
