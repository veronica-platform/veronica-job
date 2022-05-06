package ec.veronica.job.factory.extractor;

import ec.veronica.job.interfaces.ExtractorService;
import ec.veronica.job.types.DocumentType;
import org.springframework.stereotype.Service;

import static ec.veronica.job.types.DocumentType.COMPROBANTE_RETENCION;

@Service
public class WithHoldingExtractorService implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/comprobanteRetencion/infoCompRetencion/identificacionSujetoRetenido";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(COMPROBANTE_RETENCION);
    }

}
