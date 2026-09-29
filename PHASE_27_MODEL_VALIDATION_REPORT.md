# PHASE 27 — MODEL VALIDATION REPORT
**AI CROP CARE — Machine Learning Training & Accuracy Verification**
*Date: 2026-09-21*

---

### A. DATASET SOURCE & AVAILABILITY
- **Dataset Status:** **NOT CURRENTLY AVAILABLE IN PROJECT REPOSITORY**
- **Searched Locations:**
  - `dataset/` (Not found)
  - `train/` / `val/` / `test/` (Not found)
  - `models/` (Empty)
- **Uploads Directory:** `uploads/` contains 76 temporary user-uploaded scans from previous prototype testing. Per Part 2 instructions, these were **NOT** counted or converted into a training dataset.
- **Statement:** **"Real training dataset is not currently available."**

---

### B. REQUIRED DATASET STRUCTURE FOR PHASE 27 TRAINING
To enable scientific training, the dataset must be downloaded or placed at:
`c:\Users\BRINDHA\OneDrive\Desktop\crop_disease_assistant\dataset`

Partitioning specification:
```
dataset/
├── train/   (70% of samples per class)
│   ├── Crop___Disease/
│   └── Crop___healthy/
├── val/     (15% of samples per class)
│   ├── Crop___Disease/
│   └── Crop___healthy/
└── test/    (15% of samples per class - STRICTLY UNSEEN)
    ├── Crop___Disease/
    └── Crop___healthy/
```

Classes must follow the standard `Crop___Disease` format:
- `Tomato___Bacterial_spot`
- `Tomato___Early_blight`
- `Tomato___Late_blight`
- `Tomato___healthy`
- `Rice___Bacterial_blight`
- `Rice___Leaf_blast`
- `Rice___healthy`
- etc.

---

### C. MODEL ARCHITECTURE & PIPELINE IMPLEMENTATION
- **Selected Architecture:** **MobileNetV3-Small**
  - Input resolution: $224 \times 224 \times 3$
  - Backbone: Pretrained on ImageNet with top layers replaced.
  - Head: GlobalAveragePooling2D $\rightarrow$ Dropout(0.2) $\rightarrow$ Dense(num_classes, softmax).
  - Target Quantization: INT8 / FP16 TFLite for Android on-device execution.
- **Pipeline Implementation:** [`model/train_crop_disease_model.py`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/model/train_crop_disease_model.py)
  - Incorporates automatic dataset auditing, corrupt image filtering, class frequency weighting, early stopping, and learning rate scheduling.
  - Exports trained weights directly to TFLite format upon completion.

---

### D. ACCURACY & EVALUATION STATUS
- **Training Completed:** **NO** (Blocked by missing dataset).
- **Test Completed:** **NO** (No test dataset present).
- **Reported Accuracy:** **Not yet measured.**
- **Statement:** **"100% accuracy is NOT demonstrated unless the untouched test dataset actually produces 100% accuracy."**
  - No synthetic or fabricated metrics have been produced.
  - Evaluation will strictly report empirical test accuracy, macro/weighted F1, and per-class recall once the dataset is provided.

---

### E. REAL-WORLD TESTING PROTOCOL
- Directory initialized: [`real_world_test/`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/real_world_test/)
- Reserved for field images taken across various camera sensors, lighting conditions, and partial leaf occlusions.
- Real-world images are strictly quarantined from training splits to prevent data leakage.

---

### F. PRODUCTION SAFETY & INTEGRATION
- **Gemini Vision Production Pipeline:** **100% PRESERVED & UNCHANGED.**
- The active `/api/ai/predict` endpoint in `backend/app.py` continues to run with the Phase 26 Image Quality Gate and Crop-Disease Matrix.
- No untested local models have been substituted into production.
