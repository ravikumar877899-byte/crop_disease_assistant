"""
AI CROP CARE - User Authentication Database
SQLite database manager for Phase 4 authentication
"""

import os
import sqlite3
import secrets
from datetime import datetime, timezone
from werkzeug.security import generate_password_hash, check_password_hash

BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
DB_DIR = os.path.join(BASE_DIR, 'database')
DB_PATH = os.path.join(DB_DIR, 'aicropcare.db')

def get_db_connection():
    os.makedirs(DB_DIR, exist_ok=True)
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn

def init_db():
    conn = get_db_connection()
    cursor = conn.cursor()
    
    # Users Table
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            email TEXT UNIQUE NOT NULL,
            password_hash TEXT NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """)
    
    # Session Tokens Table
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS auth_tokens (
            token TEXT PRIMARY KEY,
            user_id INTEGER NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
        );
    """)
    
    conn.commit()
    conn.close()

def create_user(username, email, password):
    username = username.strip()
    email = email.strip().lower()
    
    if not username:
        return None, "Username is required."
    if not email or "@" not in email:
        return None, "A valid email address is required."
    if not password or len(password) < 6:
        return None, "Password must be at least 6 characters long."
        
    conn = get_db_connection()
    cursor = conn.cursor()
    
    # Check uniqueness
    cursor.execute("SELECT id FROM users WHERE username = ?", (username,))
    if cursor.fetchone():
        conn.close()
        return None, "Username already exists."
        
    cursor.execute("SELECT id FROM users WHERE email = ?", (email,))
    if cursor.fetchone():
        conn.close()
        return None, "Email address already registered."
        
    password_hash = generate_password_hash(password)
    
    try:
        cursor.execute(
            "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)",
            (username, email, password_hash)
        )
        user_id = cursor.lastrowid
        
        # Generate session token
        token = secrets.token_hex(32)
        cursor.execute(
            "INSERT INTO auth_tokens (token, user_id) VALUES (?, ?)",
            (token, user_id)
        )
        
        conn.commit()
        
        user = {
            "id": user_id,
            "username": username,
            "email": email
        }
        conn.close()
        return (user, token), None
    except Exception as e:
        conn.rollback()
        conn.close()
        return None, str(e)

def verify_user(username_or_email, password):
    identifier = username_or_email.strip()
    
    if not identifier or not password:
        return None, None, "Username/Email and password are required."
        
    conn = get_db_connection()
    cursor = conn.cursor()
    
    cursor.execute(
        "SELECT id, username, email, password_hash FROM users WHERE username = ? OR email = ?",
        (identifier, identifier.lower())
    )
    row = cursor.fetchone()
    
    if not row:
        conn.close()
        return None, None, "Invalid username or password."
        
    if not check_password_hash(row["password_hash"], password):
        conn.close()
        return None, None, "Invalid username or password."
        
    user_id = row["id"]
    token = secrets.token_hex(32)
    
    cursor.execute(
        "INSERT INTO auth_tokens (token, user_id) VALUES (?, ?)",
        (token, user_id)
    )
    conn.commit()
    
    user = {
        "id": user_id,
        "username": row["username"],
        "email": row["email"]
    }
    conn.close()
    return user, token, None

def get_user_by_token(token):
    if not token:
        return None
        
    conn = get_db_connection()
    cursor = conn.cursor()
    
    cursor.execute("""
        SELECT u.id, u.username, u.email, u.created_at
        FROM auth_tokens t
        JOIN users u ON t.user_id = u.id
        WHERE t.token = ?
    """, (token,))
    
    row = cursor.fetchone()
    conn.close()
    
    if row:
        return {
            "id": row["id"],
            "username": row["username"],
            "email": row["email"],
            "created_at": row["created_at"]
        }
    return None

def invalidate_token(token):
    if not token:
        return
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("DELETE FROM auth_tokens WHERE token = ?", (token,))
    conn.commit()
    conn.close()

# Auto-initialize database on import
init_db()
