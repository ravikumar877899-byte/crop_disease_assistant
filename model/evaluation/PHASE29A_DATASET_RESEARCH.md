# PHASE 29A DATASET RESEARCH

## Candidate 1: PlantDoc (Original GitHub)
1. **Dataset name**: PlantDoc
2. **Original source**: Pratik Kayal / Indian Institute of Technology
3. **Official URL**: https://github.com/pratikkayal/PlantDoc-Dataset
4. **Dataset size**: ~2,598 images
5. **Image type**: JPEG/PNG
6. **Crop coverage**: Apple, Corn, Grape, Potato, Tomato, and others.
7. **Disease coverage**: 17 diseases + healthy classes.
8. **Real-world/field-photo status**: Real-world field images, non-lab conditions (varying lighting, backgrounds).
9. **License**: MIT License (for the repository), academic usage permitted.
10. **Commercial/research use**: Research use highly recommended/allowed.
11. **Download method**: Git clone / ZIP download.
12. **Download size**: ~900 MB
13. **Resumable download**: No (causes network reset on this host).
14. **Independent from PlantVillage**: Yes (explicitly curated to address PlantVillage limitations).
15. **Overlapping classes**: 8-10 overlapping classes with our 18 trained classes.
16. **Limitations**: Huge monolithic ZIP file fails to download on current unstable network.

## Candidate 2: PlantDoc-Full (Hugging Face)
1. **Dataset name**: plantdoc-full
2. **Original source**: Hugging Face (uploaded by geraldmc), derived from original PlantDoc.
3. **Official URL**: https://huggingface.co/datasets/geraldmc/plantdoc-full
4. **Dataset size**: ~2,598 images
5. **Image type**: Parquet encoded (decodes to PIL Images / JPEG/PNG).
6. **Crop coverage**: Same as PlantDoc.
7. **Disease coverage**: Same as PlantDoc.
8. **Real-world/field-photo status**: Real-world field images.
9. **License**: Same as original PlantDoc (MIT / academic).
10. **Commercial/research use**: Research use allowed.
11. **Download method**: Hugging Face Datasets API (Parquet chunks).
12. **Download size**: ~850 MB total, split into smaller chunks.
13. **Resumable download**: Yes (file-level chunking via API).
14. **Independent from PlantVillage**: Yes.
15. **Overlapping classes**: 8-10 overlapping classes.
16. **Limitations**: Requires `datasets` library for robust parsing.

## Candidate 3: Paddy Doctor
1. **Dataset name**: Paddy Doctor
2. **Original source**: Kaggle / Academic Challenge
3. **Official URL**: https://www.kaggle.com/competitions/paddy-disease-classification
4. **Dataset size**: ~10,000+ images
5. **Image type**: JPEG
6. **Crop coverage**: Rice (Paddy)
7. **Disease coverage**: Rice diseases (Blast, Blight, Tungro, etc.)
8. **Real-world/field-photo status**: Yes.
9. **License**: Kaggle competition rules / Academic.
10. **Commercial/research use**: Research use.
11. **Download method**: Kaggle API.
12. **Download size**: ~1 GB.
13. **Resumable download**: Yes.
14. **Independent from PlantVillage**: Yes.
15. **Overlapping classes**: 0 overlapping classes with our 18 model classes.
16. **Limitations**: No overlap with Apple, Corn, Grape, Potato, Tomato. Unusable for direct accuracy validation of our 18 classes.
