function addFieldToForm(formElem, key, value, hidden = true) {
    const input = document.createElement("INPUT");
    input.type = "text";
    input.hidden = hidden;
    input.name = key;
    input.value = value;
    formElem.appendChild(input);
}

function loadFieldsFromUrl() {
    const fields = ["request_id", "client_id", "redirect_uri", "scope", "state", "response_type"];
    const form = document.getElementById("login-form");
    fields.forEach(field => {
        const queryParam = getQueryParam(field);
        if (queryParam != null) {
            addFieldToForm(form, field, queryParam);
        }
    });
}