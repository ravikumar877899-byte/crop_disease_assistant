import google.generativeai as genai # type: ignore
import os
import sys
from typing import Optional, cast, Any
from dotenv import load_dotenv # type: ignore

load_dotenv('.env')
# Explicitly cast to Optional[str] to satisfy linter
raw_key: Optional[str] = os.getenv("GEMINI_API_KEY")

if not raw_key:
    print("API Key NOT found in env")
    sys.exit(1)

# Clean and store the key
api_key: str = str(raw_key).strip()

print(f"Testing API Key: {api_key[:10]}...") # type: ignore

try:
    genai.configure(api_key=api_key)
    models = genai.list_models()
    print("Successfully listed models:")
    # Using cast to help the linter understand the iterator type
    for m in cast(Any, models): 
        if 'generateContent' in m.supported_generation_methods:
            print(f"- {m.name}")
except Exception as e:
    print(f"API Key test failed: {e}")
