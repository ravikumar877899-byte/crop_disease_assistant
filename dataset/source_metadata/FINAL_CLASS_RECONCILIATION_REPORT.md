# FINAL DATASET CLASS RECONCILIATION REPORT

## 1. Current Physical Classes (dataset/raw/plantvillage)
Total images: 22,799

1. Apple___Apple_scab: 630
2. Apple___Black_rot: 621
3. Apple___Cedar_apple_rust: 275
4. Apple___healthy: 1645
5. Corn_(maize)___Common_rust_: 1192
6. Corn_(maize)___Northern_Leaf_Blight: 985
7. Corn_(maize)___healthy: 1162
8. Grape___Black_rot: 1180
9. Grape___Esca_(Black_Measles): 1383
10. Grape___Leaf_blight_(Isariopsis_Leaf_Spot): 1076
11. Grape___healthy: 423
12. Potato___Early_blight: 1000
13. Potato___Late_blight: 1000
14. Potato___healthy: 152
15. Tomato___Bacterial_spot: 2127
16. Tomato___Early_blight: 1000
17. Tomato___Tomato_Yellow_Leaf_Curl_Virus: 5357
18. Tomato___healthy: 1591

## 2. Existing Model Classes (model/class_indices.json)
Total defined: 28 classes
- 0: Apple Scab
- 1: Apple Black Rot
- 2: Apple Cedar Rust
- 3: Corn Common Rust
- 4: Corn Gray Leaf Spot
- 5: Grape Black Rot
- 6: Grape Esca (Black Measles)
- 7: Grape Leaf Blight
- 8: Potato Early Blight
- 9: Potato Late Blight
- 10: Rice Bacterial Blight
- 11: Rice Leaf Blast
- 12: Tomato Bacterial Spot
- 13: Tomato Early Blight
- 14: Tomato Yellow Leaf Curl Virus
- 15: Banana Black Sigatoka
- 16: Healthy Banana Leaf
- 17: Mango Anthracnose
- 18: Mango Mealybug
- 19: Coffee Leaf Rust
- 20: Rubber Leaf Spot
- 21: Tea Blister Blight
- 22: Sugarcane Red Rot
- 23: Wheat Leaf Rust
- 24: Potato Black Scurf
- 25: Citrus Canker
- 26: Cotton Boll Rot
- 27: Soybean Rust

## 3. Existing Disease Matrix (backend/crop_disease_matrix.py)
**Status:** File does not exist in the repository. The disease/treatment mappings are entirely housed within `model/class_indices.json` and `dataset/source_metadata/class_mapping.csv`.

## 4. Reconciliation Analysis

### Exact Matches (12 Classes)
These downloaded PlantVillage classes map directly to the active diseases in `class_indices.json`:
1. Apple___Apple_scab -> Apple Scab (0)
2. Apple___Black_rot -> Apple Black Rot (1)
3. Apple___Cedar_apple_rust -> Apple Cedar Rust (2)
4. Corn_(maize)___Common_rust_ -> Corn Common Rust (3)
5. Grape___Black_rot -> Grape Black Rot (5)
6. Grape___Esca_(Black_Measles) -> Grape Esca (Black Measles) (6)
7. Grape___Leaf_blight_(Isariopsis_Leaf_Spot) -> Grape Leaf Blight (7)
8. Potato___Early_blight -> Potato Early Blight (8)
9. Potato___Late_blight -> Potato Late Blight (9)
10. Tomato___Bacterial_spot -> Tomato Bacterial Spot (12)
11. Tomato___Early_blight -> Tomato Early Blight (13)
12. Tomato___Tomato_Yellow_Leaf_Curl_Virus -> Tomato Yellow Leaf Curl Virus (14)

### Missing Classes
- **Corn Gray Leaf Spot (Cercospora):** Expected by index 4, but not physically downloaded.
- **Rice & Deferred Classes:** 15 other classes (Rice, Banana, Mango, etc.) are in `class_indices.json` but were intentionally deferred for future datasets (Paddy Doctor, etc.) or phases.

### Extra Classes (6 Classes)
These were physically downloaded but are **not** present in `class_indices.json`:
1. Apple___healthy
2. Corn_(maize)___healthy
3. Grape___healthy
4. Potato___healthy
5. Tomato___healthy
6. Corn_(maize)___Northern_Leaf_Blight

## 5. Northern Leaf Blight vs Gray Leaf Spot Discrepancy
- **Is Northern Leaf Blight already represented?** No. It does not exist in `class_indices.json` or `class_mapping.csv`.
- **Is Cercospora/Gray Leaf Spot represented?** Yes. It is defined as Index 4 (`Corn Gray Leaf Spot`) in `class_indices.json` and is mapped in `class_mapping.csv`, but the images were not downloaded.

## 6. Duplicate Analysis
- 13 exact SHA-256 duplicate image hashes found.
- 7 duplicates within `Apple___healthy`.
- 6 duplicates within `Tomato___healthy`.
- **Finding:** All duplicates are strictly **WITHIN the same class**.
- **Conclusion:** There are no cross-class duplicates (which means there is zero risk of data leakage or label noise). They can safely be removed during the train/test splitting phase.

## 7. Recommendation for Final Training Class List
To build a robust model using the data we actually have, we must update the model configuration to match the 18 physically acquired classes. This requires adding the healthy classes and swapping the missing Gray Leaf Spot for the acquired Northern Leaf Blight.

**Recommended 18-Class Target:**
1. Apple Scab (630)
2. Apple Black Rot (621)
3. Apple Cedar Rust (275)
4. Apple Healthy (1645)
5. Corn Common Rust (1192)
6. Corn Northern Leaf Blight (985)
7. Corn Healthy (1162)
8. Grape Black Rot (1180)
9. Grape Esca (1383)
10. Grape Leaf Blight (1076)
11. Grape Healthy (423)
12. Potato Early Blight (1000)
13. Potato Late Blight (1000)
14. Potato Healthy (152)
15. Tomato Bacterial Spot (2127)
16. Tomato Early Blight (1000)
17. Tomato Yellow Leaf Curl Virus (5357)
18. Tomato Healthy (1591)

**Exact total image count for recommended list:** 22,799 images (before removing the 13 duplicates).

## 8. Action Required Before Phase 27C
1. Update `model/class_indices.json` to include the 5 `healthy` classes and `Corn Northern Leaf Blight`, while removing the deferred/missing classes (or re-indexing them).
2. Update `dataset/source_metadata/class_mapping.csv` to reflect the change from Gray Leaf Spot to Northern Leaf Blight.
3. Decide if the 13 same-class duplicates should be deleted prior to splitting.
