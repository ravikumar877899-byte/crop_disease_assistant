import google.generativeai as genai # type: ignore
import os
from dotenv import load_dotenv # type: ignore

load_dotenv('backend/.env')
# Test Key 1 specifically
api_key = os.getenv("GEMINI_API_KEY")

if not api_key:
    print("TEST FAILED: GEMINI_API_KEY not found in .env")
else:
    print(f"Testing primary key (Key 1): {str(api_key)[:10]}...") # type: ignore
    try:
        genai.configure(api_key=api_key.strip())
        print("Authorized Models for this Key:")
        models = [m.name for m in genai.list_models() if 'generateContent' in m.supported_generation_methods]
        for name in models:
            print(f"- {name}")
        
        if models:
            print("--- STATUS: SUCCESS! This key is ACTIVE and AUTHORIZED. ---")
        else:
            print("--- STATUS: WARNING! Key is valid but has no generative models. ---")
    except Exception as e:
        print(f"--- STATUS: ERROR - {str(e)} ---")
