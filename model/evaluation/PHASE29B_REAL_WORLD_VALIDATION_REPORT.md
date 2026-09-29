# FINAL REAL-WORLD VALIDATION REPORT

1. Dataset source: PlantDoc-Full (Hugging Face / Pratik Kayal)
2. Dataset independence: 100% independent. No SHA-256 duplicates with PlantVillage.
3. Number of images: 2578
4. Matched images: 1051
5. Unseen images: 1527
6. Duplicate results: 0 duplicates in PlantVillage train/val/test.
7. Real-world accuracy: 26.17%
8. Precision (Macro): 23.35%
9. Recall (Macro): 12.66%
10. F1 (Macro): 14.25%
11. Per-class results: See CSV/Logs.
12. Confusion matrix: Saved to phase29b_confusion_matrix.png
13. Healthy safety: 43 diseased images missed (Disease -> Healthy)
14. Confidence analysis: See PHASE29B_CONFIDENCE_ANALYSIS.md
15. High-confidence errors: 126
16. NEEDS_REVIEW simulations: Generated.
17. OOD analysis: 164 high-confidence OOD mistakes.
18. PlantVillage comparison: Substantial gap between held-out PV and independent real-world.
19. Limitations: Model overfits to PlantVillage lab conditions. False healthy rate implies direct replacement of Gemini Vision is premature.
20. Recommended next step: Implement NEEDS_REVIEW fallback, augment with diverse backgrounds, or utilize Gemini as primary agent for complex cases.
