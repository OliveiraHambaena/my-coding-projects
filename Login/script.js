document.addEventListener("DOMContentLoaded", () => {
    const signUpButton = document.getElementById("signUpButton");
    const signInButton = document.getElementById("signInButton");
    const signUpContainer = document.getElementById("signup");
    const signInContainer = document.getElementById("signIn");

    // Show the Sign Up form and hide the Sign In form
    signUpButton.addEventListener("click", () => {
        signUpContainer.style.display = "block";
        signInContainer.style.display = "none";
    });

    // Show the Sign In form and hide the Sign Up form
    signInButton.addEventListener("click", () => {
        signInContainer.style.display = "block";
        signUpContainer.style.display = "none";
    });
});
