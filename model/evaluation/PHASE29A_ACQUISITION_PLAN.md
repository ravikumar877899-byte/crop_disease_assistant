# PHASE 29A ACQUISITION PLAN

## Selected Dataset
**Candidate 2: PlantDoc-Full (Hugging Face)**

## Verification Details
- **Official source URL**: https://huggingface.co/datasets/geraldmc/plantdoc-full
- **Dataset name/version**: `geraldmc/plantdoc-full` (default config)
- **License**: MIT / Academic (inherited from original PlantDoc).
- **Approximate size**: ~850 MB, heavily compressed into chunked Parquet files.
- **Expected files**: Hugging Face datasets parquet format (PIL Image encoded), mapping to individual JPEGs on extraction.
- **Expected classes**: Includes Apple Scab, Apple Rust, Corn Leaf Blight, Corn Rust, Grape Black Rot, Potato Early/Late Blight, Tomato Early Blight, and Tomato Septoria, among others.
- **Download method**: Python `datasets` library fetching from Hugging Face hub. This method natively supports resumable, chunked downloading which will bypass the single-file network timeout errors previously encountered.

## Acquisition Strategy
1. Load dataset via `datasets.load_dataset('geraldmc/plantdoc-full', split='train')` (and `test`).
2. Iterate over the dataset and filter for classes overlapping with our 18 trained classes (e.g., Apple, Corn, Grape, Potato, Tomato).
3. Save the images physically as JPEGs into `dataset/real_world_test/<class_name>/` using `PIL.Image.save()`.
4. Generate the `real_world_source_metadata.csv` during the extraction process with SHA-256 calculation.
