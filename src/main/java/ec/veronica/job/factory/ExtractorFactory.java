package ec.veronica.job.factory;

import ec.veronica.common.types.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExtractorFactory {

    private List<ExtractorService> extractorServiceList;

    @Autowired
    public ExtractorFactory(List<ExtractorService> extractorServiceList) {
        this.extractorServiceList = extractorServiceList;
    }

    public ExtractorService get(DocumentType documentType) {
        return extractorServiceList
                .stream()
                .filter(service -> service.supports(documentType))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
