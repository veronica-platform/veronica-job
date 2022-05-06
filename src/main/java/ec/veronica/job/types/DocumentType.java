package ec.veronica.job.types;

import ec.veronica.job.commons.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

import static ec.veronica.job.commons.Constants.MAX_ACCESS_KEY_LENGTH;

public enum DocumentType {

    FACTURA("01", "factura", "Factura", "FAC"),
    LIQUIDACION_COMPRAS("03", "liquidacionCompra", "Liquidación de compra", "LQ"),
    NOTA_CREDITO("04", "notaCredito", "Nota de crédito", "NC"),
    NOTA_DEBITO("05", "notaDebito", "Nota de débito", "ND"),
    GUITA_REMISION("06", "guiaRemision", "Guía de remisión", "GR"),
    COMPROBANTE_RETENCION("07", "comprobanteRetencion", "Comprobante de retención", "CR");

    private final String code;
    private final String name;
    private final String description;
    private final String shortName;

    DocumentType(String code, String name, String description, String shortName) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.shortName = shortName;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public static Optional<DocumentType> getFromCode(String documentType) {
        return get(documentType);
    }

    public static Optional<DocumentType> getFromAccessKey(String accessKey) {
        if (!StringUtils.isEmpty(accessKey) && accessKey.length() == MAX_ACCESS_KEY_LENGTH) {
            String documentType = accessKey.substring(8, 10);
            return get(documentType);
        }
        return Optional.empty();
    }

    private static Optional<DocumentType> get(String documentType) {
        return Stream.of(DocumentType.values())
                .filter(p -> p.getCode().compareTo(documentType) == 0)
                .findFirst();
    }

}
