# PLANTVILLAGE ALTERNATIVE SOURCE RESEARCH REPORT
**Date of Audit:** 2026-09-22  
**Auditor / Tool:** AI Crop Care Dataset Research Module  
**Scope:** Evaluation of alternative legitimate acquisition sources for the required 18 PlantVillage COLOR classes without downloading the 2.33 GB GitHub codeload master archive.

---

## 1. Candidate Source Identity & Legitimacy
- **Repository Identifier:** `mohanty/PlantVillage`
- **Canonical URL:** `https://huggingface.co/datasets/mohanty/PlantVillage`
- **Dataset Author:** **Sharada P. Mohanty** (`@mohanty`, EPFL / Frontiers lead author of the original 2016 PlantVillage paper)
- **Legitimacy Status:** **VERIFIED CANONICAL AUTHOR REPOSITORY**. The dataset on Hugging Face is maintained directly by Sharada Mohanty, the primary researcher who published the PlantVillage benchmark dataset in *Frontiers in Plant Science* (2016).
- **Associated Citation:**
  > Mohanty, S. P., Hughes, D. P., & Salathé, M. (2016). *Using deep learning for image-based plant disease detection*. Frontiers in Plant Science, 7, 1419. doi: 10.3389/fpls.2016.01419.

---

## 2. Dataset License & Legal Terms
- **License Type:** **Creative Commons Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)**
- **Documented in:** Dataset card metadata (`cardData.license: cc-by-sa-3.0`), dataset tags, and `plant_village.py` loader script.
- **Commercial & Academic Permissibility:**
  - Sharing and adaptation are explicitly permitted under attribution and share-alike conditions.
  - Fully compliant with our educational, research, and non-profit mobile assistive application.
  - Requires clear attribution to Mohanty et al. (2016) and preservation of license terms for derivative splits.

---

## 3. Availability of the 18 Target COLOR Classes
The dataset structure on Hugging Face exposes the exact directory tree and color split manifests (`splits/color_train.txt` and `splits/color_test.txt`).

All **18 target classes** are confirmed present and correspond 1-to-1 with the canonical classes:

| # | AI Crop Care Target Class | Hugging Face Dataset Folder | Verified Image Count |
| :---: | :--- | :--- | :---: |
| 1 | Apple Scab | `raw/color/Apple___Apple_scab` | 630 |
| 2 | Apple Black Rot | `raw/color/Apple___Black_rot` | 621 |
| 3 | Apple Cedar Apple Rust | `raw/color/Apple___Cedar_apple_rust` | 275 |
| 4 | Apple Healthy | `raw/color/Apple___healthy` | 1,645 |
| 5 | Corn Common Rust | `raw/color/Corn_(maize)___Common_rust_` | 1,192 |
| 6 | Corn Gray Leaf Spot | `raw/color/Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot` | 513 |
| 7 | Corn Healthy | `raw/color/Corn_(maize)___healthy` | 1,162 |
| 8 | Grape Black Rot | `raw/color/Grape___Black_rot` | 1,180 |
| 9 | Grape Esca | `raw/color/Grape___Esca_(Black_Measles)` | 1,383 |
| 10 | Grape Leaf Blight | `raw/color/Grape___Leaf_blight_(Isariopsis_Leaf_Spot)` | 1,076 |
| 11 | Grape Healthy | `raw/color/Grape___healthy` | 423 |
| 12 | Potato Early Blight | `raw/color/Potato___Early_blight` | 1,000 |
| 13 | Potato Late Blight | `raw/color/Potato___Late_blight` | 1,000 |
| 14 | Potato Healthy | `raw/color/Potato___healthy` | 152 |
| 15 | Tomato Bacterial Spot | `raw/color/Tomato___Bacterial_spot` | 2,127 |
| 16 | Tomato Early Blight | `raw/color/Tomato___Early_blight` | 1,000 |
| 17 | Tomato Yellow Leaf Curl | `raw/color/Tomato___Tomato_Yellow_Leaf_Curl_Virus` | 5,357 |
| 18 | Tomato Healthy | `raw/color/Tomato___healthy` | 1,591 |
| **Total** | **All 18 Target Classes** | **Exact Canonical Color Subtree** | **22,327 images** |

---

