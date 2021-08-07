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

/**
 * @param {InputEvent} $event
 */
function onAvatarUrlInput($event) {
    const url = $event.target.value;
    const previewElem = document.getElementById("avatar-preview");
    if (previewElem) {
        appendImagePreview(previewElem, url);
    }
}
/**
 * @param {HTMLInputElement} elem
 * @param {string} url
 */
function appendImagePreview(elem, url) {
    const image = document.createElement("IMG");
    image.src = url;
    elem.innerHTML = "";
    elem.appendChild(image);
}

function onProfileLoad() {
    const passInput = document.getElementById("pass-input");
    const confirmPassInput = document.getElementById("pass-input-2");
    passInput.addEventListener("input", onPasswordInput(confirmPassInput));
    confirmPassInput.addEventListener("input", onPasswordInput(passInput));

    const avatarInput = document.getElementById("avatar-input");
    const previewElem = document.getElementById("avatar-preview");
    avatarInput.addEventListener("click", onAvatarUrlInput, false);
    if (avatarInput.value !== "") {
        appendImagePreview(previewElem, avatarInput.value);
    }
}

window.onload = onProfileLoad;