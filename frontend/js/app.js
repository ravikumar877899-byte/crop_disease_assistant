/**
 * AI CROP CARE - Main Application JavaScript
 * AI Crop Disease Detection and Treatment Assistant
 * Phase 1: Project Foundation and Basic UI
 */

document.addEventListener("DOMContentLoaded", () => {
  // 1. Mobile Menu Toggle
  const mobileMenuBtn = document.getElementById("mobile-menu-btn");
  const navLinks = document.getElementById("nav-links");

  if (mobileMenuBtn && navLinks) {
    mobileMenuBtn.addEventListener("click", () => {
      navLinks.classList.toggle("active");
    });
  }

  // 2. Profile & Logout Placeholders
  const profileLink = document.getElementById("profile-link");
  const logoutLink = document.getElementById("logout-link");

  if (profileLink) {
    profileLink.addEventListener("click", (e) => {
      e.preventDefault();
      alert("👤 Farmer Profile functionality will be activated in Phase 2 (User Authentication & Database).");
    });
  }

  if (logoutLink) {
    logoutLink.addEventListener("click", (e) => {
      e.preventDefault();
      alert("🔒 Logout functionality will be activated in Phase 2 (User Authentication & Database).");
    });
  }

  // 3. Scan Page: File Upload, Drag & Drop, Camera, & Analyze Actions
  const fileInput = document.getElementById("crop-image-input");
  const cameraInput = document.getElementById("camera-image-input");
  const uploadZone = document.getElementById("upload-dropzone");
  const uploadBtn = document.getElementById("upload-btn");
  const openCameraBtn = document.getElementById("open-camera-btn");
  const previewContainer = document.getElementById("preview-container");
  const previewImage = document.getElementById("preview-image");
  const previewFilename = document.getElementById("preview-filename");
  const previewFilesize = document.getElementById("preview-filesize");
  const removeImageBtn = document.getElementById("remove-image-btn");
  const analyzeBtn = document.getElementById("analyze-crop-btn");
  const phaseNotice = document.getElementById("phase-notice");

  let selectedFile = null;

  function handleFileSelect(file) {
    if (!file) return;
    if (!file.type.startsWith("image/")) {
      alert("Please upload a valid image file (JPG, PNG, WEBP).");
      return;
    }

    selectedFile = file;

    const reader = new FileReader();
    reader.onload = (e) => {
      if (previewImage) previewImage.src = e.target.result;
      if (previewFilename) previewFilename.textContent = file.name;
      if (previewFilesize) {
        const sizeInKB = (file.size / 1024).toFixed(1);
        previewFilesize.textContent = `${sizeInKB} KB`;
      }
      if (previewContainer) previewContainer.style.display = "block";
      if (phaseNotice) phaseNotice.style.display = "none";
    };
    reader.readAsDataURL(file);
  }

  if (uploadBtn && fileInput) {
    uploadBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      fileInput.click();
    });
  }

  if (fileInput) {
    fileInput.addEventListener("change", (e) => {
      const file = e.target.files[0];
      handleFileSelect(file);
    });
  }

  if (openCameraBtn && cameraInput) {
    openCameraBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      cameraInput.click();
    });
  }

  if (cameraInput) {
    cameraInput.addEventListener("change", (e) => {
      const file = e.target.files[0];
      handleFileSelect(file);
    });
  }

  if (uploadZone) {
    uploadZone.addEventListener("click", () => {
      if (fileInput) fileInput.click();
    });

    uploadZone.addEventListener("dragover", (e) => {
      e.preventDefault();
      uploadZone.classList.add("dragover");
    });

    uploadZone.addEventListener("dragleave", () => {
      uploadZone.classList.remove("dragover");
    });

    uploadZone.addEventListener("drop", (e) => {
      e.preventDefault();
      uploadZone.classList.remove("dragover");
      if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
        handleFileSelect(e.dataTransfer.files[0]);
      }
    });
  }

  if (removeImageBtn) {
    removeImageBtn.addEventListener("click", () => {
      selectedFile = null;
      if (fileInput) fileInput.value = "";
      if (cameraInput) cameraInput.value = "";
      if (previewImage) previewImage.src = "";
      if (previewContainer) previewContainer.style.display = "none";
      if (phaseNotice) phaseNotice.style.display = "none";
    });
  }

  if (analyzeBtn) {
    analyzeBtn.addEventListener("click", () => {
      if (!selectedFile) {
        alert("Please select or capture a crop leaf image first before analyzing.");
        return;
      }

      // Display Phase 1 Notice as required
      if (phaseNotice) {
        phaseNotice.style.display = "flex";
        phaseNotice.scrollIntoView({ behavior: "smooth", block: "nearest" });
      }
    });
  }

  // 4. Chatbot Page: Krishi AI Chat Interactivity
  const chatForm = document.getElementById("chat-form");
  const chatInput = document.getElementById("chat-input");
  const chatMessages = document.getElementById("chat-messages");
  const suggestionChips = document.querySelectorAll(".suggestion-chip");

  function appendChatMessage(sender, text) {
    if (!chatMessages) return;

    const bubble = document.createElement("div");
    bubble.classList.add("chat-bubble", sender);

    const avatar = document.createElement("div");
    avatar.classList.add("bubble-avatar");
    avatar.textContent = sender === "bot" ? "🤖" : "👨‍🌾";

    const content = document.createElement("div");
    content.classList.add("bubble-text");
    content.textContent = text;

    bubble.appendChild(avatar);
    bubble.appendChild(content);

    chatMessages.appendChild(bubble);
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }

  function sendChatMessage(text) {
    if (!text || text.trim() === "") return;

    // Append farmer message
    appendChatMessage("user", text.trim());
    if (chatInput) chatInput.value = "";

    // Simulate Krishi AI response with required placeholder message
    setTimeout(() => {
      const response = "Hello! I am Krishi AI. I will help you with crop-related questions. (Full AI conversation will be activated in an upcoming phase).";
      appendChatMessage("bot", response);
    }, 400);
  }

  if (chatForm && chatInput) {
    chatForm.addEventListener("submit", (e) => {
      e.preventDefault();
      sendChatMessage(chatInput.value);
    });
  }

  suggestionChips.forEach((chip) => {
    chip.addEventListener("click", () => {
      sendChatMessage(chip.textContent);
    });
  });

  // 5. Backend Status Ping Check (Optional Enhancement for Phase 1 verification)
  const backendStatusPill = document.getElementById("backend-status-pill");
  if (backendStatusPill) {
    fetch("http://127.0.0.1:5000/api/health")
      .then((res) => res.json())
      .then((data) => {
        if (data.status === "success") {
          backendStatusPill.textContent = "🟢 Backend Online";
          backendStatusPill.className = "badge badge-green";
        }
      })
      .catch(() => {
        backendStatusPill.textContent = "⚪ Local UI Mode";
        backendStatusPill.className = "badge badge-amber";
      });
  }
});
