# PHASE 30 MODEL IMPROVEMENT REPORT

## 1. Previous Performance (Phase 27C & 29B)
- PV Acc: 96.24%
- RW Acc: 26.17%
- RW Disease->Healthy: 43

## 2. Dataset Strategy & Leakage
- PlantVillage Test remains entirely untouched.
- PlantDoc RW Test was strictly separated (70/15/15 train/val/test) to prevent leakage.
- Cross-dataset leakage identified and purged: 0

## 3. Results
- New PV Acc: 96.42%
- New RW Acc: 57.06%
- RW Disease->Healthy: 9
- RW High Conf Errors: 3
- OOD High Conf Mistakes: 195 / 1527

## 4. Model Selection
**Phase 30 model ADOPTED.** Real-world performance improved without unacceptable PlantVillage regression. A new `.keras` and `.tflite` was saved as `_phase30`.
