import sqlite3
import os

db_path = r'c:\Users\BRINDHA\OneDrive\Desktop\New folder\crop_disease_assistant\backend\instance\crop_care_api.db'

if not os.path.exists(db_path):
    print(f"Database not found at {db_path}")
else:
    try:
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
        tables = cursor.fetchall()
        print(f"Tables found: {tables}")
        
        for table in tables:
            name = table[0]
            cursor.execute(f"PRAGMA table_info({name});")
            info = cursor.fetchall()
            print(f"Columns in {name}: {info}")
            
            cursor.execute(f"SELECT COUNT(*) FROM {name};")
            count = cursor.fetchone()[0]
            print(f"Row count in {name}: {count}")
            
        conn.close()
    except Exception as e:
        print(f"Error checking database: {e}")
