package ec.veronica.job.factory.extractor;

import ec.veronica.job.interfaces.ExtractorService;
import ec.veronica.job.types.DocumentType;
import org.springframework.stereotype.Service;

import static ec.veronica.job.types.DocumentType.NOTA_DEBITO;

@Service
public class DebitMemoExtractorService implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/notaDebito/infoNotaDebito/identificacionComprador";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(NOTA_DEBITO);
    }

}
