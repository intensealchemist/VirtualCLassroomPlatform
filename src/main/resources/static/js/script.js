document.addEventListener('DOMContentLoaded', function () {
  // Hamburger toggle (if present)
  const hamburgerBtn = document.querySelector('.hamburger-menu-btn');
  const navLinks = document.querySelector('.nav-links');
  if (hamburgerBtn && navLinks) {
    hamburgerBtn.addEventListener('click', () => {
      navLinks.classList.toggle('active');
    });
  }

  // Typed.js effect (if library and target element are present)
  if (window.Typed && document.querySelector('.typing-effect')) {
    new Typed('.typing-effect', {
      strings: ['-', '<br>', '-'],
      typeSpeed: 40,
      backSpeed: 0,
      loop: false,
    });
  }

  // File upload demo (if form is present)
  const fileUploadForm = document.getElementById('file-upload-form');
  if (fileUploadForm) {
    fileUploadForm.addEventListener('submit', (event) => {
      event.preventDefault();
      const fileInput = document.getElementById('file-upload');
      const file = fileInput ? fileInput.files[0] : null;
      console.log('File selected:', file);
      if (fileInput) fileInput.value = '';
    });
  }

  // Footer year auto-increment (if footer span present)
  const yearEl = document.getElementById('current-year');
  if (yearEl) {
    yearEl.textContent = new Date().getFullYear();
  }
});
