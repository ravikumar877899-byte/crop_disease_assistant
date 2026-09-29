# AI CROP CARE — DATASET COMPATIBILITY & FEASIBILITY REPORT
**Phase 27A: Cross-Dataset Alignment Analysis (PlantVillage, PlantDoc, Paddy Doctor)**
*Date: 2026-09-21*

---

### EXECUTIVE SUMMARY
AI CROP CARE currently defines **28 target crop/disease classes** in `model/class_indices.json` across 16 agricultural crops (Tomato, Potato, Rice, Corn, Grape, Apple, Banana, Mango, Coffee, Rubber, Tea, Sugarcane, Wheat, Citrus, Cotton, Soybean).

This audit evaluates the feasibility of mapping these classes to the 3 prominent publicly documented agricultural computer vision datasets:
1. **PlantVillage** (Hughes & Salathé, 2015) — 54,303 lab/controlled condition leaf photos, 38 classes across 14 crops.
2. **PlantDoc** (Singh et al., 2019) — 2,598 real-world, in-field, multi-leaf images across 27 classes and 13 crops.
3. **Paddy Doctor** (P. et al., 2022 / CVPR Workshop) — 16,225 real-world field images collected from paddy farms near Tirunelveli, Tamil Nadu across 10 rice-specific disease/pest classes and healthy rice.

---

### A. CURRENT AI CROP CARE CLASSES (`model/class_indices.json`)
The current project index contains 28 entries:
1. **Apple**: Apple Scab, Apple Black Rot, Apple Cedar Rust
2. **Corn (Maize)**: Corn Common Rust, Corn Gray Leaf Spot
3. **Grape**: Grape Black Rot, Grape Esca (Black Measles), Grape Leaf Blight
4. **Potato**: Potato Early Blight, Potato Late Blight, Potato Black Scurf
5. **Rice**: Rice Bacterial Blight, Rice Leaf Blast
6. **Tomato**: Tomato Bacterial Spot, Tomato Early Blight, Tomato Yellow Leaf Curl Virus
7. **Banana**: Banana Black Sigatoka, Healthy Banana Leaf
8. **Mango**: Mango Anthracnose, Mango Mealybug
9. **Coffee**: Coffee Leaf Rust
10. **Rubber**: Rubber Leaf Spot
11. **Tea**: Tea Blister Blight
12. **Sugarcane**: Sugarcane Red Rot
13. **Wheat**: Wheat Leaf Rust
14. **Citrus**: Citrus Canker
15. **Cotton**: Cotton Boll Rot
16. **Soybean**: Soybean Rust

---

### B. OFFICIAL PLANTVILLAGE DATASET CLASSES (38 Classes)
The standard 38 benchmark classes in PlantVillage:
- `Apple___Apple_scab`
- `Apple___Black_rot`
- `Apple___Cedar_apple_rust`
- `Apple___healthy`
- `Blueberry___healthy`
- `Cherry_(including_sour)___Powdery_mildew`
- `Cherry_(including_sour)___healthy`
- `Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot`
- `Corn_(maize)___Common_rust_`
- `Corn_(maize)___Northern_Leaf_Blight`
- `Corn_(maize)___healthy`
- `Grape___Black_rot`
- `Grape___Esca_(Black_Measles)`
- `Grape___Leaf_blight_(Isariopsis_Leaf_Spot)`
- `Grape___healthy`
- `Orange___Haunglongbing_(Citrus_greening)`
- `Peach___Bacterial_spot`
- `Peach___healthy`
- `Pepper,_bell___Bacterial_spot`
- `Pepper,_bell___healthy`
- `Potato___Early_blight`
- `Potato___Late_blight`
- `Potato___healthy`
- `Raspberry___healthy`
- `Soybean___healthy`
- `Squash___Powdery_mildew`
- `Strawberry___Leaf_scorch`
- `Strawberry___healthy`
- `Tomato___Bacterial_spot`
- `Tomato___Early_blight`
- `Tomato___Late_blight`
- `Tomato___Leaf_Mold`
- `Tomato___Septoria_leaf_spot`
- `Tomato___Spider_mites Two-spotted_spider_mite`
- `Tomato___Target_Spot`
- `Tomato___Tomato_Yellow_Leaf_Curl_Virus`
- `Tomato___Tomato_mosaic_virus`
- `Tomato___healthy`

---

