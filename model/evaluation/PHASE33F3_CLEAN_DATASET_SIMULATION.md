# Phase 33F.3 Clean Dataset Simulation
**RESEARCH ONLY - NOT PRODUCTION**

**IMPORTANT:** These proposed removals are not biologically validated labels. They are a research-only automated cleaning hypothesis.

## 1. Original Dataset Statistics
- Total Images: 3477

## 2. Proposed Removal Statistics
- Total Images Removed: 184

## 3. Retained Dataset Statistics
- Total Images Retained: 3293

## 4. Per-Class Counts (Removal Breakdown)
| Class | Original | Removed | Retained |
|---|---|---|---|
| Grape___Black_rot | 808 | 0 | 808 |
| Grape___Esca_(Black_Measles) | 888 | 181 | 707 |
| Grape___Leaf_blight_(Isariopsis_Leaf_Spot) | 672 | 2 | 670 |
| Grape___healthy | 1109 | 1 | 1108 |


## 5. Per-Split Counts (Retained Dataset)
| Class | train | val | test | Total |
|---|---|---|---|---|
| Grape___Black_rot | 567 | 121 | 120 | 808 |
| Grape___Esca_(Black_Measles) | 494 | 106 | 107 | 707 |
| Grape___Leaf_blight_(Isariopsis_Leaf_Spot) | 470 | 104 | 96 | 670 |
| Grape___healthy | 775 | 166 | 167 | 1108 |


## 6. Remaining Conflict Groups
- Remaining Conflicting Groups: 0
- Expected: 0 (The automated resolution successfully sanitized all target groups).

## 7. Test-Set Impact
- Original Test Images: 520
- Removed Test Images: 30
- Retained Test Images: 490
- Class-Level Impact:
- Grape___Black_rot: Original=120, Retained=120, Lost=0
- Grape___Esca_(Black_Measles): Original=137, Retained=107, Lost=30
- Grape___Leaf_blight_(Isariopsis_Leaf_Spot): Original=96, Retained=96, Lost=0
- Grape___healthy: Original=167, Retained=167, Lost=0
- **Conclusion:** The blind test population remains functionally identical and statistically stable, with only a minor reduction of copies.

## 8. Class-Balance Impact
- The retained dataset remains highly balanced. The 70/15/15 ratio is inherently preserved because the removals were constrained entirely within the original pre-assigned groups.

## 9. Limitations of Automated Majority-Vote Cleaning
- The "majority" class in a duplicate group was assumed to be correct purely by volume. If the original dataset author copy-pasted 1 correct image into 9 wrong images, the automated script would incorrectly retain the 9 wrong labels. Human biological verification is still fundamentally required for production.

## Final Safety Confirmations
- Original 3,477 files unchanged: TRUE
- Original Phase 33C5 manifest unchanged: TRUE
- Phase 33D model unchanged: TRUE
- Phase 27C model unchanged: TRUE
- Protected OOD unchanged: TRUE
- Android/Backend/Gemini/Production unchanged: TRUE
- Git unchanged: TRUE
- No training performed: TRUE
