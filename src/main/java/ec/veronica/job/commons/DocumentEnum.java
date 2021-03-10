package ec.veronica.job.commons;

import java.util.Optional;
import java.util.stream.Stream;

public enum DocumentEnum {

    FACTURA("01", "FACTURA", "Factura"),
    LIQUIDACION_COMPRAS("03", "LIQUIDACION_COMPRA", "Liquidación de compra"),
    NOTA_CREDITO("04", "NOTA_CREDITO", "Nota de crédito"),
    NOTA_DEBITO("05", "NOTA_DEBITO", "Nota de débito"),
    GUITA_REMISION("06", "GUIA_REMISION", "Guía de remisión"),
    COMPROBANTE_RETENCION("07", "COMPROBANTE_RETENCION", "Comprobante de retención");

    private String code;
    private String nombre;
    private String description;

    private DocumentEnum(String code, String nombre, String description) {
        this.code = code;
        this.nombre = nombre;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public static Optional<DocumentEnum> get(String documentType) {
        return Stream.of(values()).filter((p) -> {
            return p.getCode().compareTo(documentType) == 0;
        }).findFirst();
    }

}
