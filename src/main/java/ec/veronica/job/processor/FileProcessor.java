package ec.veronica.job.processor;

import ec.veronica.job.factory.ExtractorFactory;
import ec.veronica.job.factory.ProcessorFactory;
import ec.veronica.job.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileProcessor implements Processor {

    private final AuditLogService auditLogService;
    private final ExtractorFactory extractorFactory;
    private final ProcessorFactory processorFactory;

    @Override
    public void process(Exchange exchange) {
    }

}
