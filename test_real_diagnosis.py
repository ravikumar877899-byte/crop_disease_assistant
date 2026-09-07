import os
import sys

# Add backend to path
sys.path.append(os.path.join(os.path.dirname(__file__), 'backend'))

from PIL import Image # type: ignore
from app import predict_disease, GEMINI_AVAILABLE, GEMINI_KEYS

print(f"GEMINI_AVAILABLE: {GEMINI_AVAILABLE}")
print(f"API Keys count: {len(GEMINI_KEYS)}")

test_image = r'C:\Users\BRINDHA\OneDrive\Desktop\New folder\crop_disease_assistant\uploads\test.jpg'
# Create a dummy image if it doesn't exist just to test the logic
if not os.path.exists(test_image):
    img = Image.new('RGB', (100, 100), color = 'red')
    os.makedirs(os.path.dirname(test_image), exist_ok=True)
    img.save(test_image)

print(f"Testing diagnosis with: {test_image}")
result = predict_disease(None, image_path=test_image)

print("\n--- RESULT ---")
import json
print(json.dumps(result, indent=2))