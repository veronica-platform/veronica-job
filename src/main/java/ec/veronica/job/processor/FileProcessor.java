package ec.veronica.job.processor;

import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import com.rolandopalermo.facturacion.ec.common.types.DocumentType;
import ec.veronica.job.dto.ReceiptDetailsDto;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static ec.veronica.job.commons.XmlUtils.xpath;
import static java.lang.String.format;

@Component
public class FileProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String xml = new String(exchange.getIn().getBody(String.class).getBytes(StandardCharsets.UTF_8));
        System.out.println(xml);
    }

    private ReceiptDetailsDto readDetails(String xml) {
        String docType = xpath(xml, "//codDoc");
        Optional<DocumentType> optionalDocumentEnum = DocumentType.getFromCode(docType);
        optionalDocumentEnum.orElseThrow(() -> new VeronicaException(format("El tipo de documento en %s es inválido", xml)));
        DocumentType documentType = optionalDocumentEnum.get();
        return ReceiptDetailsDto.builder()
                .estab(xpath(xml, "//estab"))
                .ptoEmision(xpath(xml, "//ptoEmi"))
                .docType(docType)
                .docNumber(xpath(xml, "//secuencial"))
                .customerNumber(xpath(xml, getCustomerNumberXPath(documentType)))
                .documentType(documentType)
                .build();
    }

    private String getCustomerNumberXPath(DocumentType documentType) {
        String customerNumberXpath;
        switch (documentType) {
            case FACTURA:
                customerNumberXpath = "/factura/infoFactura/identificacionComprador";
                break;
            case LIQUIDACION_COMPRAS:
                customerNumberXpath = "/liquidacionCompra/infoLiquidacionCompra/identificacionProveedor";
                break;
            case NOTA_CREDITO:
                customerNumberXpath = "/notaCredito/infoNotaCredito/identificacionComprador";
                break;
            case NOTA_DEBITO:
                customerNumberXpath = "/notaDebito/infoNotaDebito/identificacionComprador";
                break;
            case GUITA_REMISION:
                customerNumberXpath = "/guiaRemision/infoGuiaRemision/rucTransportista";
                break;
            case COMPROBANTE_RETENCION:
                customerNumberXpath = "/comprobanteRetencion/infoCompRetencion/identificacionSujetoRetenido";
                break;
            default:
                throw new VeronicaException("Tipo documento inválido");
        }
        return customerNumberXpath;
    }

}
