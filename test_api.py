import os
import sys
from typing import cast
import google.generativeai as genai # type: ignore
from dotenv import load_dotenv # type: ignore

load_dotenv('backend/.env')
key = os.getenv("GEMINI_API_KEY")

if not key:
    print("API FAILURE: GEMINI_API_KEY not found in .env")
    sys.exit(1)

print(f"Testing key: {cast(str, key)[:10]}...") # type: ignore

try:
    genai.configure(api_key=str(key))
    print("Available models:")
    for m in genai.list_models():
        if 'generateContent' in m.supported_generation_methods:
            print(m.name)
except Exception as e:
    print("API FAILURE:", e)