### C. EXACT MATCHES (AI Crop Care $\leftrightarrow$ PlantVillage)
These classes exist verbatim in PlantVillage:
1. `Apple___Apple_scab` $\longleftrightarrow$ `Apple Scab`
2. `Apple___Black_rot` $\longleftrightarrow$ `Apple Black Rot`
3. `Apple___Cedar_apple_rust` $\longleftrightarrow$ `Apple Cedar Rust`
4. `Grape___Black_rot` $\longleftrightarrow$ `Grape Black Rot`
5. `Grape___Esca_(Black_Measles)` $\longleftrightarrow$ `Grape Esca (Black Measles)`
6. `Potato___Early_blight` $\longleftrightarrow$ `Potato Early Blight`
7. `Potato___Late_blight` $\longleftrightarrow$ `Potato Late Blight`
8. `Tomato___Bacterial_spot` $\longleftrightarrow$ `Tomato Bacterial Spot`
9. `Tomato___Early_blight` $\longleftrightarrow$ `Tomato Early Blight`
10. `Tomato___Tomato_Yellow_Leaf_Curl_Virus` $\longleftrightarrow$ `Tomato Yellow Leaf Curl Virus`

---

### D. CLASSES THAT CAN BE SAFELY MAPPED
Minor naming/spacing variations that refer to the exact same biological pathogen:
1. `Corn Common Rust`: PlantVillage label is `Corn_(maize)___Common_rust_` (Pathogen: *Puccinia sorghi*).
2. `Corn Gray Leaf Spot`: PlantVillage label is `Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot` (Pathogen: *Cercospora zeae-maydis*).
3. `Grape Leaf Blight`: PlantVillage label is `Grape___Leaf_blight_(Isariopsis_Leaf_Spot)` (Pathogen: *Pseudocercospora vitis*).

---

### E. AI CROP CARE CLASSES NOT PRESENT IN PLANTVILLAGE
> [!IMPORTANT]
> PlantVillage **DOES NOT CONTAIN** Rice, Banana, Cotton, Sugarcane, Mango, Coffee, Rubber, or Tea.
> Any claim that PlantVillage can train the full AI Crop Care catalog is scientifically false.

Specifically missing from PlantVillage:
1. **Rice Bacterial Blight** (*Xanthomonas oryzae*)
2. **Rice Leaf Blast** (*Magnaporthe oryzae*)
3. **Banana Black Sigatoka** (*Pseudocercospora fijiensis*)
4. **Healthy Banana Leaf**
5. **Mango Anthracnose** (*Colletotrichum gloeosporioides*)
6. **Mango Mealybug** (*Drosicha mangiferae*)
7. **Coffee Leaf Rust** (*Hemileia vastatrix*)
8. **Rubber Leaf Spot** (*Corynespora cassiicola*)
9. **Tea Blister Blight** (*Exobasidium vexans*)
10. **Sugarcane Red Rot** (*Colletotrichum falcatum*)
11. **Wheat Leaf Rust** (*Puccinia triticina*)
12. **Potato Black Scurf** (*Rhizoctonia solani* — PlantVillage only has Early/Late blight)
13. **Citrus Canker** (*Xanthomonas citri* — PlantVillage only has Huanglongbing/Greening in Orange)
14. **Cotton Boll Rot**
15. **Soybean Rust** (*Phakopsora pachyrhizi* — PlantVillage only has `Soybean___healthy`)

---

### F. PLANTVILLAGE CLASSES NOT CURRENTLY NEEDED BY AI CROP CARE
PlantVillage contains 25 additional classes not in `model/class_indices.json`:
- Blueberry (healthy), Cherry (powdery mildew, healthy), Peach (bacterial spot, healthy), Bell Pepper (bacterial spot, healthy), Raspberry (healthy), Squash (powdery mildew), Strawberry (leaf scorch, healthy).
- Tomato classes not in AI Crop Care index: `Tomato___Leaf_Mold`, `Tomato___Septoria_leaf_spot`, `Tomato___Spider_mites`, `Tomato___Target_Spot`, `Tomato___Tomato_mosaic_virus`, `Tomato___healthy`.
- Corn: `Northern_Leaf_Blight`, `Corn___healthy`.
- Apple: `Apple___healthy`.
- Grape: `Grape___healthy`.
- Orange: `Haunglongbing`.

---

### G. EVALUATION OF CANDIDATE DATASETS

