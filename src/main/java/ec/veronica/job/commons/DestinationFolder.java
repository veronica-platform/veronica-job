package ec.veronica.job.commons;

public enum DestinationFolder {

    FOLDER_INBOX("Inbox"),
    FOLDER_PENDING("PendienteSubir"),
    FOLDER_PROCESSING_ERROR("ErrorProcesando"),
    FOLDER_REJECTED("Devueltos"),
    FOLDER_UNAUTHORIZED("NoAutorizados"),
    FOLDER_AUTHORIZED("Autorizados");

    private String value;

    DestinationFolder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
