const basePath = '/api/api-keys'
const timeout = 600000

$(document).ready(function () {
    $("#create-form").submit(function (event) {
        event.preventDefault();
        _create();
    });

});

function deleteApiKey(apiKeyId) {
    swal({
            title: "¿Está seguro?",
            text: "Una vez eliminado, una API Key no puede ser recuperada",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "btn-danger",
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar",
            closeOnConfirm: false
        },
        function () {
            _delete(apiKeyId)
        }
    );
}

function _delete(apiKeyId) {
    let token = $("meta[name='_csrf']").attr("content");
    $.ajax({
        type: "DELETE",
        contentType: "application/json",
        url: `${basePath}/${apiKeyId}`,
        headers: {"X-CSRF-TOKEN": token},
        cache: false,
        timeout: timeout,
        success: function (data) {
            redirectToHome();
        },
        error: function (e) {
            handleError(e)
        }
    });
}

function _create() {
    let token = $("meta[name='_csrf']").attr("content");
    let request = {}
    request["keyValue"] = $("#value").val();
    request["supplierNumber"] = $("#supplierNumber").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: basePath,
        headers: {"X-CSRF-TOKEN": token},
        data: JSON.stringify(request),
        dataType: 'json',
        cache: false,
        timeout: timeout,
        success: function (data) {
            redirectToHome();
        },
        error: function (e) {
            handleError(e)
        }
    });
}

function _deleteAll() {
    let token = $("meta[name='_csrf']").attr("content");
    $.ajax({
        type: "DELETE",
        contentType: "application/json",
        url: `${basePath}/all`,
        headers: {"X-CSRF-TOKEN": token},
        cache: false,
        timeout: timeout,
        success: function (data) {
            redirectToHome();
        },
        error: function (e) {
            handleError(e)
        }
    });
}

function handleError(error) {
    console.log(error)
    swal({
            title: "Error",
            text: error?.responseJSON?.message ?? 'Ocurrió un error interno en la aplicación',
            type: "error"
        }
    )
}

function redirectToHome() {
    window.location.href = "/api-keys/";
}