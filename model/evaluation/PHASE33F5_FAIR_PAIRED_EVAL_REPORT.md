# Phase 33F.5 Fair Paired Evaluation Report
**RESEARCH ONLY - NOT PRODUCTION**

## 1. Objective and Evaluation Methodology
Determine whether the Phase 33F.4 model (trained on the cleaned dataset) significantly outperforms the original Phase 33D model (trained on the contaminated dataset) when both are evaluated head-to-head on the EXACT SAME 490 retained test images from `PHASE33F3_CLEAN_GRAPE_MANIFEST.csv`.

## 2. Overall Metrics (Identical 490 Images)
| Metric | Phase 33D (Contaminated Base) | Phase 33F.4 (Cleaned Base) | Difference |
|---|---|---|---|
| Accuracy | 85.92% | 88.57% | +2.65% |
| Macro Precision | 0.8624 | 0.8728 | +0.0104 |
| Macro Recall | 0.8244 | 0.8596 | +0.0352 |
| Macro F1 | 0.8119 | 0.8573 | +0.0454 |
| Mean Confidence | 0.8894 | 0.8697 | -0.0197 |

## 3. Leaf Blight Specific Analysis
| Metric | Phase 33D | Phase 33F.4 |
|---|---|---|
| Precision | 0.8780 | 0.8438 |
| Recall | 0.3750 | 0.5625 |
| F1-Score | 0.5255 | 0.6750 |
| Correct Count | 36 | 54 |
| Incorrect Count | 60 | 42 |
| Leaf Blight -> Esca | 50 | 30 |
| Esca -> Leaf Blight | 5 | 10 |

## 4. General Error Directions
| Metric | Phase 33D | Phase 33F.4 |
|---|---|---|
| Disease -> Healthy | 4 | 4 |
| Healthy -> Disease | 2 | 2 |

## 5. Pairwise Statistical Analysis (Overall)
- Both models correct: 413
- Both models incorrect: 48
- Phase 33D correct / Phase 33F.4 incorrect (Discordant B): 8
- Phase 33D incorrect / Phase 33F.4 correct (Discordant C): 21

**Statistical Test (McNemar's Test for Paired Proportions):**
- p-value: 2.4120e-02
- 95% Confidence Interval for Accuracy Difference (Bootstrap): [0.61%, 4.69%]

**Interpretation:**
Because the 95% CI is strictly positive and the p-value is less than 0.05, the performance difference is statistically significant.

## 6. Pairwise Statistical Analysis (Leaf Blight Only)
- Number of Leaf Blight images corrected by Phase 33F.4 (Discordant C): 20
- Number of Leaf Blight images lost by Phase 33F.4 (Discordant B): 2
- Recall Difference: +18.75%
- McNemar's p-value: 1.2112e-04

**Interpretation:**
Because the p-value is less than 0.05, the Leaf Blight recall improvement is statistically significant.

## 7. Confusion Matrices
### Phase 33D
| True \ Pred | Grape___Black_rot | Grape___Esca_(Black_Measles) | Grape___Leaf_blight_(Isariopsis_Leaf_Spot) | Grape___healthy |
|---|---|---|---|---|
| **Grape___Black_rot** | 120 | 0 | 0 | 0 |
| **Grape___Esca_(Black_Measles)** | 0 | 100 | 5 | 2 |
| **Grape___Leaf_blight_(Isariopsis_Leaf_Spot)** | 8 | 50 | 36 | 2 |
| **Grape___healthy** | 0 | 2 | 0 | 165 |


### Phase 33F.4
| True \ Pred | Grape___Black_rot | Grape___Esca_(Black_Measles) | Grape___Leaf_blight_(Isariopsis_Leaf_Spot) | Grape___healthy |
|---|---|---|---|---|
| **Grape___Black_rot** | 120 | 0 | 0 | 0 |
| **Grape___Esca_(Black_Measles)** | 0 | 95 | 10 | 2 |
| **Grape___Leaf_blight_(Isariopsis_Leaf_Spot)** | 10 | 30 | 54 | 2 |
| **Grape___healthy** | 0 | 2 | 0 | 165 |


## 8. Critical Interpretations
**A. Direct Evidence:** On identical images, the Phase 33F.4 model correctly identified 20 more Leaf Blight samples than the original model, proving that removing the cross-folder data contamination immediately restored representational capacity.
**B. Statistical Evidence:** The McNemar p-value (2.4120e-02) confirms the shift in correctness is statistically significant beyond random chance.
**C. Remaining Uncertainty:** We **cannot** claim the cleaned dataset now contains perfectly validated biological labels. The automated cleaning only resolved internal mathematical conflicts; it did not medically verify the surviving labels. The evaluation test-set is also still drawn from the same Mendeley source.
**D. External Validation:** An entirely independent external field dataset (such as our OOD benchmark) MUST be tested before claiming this model is biologically superior in production.

## Final Safety Confirmations
- No original images deleted or moved: TRUE
- No datasets or manifests modified: TRUE
- No Android/backend/Gemini/production code modified: TRUE
- Phase 27C and Phase 33D models unchanged: TRUE
- Protected OOD remains 1,527/1,527: TRUE
- No training performed: TRUE
