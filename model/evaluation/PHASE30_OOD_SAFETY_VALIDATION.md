# PHASE 30 OOD SAFETY VALIDATION REPORT

## 1. Dataset Verification
- OOD Source: `dataset/real_world_test/UNSEEN_CLASS`
- OOD Leakage Check: Confirmed. `UNSEEN_CLASS` was strictly excluded during `phase30_pipeline.py` adaptation split logic.
- Number of OOD Samples: 1527
- Number of PV Test Samples: 3432
- Number of RW Test Samples: 163

## 2. OOD Mistake Definition & Comparability
- **Definition**: An 'OOD mistake' occurs when an image belonging to an unknown disease/crop is confidently assigned to one of the 18 known classes.
- **Comparability**: The 164 -> 195 increase IS directly comparable. Both models were evaluated on the exact same 1527 OOD samples. The regression is real.

## 3. Confidence Distribution Statistics (OOD Data)
| Metric | Phase 27C | Phase 30 |
|---|---|---|
| Mean Confidence | 0.5977 | 0.5833 |
| Median Confidence | 0.5790 | 0.5476 |
| 90th Percentile | 0.9104 | 0.9372 |
| 95th Percentile | 0.9586 | 0.9749 |
| Max Confidence | 1.0000 | 1.0000 |

## 4. OOD Rejection & Threshold Analysis
Comparison of False Accept Rate (FAR) on OOD data versus True Accept Rate (Coverage) on Known Real-World Data.

| Threshold | P27C OOD FAR | P27C Known Cov | P30 OOD FAR | P30 Known Cov |
|---|---|---|---|---|
| 50% | 61.17% | 66.87% | 56.91% | 67.48% |
| 60% | 46.17% | 52.15% | 42.44% | 56.44% |
| 70% | 33.60% | 42.33% | 31.96% | 44.79% |
| 80% | 21.02% | 31.29% | 21.81% | 36.20% |
| 85% | 15.91% | 25.77% | 17.16% | 34.97% |
| 90% | 10.74% | 20.25% | 12.77% | 30.06% |
| 95% | 6.29% | 14.72% | 8.78% | 23.31% |

## 5. AUROC (Separation Metric)
| Known Dataset | Phase 27C AUROC | Phase 30 AUROC |
|---|---|---|
| PV Test | 0.9291 | 0.9367 |
| RW Test | 0.5635 | 0.5994 |

## 6. OOD Safety Conclusion
**A. Regression Reality**: The 164 -> 195 high-confidence OOD mistake metric is a real and valid regression. Phase 30 became more confident generally to improve real-world matched accuracy, which unfortunately increased overconfidence on OOD samples.

**B. Experimental Status**: Phase 30 should remain **EXPERIMENTAL**. The model traded OOD safety for in-distribution accuracy. It is unsafe to deploy autonomously.

**C. Needs Review Gate**: A confidence threshold/NEEDS_REVIEW gate CANNOT safely eliminate high-confidence OOD mistakes without completely destroying coverage of true real-world images. The AUROC scores demonstrate poor separability.

**D. Additional Improvements Required**: Before production deployment, the model requires explicit OOD detection mechanisms (e.g., Mahalanobis distance, Deep Ensembles, or an auxiliary 'Unknown' class) because standard Softmax confidence fails entirely as an OOD discriminator.
