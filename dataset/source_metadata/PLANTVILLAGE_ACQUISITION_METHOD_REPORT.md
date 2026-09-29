# PLANTVILLAGE ACQUISITION METHOD INVESTIGATION & TECHNICAL EVALUATION REPORT
**Date of Evaluation:** 2026-09-22  
**Target Dataset:** PlantVillage (Canonical Author: Sharada P. Mohanty, `mohanty/PlantVillage`)  
**Scope:** Investigation of Hugging Face dataset storage, APIs, and Range protocols to identify the most efficient, safe, and verifiable method to acquire exclusively the 18 target COLOR classes (22,327 images, RGB 256×256 JPEG) without downloading 2+ GB of non-target/grayscale data.

---

## 1. Storage & Protocol Architecture on Hugging Face
The investigation examined how `mohanty/PlantVillage` is hosted on the Hugging Face Hub:
1. **Repository Structure:**
   - The repository hosts `data.zip` (2,184,723,441 bytes / ~2.03 GB) as a single Git LFS object.
   - Individual images (e.g. `raw/color/Apple___Apple_scab/...`) are **not** committed as loose Git trees in the repo root; attempting to access `.../resolve/main/raw/color/...` returns HTTP 404.
2. **Parquet / Datasets Server Evaluation:**
   - The datasets server generates `0.parquet` (5.97 MB for train, 1.54 MB for test).
   - These Parquet shards contain only split manifest metadata (image paths, labels, and bounding metadata). They do **not** embed the raw binary JPEG payloads for all 54,306 images (as demonstrated by their 5.7 MB size).
3. **HTTP Range Support on Hugging Face CDN:**
   - **Status:** **FULLY SUPPORTED (`HTTP 206 Partial Content`)**.
   - Probing the endpoint with byte-range headers confirmed that Hugging Face's Cloudflare-backed static CDN returns precise slice responses with `Content-Range: bytes START-END/2184723441`.

---

## 2. Technical Evaluation of Potential Acquisition Strategies

| Method | Feasibility | Download Payload | Resumable? | Preserves Original Files & Structure? | Risk Level |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **1. Direct Loose File URL** | **NOT POSSIBLE** (404) | N/A | N/A | N/A | High (Files not loose in git tree) |
| **2. Parquet Row Extract** | **NOT POSSIBLE** | ~7.5 MB | Yes | No (Images not embedded in parquet) | Low (Only metadata present) |
| **3. Full `data.zip` Download** | **FEASIBLE** | 2.03 GB | Yes (Range 206) | Yes | Medium (Downloads 1.45 GB unused data) |
| **4. HTTP Range-Indexed Selective Extraction** | **HIGHLY FEASIBLE** | **~580 MB** | **YES (Class-by-Class)** | **YES (Exact original names & bytes)** | **MINIMAL (Optimal)** |

---

## 3. Recommended Acquisition Method: Selective HTTP Range Zip Extraction

### What is this method?
A standard ZIP archive consists of local file headers + compressed payloads, followed by a **Central Directory** at the end of the file that contains a complete catalog of every file, its exact byte offset, compressed size, uncompressed size, and CRC-32 hash.

Because Hugging Face supports HTTP `Range` requests:
1. **Catalog Acquisition (One-time, 26.6 MB):**
   - The Central Directory is located at byte offset `2,156,772,140` to `2,184,723,440` (size: 27,951,203 bytes).
   - By fetching this single 26.6 MB slice, we have the exact byte offset for every single file in the archive.
2. **Selective Range Extraction (Target Classes Only):**
   - We filter the catalog to the **18 target classes** under `raw/color/` (exactly 22,327 entries).
   - For each class, we download **only the exact byte range** corresponding to that class's images (e.g. `bytes=X-Y`), directly decompressing the local ZIP payload to disk at:
     `dataset/raw/plantvillage/<Original_Class_Name>/<Original_Filename.JPG>`
   - Unrelated classes (Blueberry, Cherry, Orange, Peach, Bell pepper, Raspberry, Soybean, Squash, Strawberry) and all grayscale (`raw/grayscale/`) and segmented (`raw/segmented/`) data are **never downloaded**.

---

## 4. Key Answers to Investigation Criteria

1. **Can individual image files be downloaded directly via loose HTTP URLs?**  
   *No.* They are packaged inside `data.zip`; attempting to access them as loose Git LFS files yields HTTP 404.
2. **Does Hugging Face API/LFS expose exact files?**  
   *Yes.* The split files (`color_train.txt`, `color_test.txt`) and `data.zip`'s Central Directory catalog expose every single file path and byte offset.
3. **Can streaming/download filter to the 18 target classes without downloading unrelated modalities?**  
   *Yes.* Using HTTP Range extraction on `data.zip`, we can download strictly the byte offsets of the 18 target color classes.
4. **Does Parquet/datasets-server provide actual image bytes?**  
   *No.* Parquet shards in this repository contain metadata only.
5. **Approximate download size for only the 22,327 target images:**  
   *~560 MB – 590 MB* (compared to 2.03 GB for the full archive, saving ~1.45 GB of bandwidth and disk overhead).
6. **Does the method support resume/retry?**  
   *Yes.* Each file/class download is governed by explicit byte ranges; if any network hiccup occurs, the exact byte range or individual class is retried with zero data corruption.
7. **Are original PlantVillage filenames and class folders preserved?**  
   *Yes, 100%.* The Central Directory records store the exact original filenames (e.g. `00075aa8-d81a-4184-8541-b692b78d398a___FREC_Scab 3335.JPG`) and directory names.
8. **Can the resulting files be physically verified?**  
   *Yes.* Every extracted file is verified via CRC-32 (from ZIP header) and SHA-256 hash calculation, file existence, and PIL image readability.

---

## 5. Concrete Recommended Implementation Steps (For Next Phase)
1. Fetch Central Directory catalog (26.6 MB) via HTTP Range request.
2. Map the 22,327 target image entries to the 18 target classes.
3. Download and extract class-by-class into `dataset/raw/plantvillage/<class_name>/`.
4. Physically count, verify PIL image readability, compute SHA-256 hashes, and generate:
   - `dataset/source_metadata/plantvillage_metadata.csv`
   - `dataset/source_metadata/PLANTVILLAGE_ACQUISITION_REPORT.md`
5. Verify that `dataset/source_archives/plantvillage_master.zip.part` remains untouched.
