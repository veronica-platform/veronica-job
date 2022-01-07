package ec.veronica.job.factory.extractor;

import ec.veronica.common.types.DocumentType;
import ec.veronica.job.factory.ExtractorService;
import org.springframework.stereotype.Service;

import static ec.veronica.common.types.DocumentType.GUITA_REMISION;

@Service
public class BolExtractorServiceImpl implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/guiaRemision/infoGuiaRemision/rucTransportista";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(GUITA_REMISION);
    }

}
