const hamburgerBtn = document.querySelector('.hamburger-menu-btn');
const navLinks = document.querySelector('.nav-links');
hamburgerBtn.addEventListener('click', () => {
  navLinks.classList.toggle('active');
});
var typed = new Typed('.typing-effect', {
  strings: ["-", "<br>", "-"],
  typeSpeed: 40,
  backSpeed: 0,
  loop: false, 
});

const fileUploadForm = document.getElementById('file-upload-form');

fileUploadForm.addEventListener('submit', (event) => {
  event.preventDefault(); 
  const fileInput = document.getElementById('file-upload');
  const file = fileInput.files[0];

  console.log("File selected:", file); 
  fileInput.value = '';
});