#### 1. PlantVillage
- **Sample Size:** 54,303 images across 38 classes.
- **Crops:** 14 (Apple, Blueberry, Cherry, Corn, Grape, Orange, Peach, Bell Pepper, Potato, Raspberry, Soybean, Squash, Strawberry, Tomato).
- **Format / Conditions:** Controlled laboratory lighting, uniform background (plain paper/sheet).
- **Relevance:** High for standard horticultural crops (Tomato, Potato, Corn, Apple, Grape).
- **Role:** **Primary training foundation for horticultural baseline.**
- **Licensing:** CC0 / Open Access.

#### 2. PlantDoc (IIT Bombay & Research Community)
- **Sample Size:** 2,598 images across 27 classes, 13 crop species.
- **Format / Conditions:** Real-world field conditions, variable natural daylight, complex soil/leaf clutter backgrounds.
- **Relevance:** Very high for checking real-world domain generalization and robustness against clutter.
- **Role:** **Independent real-world validation benchmark for horticultural classes.**
- **Licensing:** Open Research.

#### 3. Paddy Doctor (Kaggle CVPR Workshop Benchmark)
- **Sample Size:** 16,225 high-resolution mobile photos (10,407 train, 3,469 test).
- **Crops:** **Rice / Paddy exclusively.**
- **Geographic Relevance:** Captured directly in paddy fields in Tirunelveli district, Tamil Nadu, India.
- **Classes (10 disease/pest classes + 1 healthy):**
  - Bacterial leaf blight (Matches AI Crop Care index)
  - Blast (Matches AI Crop Care index)
  - Brown spot
  - Tungro
  - Hispa
  - Leaf roller
  - Downy mildew
  - Bacterial leaf streak
  - Dead heart
  - Normal (Healthy Rice)
- **Relevance:** **CRITICAL for AI Crop Care Tamil Nadu / Indian farmer focus.** Completely solves the missing Rice disease dataset in PlantVillage.
- **Role:** **Primary training & validation set for Rice diagnostics.**
- **Licensing:** CC BY-NC-SA 4.0 (Academic / Non-commercial research).

---

### H. COMPLETE CLASS ALIGNMENT TABLE

| AI CROP CARE Class | Best Matching Dataset | Dataset Class Label | Alignment Status | Role in Training Pipeline |
| :--- | :--- | :--- | :--- | :--- |
| **Apple Scab** | PlantVillage | `Apple___Apple_scab` | EXACT MATCH | Train + Val Split |
| **Apple Black Rot** | PlantVillage | `Apple___Black_rot` | EXACT MATCH | Train + Val Split |
| **Apple Cedar Rust** | PlantVillage | `Apple___Cedar_apple_rust` | EXACT MATCH | Train + Val Split |
| **Corn Common Rust** | PlantVillage | `Corn_(maize)___Common_rust_` | SAFE MAP | Train + Val Split |
| **Corn Gray Leaf Spot** | PlantVillage | `Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot` | SAFE MAP | Train + Val Split |
| **Grape Black Rot** | PlantVillage | `Grape___Black_rot` | EXACT MATCH | Train + Val Split |
| **Grape Esca (Black Measles)** | PlantVillage | `Grape___Esca_(Black_Measles)` | EXACT MATCH | Train + Val Split |
| **Grape Leaf Blight** | PlantVillage | `Grape___Leaf_blight_(Isariopsis_Leaf_Spot)` | SAFE MAP | Train + Val Split |
| **Potato Early Blight** | PlantVillage / PlantDoc | `Potato___Early_blight` | EXACT MATCH | Train (PV) + Out-of-dist Val (PlantDoc) |
| **Potato Late Blight** | PlantVillage / PlantDoc | `Potato___Late_blight` | EXACT MATCH | Train (PV) + Out-of-dist Val (PlantDoc) |
| **Potato Black Scurf** | Specialized / Local | None in PV/PlantDoc | NOT PRESENT | Needs dedicated tuber dataset or defer |
| **Rice Bacterial Blight** | Paddy Doctor | `bacterial_leaf_blight` | EXACT MATCH | Train + Test (Paddy Doctor) |
| **Rice Leaf Blast** | Paddy Doctor | `blast` | EXACT MATCH | Train + Test (Paddy Doctor) |
| **Tomato Bacterial Spot** | PlantVillage / PlantDoc | `Tomato___Bacterial_spot` | EXACT MATCH | Train (PV) + Out-of-dist Val (PlantDoc) |
| **Tomato Early Blight** | PlantVillage / PlantDoc | `Tomato___Early_blight` | EXACT MATCH | Train (PV) + Out-of-dist Val (PlantDoc) |
| **Tomato Yellow Leaf Curl Virus**| PlantVillage / PlantDoc | `Tomato___Tomato_Yellow_Leaf_Curl_Virus` | EXACT MATCH | Train (PV) + Out-of-dist Val (PlantDoc) |
| **Banana Black Sigatoka** | Mendeley Banana / Kaggle | `Banana_Black_Sigatoka` | NOT IN PV | Requires Mendeley Banana Leaf Dataset |
| **Healthy Banana Leaf** | Mendeley Banana / Kaggle | `Banana_Healthy` | NOT IN PV | Requires Mendeley Banana Leaf Dataset |
| **Mango Anthracnose** | Mendeley Mango Leaf | `Anthracnose` | NOT IN PV | Requires Indian Mango Leaf Dataset |
| **Mango Mealybug** | Agricultural Board | Pest symptom | NOT IN PV | Defer to Gemini vision layer |
| **Coffee Leaf Rust** | BrFLD / RoCoLe | `Rust` | NOT IN PV | Requires RoCoLe Coffee Leaf Dataset |
| **Rubber Leaf Spot** | Rubber Research Inst. | `Corynespora` | NOT IN PV | Defer to Gemini vision layer |
| **Tea Blister Blight** | Tea Research Foundation | `Blister_Blight` | NOT IN PV | Defer to Gemini vision layer |
| **Sugarcane Red Rot** | Kaggle Sugarcane | `Red_Rot` | NOT IN PV | Requires Sugarcane Disease Dataset |
| **Wheat Leaf Rust** | CGIAR / Wheat Rust Atlas| `Leaf_Rust` | NOT IN PV | Requires CGIAR Wheat Dataset |
| **Citrus Canker** | Citrus Disease Dataset | `Citrus_Canker` | NOT IN PV | Requires Citrus Disease Image Repository |
| **Cotton Boll Rot** | Cotton Disease Dataset | `Boll_Rot` | NOT IN PV | Defer or require Cotton Leaf dataset |
| **Soybean Rust** | Soybean Disease Dataset | `Soybean_Rust` | NOT IN PV | PlantVillage has only `healthy` |

