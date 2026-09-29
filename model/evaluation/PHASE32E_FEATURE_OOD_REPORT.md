# Phase 32E Feature + Uncertainty OOD Research Report

## 1. Executive Summary
Evaluated Mahalanobis feature distance combined with confidence/margin/stability.

## 2. Integrity Checks
Model SHA: 994659f19ebd2ee26ebf17b628986d6a44089e30a23578f8c8bec6537d4a56cb. OOD Hashes verified untouched.

## 3. Threshold Methodology
Mahalanobis threshold computed dynamically from the 95th percentile of known dev datasets. Protected OOD remained perfectly blind.

## 4. Evaluation Results
### POLICY_A_RAW
- PV: Cov 100.0%, Acc 96.5%
- RW: Cov 100.0%, Acc 26.2%, D2H 58, H2D 152, HC-Healthy-Err 6
- OOD: FAR 100.0%, Rej 0.0%, HC 130

### POLICY_B_STRICT
- PV: Cov 76.1%, Acc 99.7%
- RW: Cov 14.4%, Acc 43.7%, D2H 4, H2D 3, HC-Healthy-Err 3
- OOD: FAR 6.7%, Rej 93.3%, HC 103

### POLICY_C_FEATURE_ONLY
- PV: Cov 92.8%, Acc 96.3%
- RW: Cov 61.2%, Acc 29.7%, D2H 26, H2D 67, HC-Healthy-Err 2
- OOD: FAR 62.0%, Rej 38.0%, HC 66

### POLICY_D_COMBINED
- PV: Cov 84.9%, Acc 99.0%
- RW: Cov 20.6%, Acc 42.1%, D2H 7, H2D 8, HC-Healthy-Err 2
- OOD: FAR 14.2%, Rej 85.8%, HC 66

## 5. Scientific Decision
C. UNSUCCESSFUL RESULT (or B. PARTIAL IMPROVEMENT). The feature manifold separation in MobileNetV3 is insufficient to reliably reject OOD imagery without damaging real-world coverage.
