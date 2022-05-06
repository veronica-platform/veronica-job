package ec.veronica.job.types;

public enum DestinationFolderType {

    FOLDER_INBOX("Inbox"),
    FOLDER_PENDING("PendienteSubir"),
    FOLDER_PROCESSING_ERROR("ErrorProcesando"),
    FOLDER_REJECTED("Devueltos"),
    FOLDER_UNAUTHORIZED("NoAutorizados"),
    FOLDER_AUTHORIZED("Autorizados");

    private String value;

    DestinationFolderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
