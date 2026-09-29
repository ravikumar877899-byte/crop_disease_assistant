# Phase 33C.2 ZIP Verification Report

## 1. ZIP File Information
- **Exact ZIP filename:** `DATASET.zip`
- **Location:** `C:\Users\BRINDHA\OneDrive\Desktop\DATASET.zip`
- **File size:** 902,559,119 bytes (~902 MB)
- **SHA-256:** `d0d739b61f85c9bf04905015b3885199c9dc61bcad5ff5e831d64940b65979f0`

## 2. ZIP Integrity
- **Status:** **PASS**. The ZIP file was tested using Python's `zipfile.testzip()` and successfully opened and read without any cyclic redundancy check (CRC) errors or corruption.

## 3. Dataset Contents
- **Root Directory:** `DATASET/`
- **Actual folder/class names:**
  - `healthy`
  - `Black rot`
  - `leaf blight`
  - `esca`
- **Estimated image count:** 3,477 images (identified by `.jpg`, `.jpeg`, `.png` extensions)

## 4. Class Mapping
- **Mapping Candidates:**
  - `healthy` -> `Grape___healthy`
  - `Black rot` -> `Grape___Black_rot`
  - `esca` -> `Grape___Esca_(Black_Measles)`
  - `leaf blight` -> `Grape___Leaf_blight_(Isariopsis_Leaf_Spot)`
- **Unmapped classes:** None

## 5. Warnings
- **Warning 1:** The dataset contains classes generically named (e.g., `healthy`, `Black rot`, `leaf blight`, `esca`). While they map directly to Grape disease states based on the dataset's declared provenance (Mendeley Grape Disease Dataset), cryptographic duplicate checking against existing datasets will be strictly necessary upon extraction to guarantee zero leakage.
- **Warning 2:** Dataset has not yet been extracted; images remain securely compressed to prevent accidental leakage into training pipelines.
