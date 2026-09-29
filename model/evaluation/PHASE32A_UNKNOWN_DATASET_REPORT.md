# PHASE 32A UNKNOWN DATASET REPORT

## 1. Existing Dataset Inventory
- **Path**: dataset/train
  - Images: 15954
  - Classes: 18
  - Source: PlantVillage
  - Training: True
  - Validation: False
  - Testing: False
  - Protected: False

- **Path**: dataset/val
  - Images: 3413
  - Classes: 18
  - Source: PlantVillage
  - Training: False
  - Validation: True
  - Testing: False
  - Protected: False

- **Path**: dataset/test
  - Images: 3432
  - Classes: 18
  - Source: PlantVillage
  - Training: False
  - Validation: False
  - Testing: True
  - Protected: True

- **Path**: dataset/real_world_test
  - Images: 2578
  - Classes: 13
  - Source: PlantDoc (Raw Extracted)
  - Training: False
  - Validation: False
  - Testing: True
  - Protected: True

- **Path**: dataset/domain_adaptation/test
  - Images: 163
  - Classes: 18
  - Source: PlantDoc (Matched)
  - Training: False
  - Validation: False
  - Testing: True
  - Protected: True

- **Path**: dataset/phase30_train
  - Images: 2924
  - Classes: 18
  - Source: Mixed PV+PlantDoc
  - Training: True
  - Validation: False
  - Testing: False
  - Protected: False

- **Path**: dataset/phase30_val
  - Images: 1027
  - Classes: 18
  - Source: Mixed PV+PlantDoc
  - Training: False
  - Validation: True
  - Testing: False
  - Protected: False

## 2. Protected Final OOD Set
- Total 1,527 UNSEEN_CLASS images successfully hashed and catalogued in `PHASE32_FINAL_OOD_MANIFEST.csv`.
- Integrity locks confirmed: These images will NOT be used for Phase 32B training or validation.

## 3. New UNKNOWN/OOD Sources
Research identified the following legitimate open-source datasets as candidates:
1. **Beans Dataset** (Makerere University AI Lab)
   - URL: https://huggingface.co/datasets/beans
   - License: MIT
   - Category: `UNKNOWN_UNSUPPORTED_CROP`
2. **Stanford Dogs Dataset**
   - URL: http://vision.stanford.edu/aditya86/ImageNetDogs/
   - License: Non-commercial research
   - Category: `IRRELEVANT_IMAGE`

## 4. Current Blockers
- **Insufficient Local Data**: The local environment currently lacks sufficient non-leaf and unsupported-crop datasets to construct a scientifically valid, biologically robust OOD training split without risking extreme bias.
- **Network Restrictions**: Cannot dynamically download multi-gigabyte diverse datasets directly through the current runner.

## 5. Final Decision
**B. INSUFFICIENT DATA — MORE DATA REQUIRED**
To safely proceed to Phase 32B (training an explicit OOD reject class), we must acquire thousands of highly diverse, real-world images spanning unsupported agricultural contexts and irrelevant background data. Attempting to build an OOD model with tiny subsets or fabricated data will destroy the model's integrity.
