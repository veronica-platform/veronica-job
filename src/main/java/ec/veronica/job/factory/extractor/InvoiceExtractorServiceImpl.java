package ec.veronica.job.factory.extractor;

import com.rolandopalermo.facturacion.ec.common.types.DocumentType;
import ec.veronica.job.factory.ExtractorService;
import org.springframework.stereotype.Service;

import static com.rolandopalermo.facturacion.ec.common.types.DocumentType.FACTURA;

@Service
public class InvoiceExtractorServiceImpl implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/factura/infoFactura/identificacionComprador";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(FACTURA);
    }

}
