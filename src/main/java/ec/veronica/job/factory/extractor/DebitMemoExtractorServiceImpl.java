package ec.veronica.job.factory.extractor;

import ec.veronica.common.types.DocumentType;
import ec.veronica.job.factory.ExtractorService;
import org.springframework.stereotype.Service;

import static ec.veronica.common.types.DocumentType.NOTA_DEBITO;

@Service
public class DebitMemoExtractorServiceImpl implements ExtractorService {

    @Override
    public String getCustomerNumberXPath() {
        return "/notaDebito/infoNotaDebito/identificacionComprador";
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType.equals(NOTA_DEBITO);
    }

}
