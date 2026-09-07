import google.generativeai as genai # type: ignore
import os
import sys
from dotenv import load_dotenv # type: ignore

load_dotenv('backend/.env')
api_key = os.getenv("GEMINI_API_KEY")

if not api_key:
    print("TEST FAILED: GEMINI_API_KEY not found in .env")
    sys.exit(1)

print(f"--- TESTING NEW PRIMARY KEY: {str(api_key)[:10]}... ---") # type: ignore

try:
    genai.configure(api_key=str(api_key).strip()) # type: ignore
    print("Checking available models for this key...")
    
    # Discovery loop
    models = genai.list_models()
    model_names = [m.name for m in models if 'generateContent' in m.supported_generation_methods]
    
    if not model_names:
         print("!!! STATUS: FAILED. No generative models found for this key. !!!")
         sys.exit(1)
         
    model_to_use = model_names[0]
    print(f"Using model: {model_to_use}")
    
    model = genai.GenerativeModel(model_to_use)
    print("Sending test greeting...")
    response = model.generate_content("Hello! Confirm you are active.")
    
    if response and response.text:
        print(f"Response: {response.text.strip()}")
        print("--- STATUS: SUCCESS! Key is active and ready. ---")
    else:
        print("!!! STATUS: FAILED (Empty response) !!!")
except Exception as e:
    print(f"!!! STATUS: ERROR - {str(e)[:150]} !!!") # type: ignore
