document.addEventListener('DOMContentLoaded', () => {
    // Shared functionality
    const resultContainer = document.getElementById('result-container');

    // Handle Image Preview
    const imageUpload = document.getElementById('imageUpload');
    const previewImg = document.getElementById('preview-img');

    if (imageUpload) {
        imageUpload.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    previewImg.src = e.target.result;
                    previewImg.style.display = 'block';
                }
                reader.readAsDataURL(file);
            }
        });
    }

    // Handle Form Submission for Upload
    const uploadForm = document.getElementById('upload-form');
    if (uploadForm) {
        uploadForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(uploadForm);
            
            showLoading();
            try {
                const response = await fetch('/predict', {
                    method: 'POST',
                    body: formData
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Server error (${response.status}): ${errorText}`);
                }
                const data = await response.json();
                displayResult(data);
            } catch (error) {
                console.error('Error:', error);
                alert(`Prediction Failed: ${error.message}`);
            }
        });
    }

    // Camera Logic
    const video = document.getElementById('camera-feed');
    const captureBtn = document.getElementById('capture-btn');
    const canvas = document.createElement('canvas');

    if (video) {
        startCamera();
        
        captureBtn.addEventListener('click', async () => {
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            canvas.getContext('2d').drawImage(video, 0, 0);
            
            const imageData = canvas.toDataURL('image/jpeg');
            
            showLoading();
            try {
                const formData = new FormData();
                formData.append('image_data', imageData);
                
                const response = await fetch('/predict', {
                    method: 'POST',
                    body: formData
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Server error (${response.status}): ${errorText}`);
                }
                const data = await response.json();
                displayResult(data);
            } catch (error) {
                console.error('Error:', error);
                alert(`Camera Detection Failed: ${error.message}`);
            }
        });
    }

    async function startCamera() {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: "environment" } });
            video.srcObject = stream;
        } catch (err) {
            console.error("Error accessing camera: ", err);
            alert("Could not access camera. Please ensure permissions are granted.");
        }
    }

    function showLoading() {
        if (resultContainer) {
            resultContainer.innerHTML = '<div class="loader">Analyzing leaf pattern... Please wait.</div>';
            resultContainer.style.display = 'block';
        }
    }

    function displayResult(data) {
        if (data.error) {
            resultContainer.innerHTML = `<div class="result-card" style="border-left-color: #d32f2f;">
                <h3>Error</h3>
                <p>${data.error}</p>
            </div>`;
            return;
        }

        // Store result in sessionStorage to persist to result page if needed, 
        // but for now we update the DOM directly for seamless experience
        let warningHtml = '';
        if (data.mock_mode) {
            let errorDetail = '';
            if (data.gemini_error) {
                if (data.gemini_error.includes('429')) {
                    errorDetail = '<br><span style="color: #c62828;">Note: AI is currently busy (Quota Exceeded). Showing estimated results based on image pattern.</span>';
                } else {
                    errorDetail = `<br><small style="color: #616161;">Diagnostic: ${data.gemini_error.split('\n')[0]}</small>`;
                }
            }
            warningHtml = `
                <div class="simulation-warning">
                    <span>⚠️ <strong>Smart Simulation Active:</strong> Gemini AI is currently reaching its request limit. We've used pattern matching to provide a relevant diagnosis.${errorDetail}</span>
                </div>
            `;
        }

        const isHealthy = data.disease.toLowerCase().includes('healthy');
        const statusClass = isHealthy ? 'status-healthy' : 'status-diseased';

        resultContainer.innerHTML = `
            ${warningHtml}
            <div class="result-card" ${data.mock_mode ? 'style="border-left-color: #ffb74d;"' : ''}>
                <div class="result-header">
                    <h2 class="${statusClass}">${data.disease}</h2>
                    <span class="confidence-badge" ${data.mock_mode ? 'style="background: #ffb74d;"' : ''}>Confidence: ${data.confidence}%</span>
                </div>
                <p><strong>Target Crop:</strong> ${data.crop}</p>
                <div style="margin-top: 1rem;">
                    <strong>Recommended Treatment:</strong>
                    <ul style="margin-top: 0.5rem; color: #33691e; padding-left: 1.5rem; list-style-type: decimal;">
                        ${(Array.isArray(data.treatment) ? data.treatment : data.treatment.split(/\.\s+/))
                            .filter(p => p.trim().length > 3)
                            .slice(0, 10)
                            .map(p => {
                                let point = p.trim();
                                if (point && !point.endsWith('.')) point += '.';
                                return `<li style="margin-bottom: 0.3rem;">${point}</li>`;
                            })
                            .join('')}
                    </ul>
                </div>
                <div style="margin-top: 1.5rem; display: flex; gap: 10px;">
                    <button onclick="window.print()" class="btn btn-secondary" style="font-size: 0.8rem; padding: 8px 15px;">
                        <i class="fas fa-print"></i> Save/Print Report
                    </button>
                    <a href="/" class="btn" style="font-size: 0.8rem; padding: 8px 15px; background: #2e7d32;">
                        <i class="fas fa-redo"></i> New Scan
                    </a>
                </div>
            </div>
        `;
        resultContainer.scrollIntoView({ behavior: 'smooth' });
    }
});