## 4. Image Color Space & Integrity
- **Color Modality:** **RGB / Full 3-Channel Color** (256 × 256 pixels, JPEG format).
- Confirmed in Hugging Face manifest: files reside under the explicit `raw/color/` namespace. Grayscale (`raw/grayscale/`) and segmented (`raw/segmented/`) subsets are stored separately and can be completely excluded.

---

## 5. Per-Class Image Frequencies
- **Class-by-class verification:** The physical file paths parsed directly from Hugging Face's canonical manifest files (`color_train.txt` + `color_test.txt`) sum to **exactly 22,327 images** across the 18 target classes, matching the expected count to the exact single image.

---

## 6. HTTP Range & Resumption Capabilities Comparison
A critical comparative technical finding between GitHub and Hugging Face:

| Capability | GitHub (`spMohanty/PlantVillage-Dataset`) | Hugging Face (`mohanty/PlantVillage`) |
| :--- | :--- | :--- |
| **Delivery Architecture** | Dynamic streaming via `codeload` service | Static CDN backed by Cloudflare / CloudFront |
| **HTTP `Accept-Ranges`** | `None` (Ignored) | **Supported (`HTTP 206 Partial Content`)** |
| **HTTP Range Support** | **FAILED** (Server returns HTTP 200 from byte 0) | **CONFIRMED WORKING** (`bytes 0-100/2184723441` returns 206) |
| **Safe Interrupted Resumption** | **NO** (Must restart from byte 0 on interruption) | **YES** (Standard byte range requests supported) |
| **Metadata File Access** | Truncated at 50,000 tree objects | Dedicated raw endpoints & manifest lists |

---

## 7. Download Size Estimation for the 18 Target Classes
- **Full Hugging Face `data.zip`:** `2,184,723,441 bytes` (~2.03 GB, contains all 38 classes across color, grayscale, and segmented).
- **Target 18 Color Classes Subset Only (22,327 images):**
  - Average image file size: ~25 KB to 38 KB
  - **Estimated physical size of 18 classes:** **~560 MB – 620 MB** (less than 28% of the total archive).

---

## 8. Preservation of Original Class Identity & Leaf Grouping
- The folder names and file names in `mohanty/PlantVillage` match the original dataset naming scheme (`Apple___Apple_scab`, `Corn_(maize)___Common_rust_`, etc.).
- The repository also provides `leaf_grouping/leaf-map.json` (40,328 entries), documenting exact individual leaf associations to eliminate data leakage when generating train/val/test splits in Phase 27B-4.

---

## 9. Recommended Acquisition Strategy Options

### Strategy A: HTTP Range Remote Stream Extraction (Targeted & Bandwidth Efficient)
Because Hugging Face's `data.zip` endpoint supports standard HTTP `Range` requests (`HTTP 206`), a Python streaming client can read the ZIP central directory at the tail of the remote file, locate the entry headers for only the 18 target color classes, and fetch **only those exact byte ranges** directly into `dataset/raw/plantvillage/`.
- **Bandwidth Required:** **~580 MB** (avoids downloading the 1.5 GB of non-target/grayscale data).
- **Risk of Interruption:** Very low, and byte offsets are deterministic.

### Strategy B: Range-Resumable Full Archive Download from Hugging Face
Download `https://huggingface.co/datasets/mohanty/PlantVillage/resolve/main/data.zip` into `dataset/source_archives/data.zip`.
- Because Hugging Face fully supports HTTP `Range: bytes=X-`, if a download is interrupted, it can resume from the exact byte without restarting from zero.
- Once downloaded, extract only the 18 color folders into `dataset/raw/plantvillage/`.

### Strategy C: Parallel Raw File Stream from GitHub Trees
Using the GitHub blob trees cached in `dataset/source_metadata/class_shas.json`, pull the 22,327 individual raw images class-by-class into `dataset/raw/plantvillage/`.

---

## 10. Summary & Readiness Assessment
- **Legitimacy:** **100% Verified** (Official author repository by S. P. Mohanty).
- **18 Target Classes:** **100% Present** (Exactly 22,327 verified color images).
- **License Compliance:** **CC BY-SA 3.0** (Attribution required; ideal for research).
- **Existing Partial File:** `dataset/source_archives/plantvillage_master.zip.part` (356,515,840 bytes) remains preserved and untouched.
- **Production Code:** `backend/app.py` and `android_app/` remain completely unchanged.
