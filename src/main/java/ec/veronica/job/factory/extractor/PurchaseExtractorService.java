package ec.veronica.job.factory.extractor;

import ec.veronica.job.interfaces.ExtractorService;
import ec.veronica.job.types.DocumentType;
import org.springframework.stereotype.Service;

import static ec.veronica.job.types.DocumentType.LIQUIDACION_COMPRAS;

@Service
public class PurchaseExtractorService implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/liquidacionCompra/infoLiquidacionCompra/identificacionProveedor";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(LIQUIDACION_COMPRAS);
    }

}
