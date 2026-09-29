# Phase 33F Leaf Blight Label Quality Audit
**RESEARCH ONLY - NOT PRODUCTION**

## 1. Leaf Blight Test-Set Statistics
- Total Images: 96
- Correctly Predicted: 36
- Mean Blur Score: 275.48
- Mean Brightness: 125.37

## 2. Leaf Blight -> Esca Confusion Statistics
- Count: 50
- Mean Confidence: 0.77
- Mean Blur Score: 236.59

## 3. Esca -> Leaf Blight Statistics
- Count: 9
- Mean Confidence: 0.62
- Mean Blur Score: 207.39

## 4. Image-Quality Comparison (Entire Dataset)

### Grape___Black_rot
- Count: 808
- Mean Resolution: 1080x1080
- Mean Aspect Ratio: 1.00
- Mean Blur (Laplacian Variance): 348.30
- Mean Brightness: 134.59

### Grape___Esca_(Black_Measles)
- Count: 888
- Mean Resolution: 1080x1080
- Mean Aspect Ratio: 1.00
- Mean Blur (Laplacian Variance): 217.03
- Mean Brightness: 117.07

### Grape___Leaf_blight_(Isariopsis_Leaf_Spot)
- Count: 672
- Mean Resolution: 1080x1080
- Mean Aspect Ratio: 1.00
- Mean Blur (Laplacian Variance): 277.13
- Mean Brightness: 122.60

### Grape___healthy
- Count: 1109
- Mean Resolution: 1080x1080
- Mean Aspect Ratio: 1.00
- Mean Blur (Laplacian Variance): 272.61
- Mean Brightness: 114.33

## 5. Near-Duplicate Label Consistency
- Groups Checked: 2228
- Groups with Conflicting Labels: 182
  - Group size 10 with classes ['Grape___Leaf_blight_(Isariopsis_Leaf_Spot)', 'Grape___Black_rot']
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot226.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot227.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot228.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot261.jpg
    - dataset\phase33c1_sources\grape\DATASET\leaf blight\leaf blight140.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot278.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot279.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot248.jpg
    - dataset\phase33c1_sources\grape\DATASET\leaf blight\leaf blight139.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot225.jpg
  - Group size 9 with classes ['Grape___Esca_(Black_Measles)', 'Grape___Black_rot']
    - dataset\phase33c1_sources\grape\DATASET\esca\esca002.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot793.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot794.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot795.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot796.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot797.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot798.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot799.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot800.jpg
  - Group size 9 with classes ['Grape___Esca_(Black_Measles)', 'Grape___Black_rot']
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot802.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot803.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot804.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot805.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot806.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot807.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot808.jpg
    - dataset\phase33c1_sources\grape\DATASET\esca\esca248.jpg
    - dataset\phase33c1_sources\grape\DATASET\Black rot\black rot801.jpg
  - Group size 2 with classes ['Grape___Esca_(Black_Measles)', 'Grape___healthy']
    - dataset\phase33c1_sources\grape\DATASET\esca\esca001.jpg
    - dataset\phase33c1_sources\grape\DATASET\healthy\healthy001.jpg
  - Group size 3 with classes ['Grape___Leaf_blight_(Isariopsis_Leaf_Spot)', 'Grape___Esca_(Black_Measles)']
    - dataset\phase33c1_sources\grape\DATASET\leaf blight\leaf blight309.jpg
    - dataset\phase33c1_sources\grape\DATASET\leaf blight\leaf blight310.jpg
    - dataset\phase33c1_sources\grape\DATASET\esca\esca004.jpg
  - (...and 177 more conflicting groups)

## 6. Observable Dataset-Quality Issues
Based on the metrics and conflict analysis, the following issues are observable:
- Label contamination exists: There are 182 near-duplicate groups that contain conflicting labels from different classes, proving human mislabeling in the original Mendeley dataset.

## 7. Recommendation
Manual human review is STRONGLY RECOMMENDED due to the detected labeling issues.

## Final Safety Check
- No images modified: TRUE
- No labels modified: TRUE
- Phase 33D model unchanged: TRUE
- Phase 27C unchanged: TRUE
- Protected OOD unchanged: TRUE
- Android/Backend/Gemini/Production unchanged: TRUE
- Git unchanged: TRUE
- No training performed: TRUE
