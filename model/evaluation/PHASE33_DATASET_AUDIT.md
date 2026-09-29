# Phase 33 Dataset Audit

## 1. Dataset sources
PlantDoc (Original Source Archives)

## 2. Original source provenance
PlantDoc dataset from academic repository.

## 3. License information
CC BY 4.0 / Academic Use

## 4. Number of images acquired
2,598 (Total PlantDoc archive)

## 5. Number accepted
0 (For training)

## 6. Number rejected
2,578 (Already locked in final test sets)

## 7. Rejection reasons
Strict data leakage prevention. Phase 29B entirely consumed PlantDoc into `dataset/real_world_test/MATCHED_CLASS` (1,051) and `dataset/real_world_test/UNSEEN_CLASS` (1,527). Using them for training would irreparably contaminate the blind benchmarks.

## 8. Exact duplicate results
2,578 exact duplicates against `dataset/real_world_test`.

## 9. Near-duplicate results
N/A

## 10. Class mappings
N/A (No new data accepted)

## 11. Unmapped classes
N/A

## 12. Train/validation/final-test counts
Train: 0, Val: 0, Test: 0

## 13. Class balance
N/A

## 14. Domain-diversity observations
N/A

## 15. Protected OOD integrity
Verified 1,527 hashes. Intact.

## 16. Whether independent final testing is possible
IMPOSSIBLE. There are ~20 images left in PlantDoc, which is mathematically insufficient to construct a new independent 18-class final test set or perform meaningful domain adaptation training.
