package ec.veronica.job.commons;

import java.util.Optional;
import java.util.stream.Stream;

public enum Status {

    STATUS_APPLIED("AUTORIZADO"),
    STATUS_NOT_APPLIED("NO AUTORIZADO"),
    STATUS_REJECTED("DEVUELTA"),
    STATUS_INTERNAL_ERROR("CON ERRORES"),
    STATUS_PENDING("PENDIENTE");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<Status> fromValue(String value) {
        return Stream.of(Status.values())
                .filter(p -> p.getValue().compareTo(value) == 0)
                .findFirst();
    }

}
