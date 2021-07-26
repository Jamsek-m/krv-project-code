function onPageLoad() {
    registerListeners();
}

function onConsentInput(result) {
    return function () {
        const form = document.getElementById("consent-form");
        addInputFieldToForm(form, "consent", result);
        form.submit();
    };
}

function registerListeners() {
    document.getElementById("allow-btn").addEventListener("click", onConsentInput("ALLOWED"));
    document.getElementById("deny-btn").addEventListener("click", onConsentInput("DENIED"));
}

window.onload = onPageLoad;
