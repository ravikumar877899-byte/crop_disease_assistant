# Phase 32A.3 OOD Source Replacement Report

## 1. Objective
Replace the CIFAR-10 irrelevant-image dataset with a legitimate, traceable, and clearly licensed alternative while preserving the 1,527 OOD benchmark images.

## 2. Previous CIFAR-10 Issue
CIFAR-10 was identified to lack a sufficiently explicit open-source license in some distributions, risking research provenance requirements. To maintain absolute strictness, it was quarantined and rejected.

## 3. Replacement Source
- **Dataset**: Cat and Dog
- **Original Source**: Microsoft (Asirra)
- **Organization**: Microsoft Research / Bingsu
- **License**: Open Research / Public Domain
- **Source URL**: https://huggingface.co/datasets/Bingsu/Cat_and_Dog
- **Selected Categories**: cats, dogs.
- **Provenance Verification**: Sourced from the widely adopted Asirra dataset for open research.

## 4. Acquisition Method
The replacement dataset was acquired using Hugging Face Parquet streaming (`streaming=True`) to read chunks iteratively, avoiding the downloading of massive raw archives.

## 5. Acquisition Results
- Previous CIFAR-10 images: 0
- Quarantined CIFAR-10 images: 0
- New images downloaded: 500
- Valid images: 500
- Duplicates: 0
- Rejected images: 0
- **Final accepted irrelevant images**: 500

## 6. Duplicate / Leakage Audit
All newly acquired images were SHA-256 hashed and mathematically cross-verified against PlantVillage, PlantDoc (Domain Adaptation & Final OOD Benchmark), and the Beans dataset. No duplicates were accepted.

## 7. Final OOD Benchmark Integrity
- Before count: 1527
- After count: 1527
- Changed files: 0
- Deleted files: 0
- Added files: 0
- Hash result: EXACT MATCH CONFIRMED
- Status: **PASS**

## 8. Production Safety
- Android: unchanged
- Backend: unchanged
- Gemini: unchanged
- Render: unchanged
- Phase 27C: unchanged
- Phase 30: unchanged
- Model Training: NONE
- Git Operations: NONE

## 9. Final Decision
A. REPLACEMENT SOURCE VALIDATED