---

### I. SCRIPT FOLDER NAMING COMPATIBILITY
The training script [`model/train_crop_disease_model.py`](file:///c:/Users/BRINDHA/OneDrive/Desktop/crop_disease_assistant/model/train_crop_disease_model.py) uses `tf.keras.preprocessing.image_dataset_from_directory()`.

- **Expected Format:**
  `dataset/<split>/<Crop>___<Disease>/image.jpg`
- **Separator:** Triple underscore (`___`).
- **Conversion Utility Needed:**
  When converting Paddy Doctor (which uses plain lower-case names like `bacterial_leaf_blight`, `blast`, `normal`), a short normalization script must rename folders to `Rice___Bacterial_blight`, `Rice___Leaf_blast`, and `Rice___healthy`.

---

### J. RECOMMENDED DATASET PLAN

#### Version 1 (V1 — Scientific Benchmark Model: 16 Core Classes)
Train the first verified MobileNetV3 model on the **16 classes that have established, verified, high-volume open datasets**:
- **Tomato (4 classes):** `Tomato___Bacterial_spot`, `Tomato___Early_blight`, `Tomato___Tomato_Yellow_Leaf_Curl_Virus`, `Tomato___healthy` (Source: PlantVillage + PlantDoc test)
- **Potato (3 classes):** `Potato___Early_blight`, `Potato___Late_blight`, `Potato___healthy` (Source: PlantVillage + PlantDoc test)
- **Corn (3 classes):** `Corn___Common_rust`, `Corn___Gray_leaf_spot`, `Corn___healthy` (Source: PlantVillage)
- **Grape (3 classes):** `Grape___Black_rot`, `Grape___Esca`, `Grape___Leaf_blight` (Source: PlantVillage)
- **Rice (3 classes):** `Rice___Bacterial_blight`, `Rice___Leaf_blast`, `Rice___healthy` (Source: Paddy Doctor)

#### Version 2 (V2 — Plantation & Cash Crop Expansion)
Incorporate supplementary datasets:
- **Banana:** Mendeley Banana Leaf Disease Dataset (Black Sigatoka, Panama Disease, Healthy).
- **Mango & Sugarcane:** Indian Agricultural Research Datasets (Anthracnose, Red Rot).
- **Unrepresented tropical pests/rare diseases (Mealybug, Rubber Leaf Spot, Tea Blister):** Remain intelligently serviced by the multimodal Gemini Vision AI layer.

---

*Phase 27A dataset compatibility inspection complete. No data downloaded. No models trained. No production files modified. STOPPED as requested.*
