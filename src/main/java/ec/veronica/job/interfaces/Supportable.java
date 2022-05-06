package ec.veronica.job.interfaces;

import ec.veronica.job.types.DocumentType;

public interface Supportable {

    default boolean supports(DocumentType documentType) {
        return false;
    }

}
