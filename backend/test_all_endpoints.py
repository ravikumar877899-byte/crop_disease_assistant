import requests
import json
import base64
import os
import time
import sys

# Ensure UTF-8 output on Windows
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding='utf-8')

BASE_URL = "http://127.0.0.1:5000"
session = requests.Session()

print("=" * 60)
print("STARTING AI CROP CARE BACKEND REST API TESTS")
print("=" * 60)

results = {}

# Test 1: Register
print("\n[TEST 1] Testing /api/auth/register...")
reg_payload = {
    "name": f"Farmer Test {int(time.time())}",
    "email": f"farmer_{int(time.time())}@cropcare.ai",
    "password": "securepassword123",
    "location": "Tamil Nadu",
    "language": "ta"
}
try:
    r = session.post(f"{BASE_URL}/api/auth/register", json=reg_payload, timeout=10)
    print(f"Status Code: {r.status_code}")
    print(f"Response: {r.text}")
    assert r.status_code in [200, 201], f"Expected 200/201, got {r.status_code}"
    results["/api/auth/register"] = "PASS"
except Exception as e:
    print(f"FAIL: {e}")
    results["/api/auth/register"] = f"FAIL: {e}"

# Test 2: Login
print("\n[TEST 2] Testing /api/auth/login...")
login_payload = {
    "email": reg_payload["email"],
    "password": reg_payload["password"]
}
try:
    r = session.post(f"{BASE_URL}/api/auth/login", json=login_payload, timeout=10)
    print(f"Status Code: {r.status_code}")
    print(f"Response: {r.text}")
    assert r.status_code == 200, f"Expected 200, got {r.status_code}"
    assert "user" in r.json(), "Missing user in response"
    results["/api/auth/login"] = "PASS"
except Exception as e:
    print(f"FAIL: {e}")
    results["/api/auth/login"] = f"FAIL: {e}"

# Test 3: Get current user (/api/auth/me)
print("\n[TEST 3] Testing /api/auth/me...")
try:
    r = session.get(f"{BASE_URL}/api/auth/me", timeout=10)
    print(f"Status Code: {r.status_code}")
    print(f"Response: {r.text}")
    assert r.status_code == 200, f"Expected 200, got {r.status_code}"
    assert r.json().get("authenticated") == True, "Expected authenticated == True"
    results["/api/auth/me"] = "PASS"
except Exception as e:
    print(f"FAIL: {e}")
    results["/api/auth/me"] = f"FAIL: {e}"

# Test 4: AI Leaf Upload Prediction (/api/predict/upload)
print("\n[TEST 4] Testing /api/predict/upload with real crop leaf image...")
sample_img_path = os.path.join(os.path.dirname(__file__), "..", "uploads", "1.2.jpg")
if not os.path.exists(sample_img_path):
    sample_img_path = os.path.join(os.path.dirname(__file__), "..", "uploads", "test.jpg")

try:
    with open(sample_img_path, "rb") as img_file:
        files = {"file": ("leaf.jpg", img_file, "image/jpeg")}
        data = {"lang": "ta"}
        r = session.post(f"{BASE_URL}/api/predict/upload", files=files, data=data, timeout=30)
    print(f"Status Code: {r.status_code}")
    print(f"Response: {r.text}")
    assert r.status_code == 200, f"Expected 200, got {r.status_code}"
    pred = r.json()
    assert "crop" in pred, "Missing crop field"
    assert "disease" in pred, "Missing disease field"
    print(f"-> Detected Crop: {pred.get('crop')} ({pred.get('crop_ta')})")
    print(f"-> Detected Disease: {pred.get('disease')} ({pred.get('disease_ta')})")
    print(f"-> AI Confidence: {pred.get('confidence')}%")
    print(f"-> Treatment Advice: {pred.get('treatment')}")
    results["/api/predict/upload"] = "PASS"
except Exception as e:
    print(f"FAIL: {e}")
    results["/api/predict/upload"] = f"FAIL: {e}"

