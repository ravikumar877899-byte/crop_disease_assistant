import tensorflow as tf # type: ignore
import os

def create_and_save_dummy_model():
    # Define a simple CNN architecture
    model = tf.keras.models.Sequential([
        tf.keras.layers.Conv2D(32, (3, 3), activation='relu', input_shape=(224, 224, 3)),
        tf.keras.layers.MaxPooling2D((2, 2)),
        tf.keras.layers.Conv2D(64, (3, 3), activation='relu'),
        tf.keras.layers.MaxPooling2D((2, 2)),
        tf.keras.layers.Flatten(),
        tf.keras.layers.Dense(128, activation='relu'),
        tf.keras.layers.Dense(23, activation='softmax') # 23 classes for the 23 diseases in class_indices.json
    ])

    model.compile(optimizer='adam',
                  loss='sparse_categorical_crossentropy',
                  metrics=['accuracy'])

    # Path to save the model
    model_dir = os.path.dirname(os.path.abspath(__file__))
    model_path = os.path.join(model_dir, 'crop_disease_model.h5')

    print(f"Generating dummy model at: {model_path}")
    model.save(model_path)
    print("Dummy model generated successfully.")

if __name__ == "__main__":
    create_and_save_dummy_model()
