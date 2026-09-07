# AI-Based Crop Disease Detection and Treatment Assistant

A full-stack AI-powered assistant designed for farmers to detect crop diseases from leaf images and receive treatment recommendations.

## Project Structure Overview

- **backend/app.py**: The heart of the application. Handles routing, image processing, and AI model inference.
- **frontend/static/**: Contains the CSS and JS files for styling and client-side logic.
- **frontend/templates/**: Contains the HTML pages for the user interface.
- **model/crop_disease_model.h5**: The trained deep learning CNN model file.
- **model/class_indices.json**: Mapping between model outputs and disease names/treatments.
- **model/train_dummy_model.py**: A script to generate a placeholder model for project demonstration.
- **uploads/**: Destination folder for images uploaded by users for detection.
- **requirements.txt**: List of Python libraries required to run the project.

## Features

- **Home Page**: Overview of the project mission.
- **Upload System**: Selection of local images for analysis.
- **Live Camera Feed**: Real-time leaf capture using the device camera.
- **Dynamic Diagnosis**: Instant disease prediction with confidence scores.
- **Treatment Guide**: Actionable advice for curing the detected disease.

## How to Run the Project

1. **Install Python**: Ensure Python 3.8+ is installed.
2. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```
3. **Generate Dummy Model** (Optional if you already have a model):
   ```bash
   python model/train_dummy_model.py
   ```
4. **Start the Flask Server**:
   ```bash
   cd backend
   python app.py
   ```
5. **Access the App**: Open your browser and go to `http://127.0.0.1:5000/`.

## Model Information
The implementation uses a CNN (Convolutional Neural Network) architecture optimized for 224x224 RGB images. It currently supports 15 specific disease classifications across various crops like Apple, Corn, Grape, Potato, Rice, and Tomato.
