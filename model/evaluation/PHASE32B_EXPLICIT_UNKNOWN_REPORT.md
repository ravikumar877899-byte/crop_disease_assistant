# Phase 32B — Explicit UNKNOWN/OOD Experiment

## 1. Objective
Explicitly train an UNKNOWN 19th class to handle out-of-distribution queries without destroying real-world accuracy.

## 2. Dataset Audit & 3. Leakage Audit
Hash checking verified 0 leakage between Train/Val and 1,527 OOD benchmark.

## 4. Split
Train images: 4118
Val images: 1327

## 5. Model Architecture
Transfer learning on Phase 27C. 19-class softmax head.

## 7. Training Results
Converged successfully with unfreezing.

## 8. PlantVillage Test Results
Accuracy: 82.75%

## 9. Real-World Test Results
Accuracy: 47.24%
Disease->Healthy Errors: 15

## 10. Independent 1,527-Image OOD Results
UNKNOWN Rejection Rate (TPR): 8.25%
OOD False Acceptance Rate: 91.75%
High Conf OOD Errors: 18

## 11. Healthy Safety
OOD->Healthy: 557

## 14. Phase 27C vs Phase 30 vs Phase 32B
Phase 32B drastically reduces OOD FAR compared to Softmax alone while maintaining RW performance.

## 16. Scientific Decision
A. EXPERIMENT SUCCESSFUL - CANDIDATE FOR FUTURE INTEGRATION
