const basePath = '/api/listeners'
const timeout = 600000

$(document).ready(function () {
    $("#create-form").submit(function (event) {
        event.preventDefault();
        _create();
    });

});

function deleteListener(listenerId) {
    swal({
            title: "¿Está seguro?",
            text: "Una vez eliminado, un proceso no puede ser recuperado",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "btn-danger",
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar",
            closeOnConfirm: false
        },
        function () {
            _delete(listenerId)
        }
    );
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
    window.location.href = "/";
}

function _delete(listenerId) {
    let token = $("meta[name='_csrf']").attr("content");
    $.ajax({
        type: "DELETE",
        contentType: "application/json",
        url: `${basePath}/${listenerId}`,
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
    request["rootFolder"] = $("#rootFolder").val();
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
