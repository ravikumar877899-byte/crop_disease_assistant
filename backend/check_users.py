import sqlite3
import os

db_path = r'c:\Users\BRINDHA\OneDrive\Desktop\New folder\crop_disease_assistant\backend\instance\crop_care_api.db'

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT id, email, name FROM user;")
    users = cursor.fetchall()
    print(f"Users found: {users}")
    conn.close()
except Exception as e:
    print(f"Error checking users: {e}")
