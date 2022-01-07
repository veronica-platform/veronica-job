package ec.veronica.job.factory.extractor;

import ec.veronica.common.types.DocumentType;
import ec.veronica.job.factory.ExtractorService;
import org.springframework.stereotype.Service;

import static ec.veronica.common.types.DocumentType.LIQUIDACION_COMPRAS;

@Service
public class PurchaseExtractorServiceImpl implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/liquidacionCompra/infoLiquidacionCompra/identificacionProveedor";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(LIQUIDACION_COMPRAS);
    }

}
