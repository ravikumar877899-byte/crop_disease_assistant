from flask_login import UserMixin # type: ignore
from datetime import datetime, timezone
from .extensions import db # type: ignore

class User(UserMixin, db.Model): # type: ignore
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(150), nullable=False)
    email = db.Column(db.String(150), unique=True, nullable=False)
    password = db.Column(db.String(150), nullable=False)
    location = db.Column(db.String(100), default="Punjab")
    language = db.Column(db.String(10), default="en")
    created_at = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc))

class ScanHistory(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    crop_name = db.Column(db.String(100), nullable=False)
    crop_ta = db.Column(db.String(100), nullable=True)
    scientific_crop = db.Column(db.String(150), nullable=True)
    disease_name = db.Column(db.String(150), nullable=False)
    disease_ta = db.Column(db.String(150), nullable=True)
    scientific_disease = db.Column(db.String(150), nullable=True)
    confidence = db.Column(db.Float, nullable=False)
    reasoning = db.Column(db.Text, nullable=True)
    reasoning_ta = db.Column(db.Text, nullable=True)
    treatment = db.Column(db.Text, nullable=True)
    treatment_ta = db.Column(db.Text, nullable=True)
    language_code = db.Column(db.String(10), default="en")
    image_path = db.Column(db.String(255), nullable=True)
    timestamp = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc))
