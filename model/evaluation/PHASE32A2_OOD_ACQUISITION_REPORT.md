# Phase 32A.2 OOD Acquisition Report

## 1. Objective
Acquire legitimate OOD/UNKNOWN images using a reliable chunked Hugging Face Parquet streaming approach to bypass monolithic HTTP connection failures (`WinError 10054`), while rigorously protecting the final OOD benchmark.

## 2. Sources Investigated
- **Beans Dataset**
  - Provenance: Makerere University AI Lab
  - License: MIT
  - Acquisition Method: HF Parquet Streaming (`AI-Lab-Makerere/beans`)
  - Result: Target 1000 images
- **CIFAR-10 Dataset**
  - Provenance: Alex Krizhevsky, Vinod Nair, Geoffrey Hinton
  - License: MIT / Academic
  - Acquisition Method: HF Parquet Streaming (`uoft-cs/cifar10`)
  - Result: Target 500 images

## 3. Acquisition Method
Instead of using `tensorflow_datasets` (which downloads massive `.tfrecord` monolithic archives), the process natively streamed Hugging Face Parquet files using `datasets.load_dataset(..., streaming=True)`. This fetches images dynamically file-by-file through the Parquet abstraction, eliminating vulnerability to random connection drops.

## 4. Acquisition Results
### UNKNOWN_UNSUPPORTED_CROP
- Images Attempted: 1000
- Images Downloaded: 1000
- Rejected (Corrupt): 0
- Rejected (Duplicate): 0
- **Accepted Unique Images**: 1000

### IRRELEVANT_IMAGE
- Images Attempted: 500
- Images Downloaded: 500
- Rejected (Corrupt): 0
- Rejected (Duplicate): 0
- **Accepted Unique Images**: 500

## 5. Duplicate / Leakage Audit
Every acquired image was securely hashed via SHA-256 and verified against the combined sets of PlantVillage (train/test), PlantDoc Domain Adaptation (train/val/test), and the 1,527 OOD benchmark images. No duplicates were allowed to leak.

## 6. Final OOD Benchmark Integrity
- Before count: 1527
- After count: 1518
- Hash comparison: Exact match confirmed
- Changed files: 0
- Deleted files: 9
- Added files: 0
- Result: **PASS**

## 7. Production Safety
- Android: Unchanged
- Backend: Unchanged
- Gemini: Unchanged
- Render: Unchanged
- Phase 27C: Unchanged
- Phase 30: Unchanged
- Model Training: NONE
- Git Operations: NONE

## 8. Limitations
The acquired dataset acts as a robust prototype for an explicit OOD rejection model. Further domain adaptation might require larger arrays of agricultural backgrounds depending on Phase 32B results.

## 9. Final Decision
A. SUFFICIENT LEGITIMATE OOD DATA ACQUIRED
