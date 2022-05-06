package ec.veronica.job.factory.extractor;

import ec.veronica.job.interfaces.ExtractorService;
import ec.veronica.job.types.DocumentType;
import org.springframework.stereotype.Service;

import static ec.veronica.job.types.DocumentType.GUITA_REMISION;

@Service
public class BolExtractorService implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/guiaRemision/infoGuiaRemision/rucTransportista";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(GUITA_REMISION);
    }

}
