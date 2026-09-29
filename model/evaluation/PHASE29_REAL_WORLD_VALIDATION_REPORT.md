# PHASE 29: REAL-WORLD / OOD VALIDATION REPORT

1. **Dataset source**: PlantDoc Dataset (Attempted to download from official repository: https://github.com/pratikkayal/PlantDoc-Dataset)
2. **License/source information**: Public research repository by Pratik Kayal.
3. **Download date**: 2026-09-22 (Attempted)
4. **Number of images**: NOT MEASURED
5. **Usable images**: NOT MEASURED
6. **Matched classes**: NOT MEASURED
7. **Unseen classes**: NOT MEASURED
8. **Duplicate findings**: NOT MEASURED
9. **Real-world accuracy**: NOT MEASURED
10. **Macro precision**: NOT MEASURED
11. **Macro recall**: NOT MEASURED
12. **Macro F1**: NOT MEASURED
13. **Weighted metrics**: NOT MEASURED
14. **Per-class performance**: NOT MEASURED
15. **Healthy safety**: NOT MEASURED
16. **Disease → Healthy errors**: NOT MEASURED
17. **Healthy → Disease errors**: NOT MEASURED
18. **Confidence analysis**: NOT MEASURED
19. **High-confidence errors**: NOT MEASURED
20. **OOD behavior**: NOT MEASURED
21. **PlantVillage vs real-world comparison**: NOT MEASURED
22. **Limitations**: Real-world/OOD testing could not be completed because the host machine repeatedly dropped the network connection to GitHub during the download phase (`[WinError 10054] An existing connection was forcibly closed by the remote host` and `curl 56 Recv failure: Connection was reset`). The model remains validated strictly on PlantVillage data (Phase 27C) with no independent field-testing evidence yet established.
23. **Recommendation for next development step**: Resolve the network instability preventing large dataset downloads, or deploy the model strictly in a controlled field test using the mobile application's camera interface for manual performance logging before full autonomous integration.
