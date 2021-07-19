function onPageLoad() {
    populateUi();
    registerListeners();
}

function populateUi() {
    const formElement = document.getElementById("consent-form");
    populateFormWithQueryParams(formElement, ["request_id", "client_id", "redirect_uri"])

    const clientId = getQueryParam("client_id");
    Array.from(document.getElementsByClassName("r-client-title")).forEach(elem => {
        elem.innerText = clientId;
    });
}

function onAllowConsent() {
    const form = document.getElementById("consent-form");
    addInputFieldToForm(form, "consent", "ALLOWED");
    form.submit();
}

function onDenyConsent() {
    const form = document.getElementById("consent-form");
    addInputFieldToForm(form, "consent", "DENIED");
    form.submit();
}

function registerListeners() {
    document.getElementById("allow-btn").addEventListener("click", onAllowConsent);
    document.getElementById("deny-btn").addEventListener("click", onDenyConsent);
}