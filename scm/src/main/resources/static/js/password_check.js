// document.addEventListener("DOMContentLoaded", function () {
//   const password = document.getElementById("password");
//   const confirmPassword = document.getElementById("confirmPassword");
//   const signInButton = document.getElementById("signInButton");
//   const togglePassword = document.getElementById("togglePassword");
//   const toggleConfirmPassword = document.getElementById(
//     "toggleConfirmPassword"
//   );

//   function validatePasswords() {
//     if (password.value === confirmPassword.value && password.value !== "") {
//       signInButton.disabled = false;
//     } else {
//       signInButton.disabled = true;
//     }
//   }

//   function toggleVisibility(input, icon) {
//     if (input.type === "password") {
//       input.type = "text";
//       icon.classList.remove("fa-eye");
//       icon.classList.add("fa-eye-slash");
//     } else {
//       input.type = "password";
//       icon.classList.remove("fa-eye-slash");
//       icon.classList.add("fa-eye");
//     }
//   }

//   password.addEventListener("input", validatePasswords);
//   confirmPassword.addEventListener("input", validatePasswords);

//   togglePassword.addEventListener("click", function () {
//     toggleVisibility(password, togglePassword);
//   });

//   toggleConfirmPassword.addEventListener("click", function () {
//     toggleVisibility(confirmPassword, toggleConfirmPassword);
//   });
// });

document.addEventListener("DOMContentLoaded", function () {
  const password = document.getElementById("password");
  const confirmPassword = document.getElementById("confirmPassword");
  const signInButton = document.getElementById("signInButton");
  const togglePassword = document.getElementById("togglePassword");
  const toggleConfirmPassword = document.getElementById(
    "toggleConfirmPassword"
  );
  const passwordError = document.getElementById("passwordError"); // Error message element

  function validatePasswords() {
    if (password.value === confirmPassword.value && password.value !== "") {
      signInButton.disabled = false;
      passwordError.classList.add("hidden");
    } else {
      signInButton.disabled = true;
      passwordError.classList.remove("hidden");
    }
  }

  function toggleVisibility(input, icon) {
    if (input.type === "password") {
      input.type = "text";
      icon.classList.remove("fa-eye");
      icon.classList.add("fa-eye-slash");
    } else {
      input.type = "password";
      icon.classList.remove("fa-eye-slash");
      icon.classList.add("fa-eye");
    }
  }

  password.addEventListener("input", validatePasswords);
  confirmPassword.addEventListener("input", validatePasswords);

  togglePassword.addEventListener("click", function () {
    toggleVisibility(password, togglePassword);
  });

  toggleConfirmPassword.addEventListener("click", function () {
    toggleVisibility(confirmPassword, toggleConfirmPassword);
  });
});
