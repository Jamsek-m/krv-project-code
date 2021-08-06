/**
 * @param {string | undefined} error
 */
function createValidationError(error) {
    const validationErrorContainer = document.getElementById("validation-error");
    if (error) {
        const para = document.createElement("P");
        para.classList.add("error-container");
        para.innerText = error;
        validationErrorContainer.innerHTML = "";
        validationErrorContainer.appendChild(para);
    } else {
        validationErrorContainer.innerHTML = "";
    }
}
/**
 * @param {HTMLInputElement} $comparison
 */
function onPasswordInput($comparison) {
    /**
     * @param {InputEvent} $event
     */
    function handler($event) {
        const pass = $event.target.value;
        const confirmation = $comparison.value;

        if (pass !== "" || confirmation !== "") {
            if (pass !== confirmation) {
                createValidationError("Passwords do not match!");
                return;
            }
        }
        createValidationError(undefined);
    }
    return handler;
}
function onPageLoad() {
    const passInput = document.getElementById("pass-input");
    const confirmPassInput = document.getElementById("pass-input-2");
    
    passInput.addEventListener("input", onPasswordInput(confirmPassInput));
    confirmPassInput.addEventListener("input", onPasswordInput(passInput));
}
window.onload = onPageLoad;