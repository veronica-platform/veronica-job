package ec.veronica.job.factory.extractor;

import com.rolandopalermo.facturacion.ec.common.types.DocumentType;
import ec.veronica.job.factory.ExtractorService;
import org.springframework.stereotype.Service;

import static com.rolandopalermo.facturacion.ec.common.types.DocumentType.COMPROBANTE_RETENCION;

@Service
public class WithHoldingExtractorServiceImpl implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/comprobanteRetencion/infoCompRetencion/identificacionSujetoRetenido";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(COMPROBANTE_RETENCION);
    }

}