# Test 5: Camera Base64 Prediction (/api/predict/camera)
print("\n[TEST 5] Testing /api/predict/camera with base64 image data...")
try:
    with open(sample_img_path, "rb") as img_file:
        b64_str = base64.b64encode(img_file.read()).decode('utf-8')
    cam_payload = {
        "image": f"data:image/jpeg;base64,{b64_str}",
        "lang": "en"
    }
    r = session.post(f"{BASE_URL}/api/predict/camera", json=cam_payload, timeout=30)
    print(f"Status Code: {r.status_code}")
    print(f"Response: {r.text}")
    assert r.status_code == 200, f"Expected 200, got {r.status_code}"
    results["/api/predict/camera"] = "PASS"
except Exception as e:
    print(f"FAIL: {e}")
    results["/api/predict/camera"] = f"FAIL: {e}"

# Test 6: Krishi AI Chatbot (/api/chatbot)
print("\n[TEST 6] Testing /api/chatbot...")
chat_payload = {
    "message": "How to treat leaf spot in tomato plants?",
    "language": "en"
}
try:
    r = session.post(f"{BASE_URL}/api/chatbot", json=chat_payload, timeout=25)
    print(f"Status Code: {r.status_code}")
    print(f"Response: {r.text}")
    assert r.status_code == 200, f"Expected 200, got {r.status_code}"
    assert "response" in r.json(), "Missing response field"
    results["/api/chatbot"] = "PASS"
except Exception as e:
    print(f"FAIL: {e}")
    results["/api/chatbot"] = f"FAIL: {e}"

# Test 7: Scan History (/api/history)
print("\n[TEST 7] Testing /api/history...")
history_record_id = None
try:
    r = session.get(f"{BASE_URL}/api/history", timeout=10)
    print(f"Status Code: {r.status_code}")
    print(f"Response: {r.text}")
    assert r.status_code == 200, f"Expected 200, got {r.status_code}"
    history_items = r.json().get("history", [])
    print(f"-> Total Scan Records in Database: {len(history_items)}")
    if history_items:
        history_record_id = history_items[0]["id"]
    results["/api/history"] = "PASS"
except Exception as e:
    print(f"FAIL: {e}")
    results["/api/history"] = f"FAIL: {e}"

# Test 8: Single History Detail (/api/history/<id>)
if history_record_id:
    print(f"\n[TEST 8] Testing /api/history/{history_record_id}...")
    try:
        r = session.get(f"{BASE_URL}/api/history/{history_record_id}", timeout=10)
        print(f"Status Code: {r.status_code}")
        print(f"Response: {r.text}")
        assert r.status_code == 200, f"Expected 200, got {r.status_code}"
        results[f"/api/history/<id>"] = "PASS"
    except Exception as e:
        print(f"FAIL: {e}")
        results[f"/api/history/<id>"] = f"FAIL: {e}"

# Test 9: Delete History Item (/api/history/<id>)
if history_record_id:
    print(f"\n[TEST 9] Testing DELETE /api/history/{history_record_id}...")
    try:
        r = session.delete(f"{BASE_URL}/api/history/{history_record_id}", timeout=10)
        print(f"Status Code: {r.status_code}")
        print(f"Response: {r.text}")
        assert r.status_code == 200, f"Expected 200, got {r.status_code}"
        results[f"DELETE /api/history/<id>"] = "PASS"
    except Exception as e:
        print(f"FAIL: {e}")
        results[f"DELETE /api/history/<id>"] = f"FAIL: {e}"

# Test 10: Logout (/api/auth/logout)
print("\n[TEST 10] Testing /api/auth/logout...")
try:
    r = session.post(f"{BASE_URL}/api/auth/logout", timeout=10)
    print(f"Status Code: {r.status_code}")
    print(f"Response: {r.text}")
    assert r.status_code == 200, f"Expected 200, got {r.status_code}"
    results["/api/auth/logout"] = "PASS"
except Exception as e:
    print(f"FAIL: {e}")
    results["/api/auth/logout"] = f"FAIL: {e}"

print("\n" + "=" * 60)
print("BACKEND REST API TEST SUMMARY")
print("=" * 60)
for endpoint, status in results.items():
    print(f"{endpoint:35}: {status}")
