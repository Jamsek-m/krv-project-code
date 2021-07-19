function getQueryParam(paramName) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(paramName);
}

function populateFormWithQueryParams(formElem, fields) {
    if (typeof formElem === "string") {
        formElem = document.getElementById(formElem);
    }
    fields.forEach(field => {
        const queryParam = getQueryParam(field);
        if (queryParam != null) {
            addInputFieldToForm(formElem, field, queryParam);
        }
    });
}

function addInputFieldToForm(formElem, key, value, hidden = true) {
    if (typeof formElem === "string") {
        formElem = document.getElementById(formElem);
    }

    const input = document.createElement("INPUT");
    input.type = "text";
    input.hidden = hidden;
    input.name = key;
    input.value = value;
    formElem.appendChild(input);
}
