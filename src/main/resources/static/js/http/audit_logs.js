const basePath = '/api/audit-logs'
const timeout = 600000

function deleteEvent(eventId) {
    swal({
            title: "¿Está seguro?",
            text: "Una vez eliminado, un evento no puede ser recuperado",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "btn-danger",
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar",
            closeOnConfirm: false
        },
        function () {
            _delete(eventId)
        }
    );
}

function _delete(eventId) {
    let token = $("meta[name='_csrf']").attr("content");
    $.ajax({
        type: "DELETE",
        contentType: "application/json",
        url: `${basePath}/${eventId}`,
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
    window.location.href = "/eventos";
}