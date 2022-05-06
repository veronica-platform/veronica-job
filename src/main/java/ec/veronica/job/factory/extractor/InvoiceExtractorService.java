package ec.veronica.job.factory.extractor;

import ec.veronica.job.interfaces.ExtractorService;
import ec.veronica.job.types.DocumentType;
import org.springframework.stereotype.Service;

import static ec.veronica.job.types.DocumentType.FACTURA;

@Service
public class InvoiceExtractorService implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/factura/infoFactura/identificacionComprador";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(FACTURA);
    }

}
