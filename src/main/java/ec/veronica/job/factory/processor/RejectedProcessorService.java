package ec.veronica.job.factory.processor;

import ec.veronica.job.interfaces.ProcessorService;
import ec.veronica.job.types.SriStatusType;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import static ec.veronica.job.types.SriStatusType.STATUS_REJECTED;

@Service
public class RejectedProcessorService implements ProcessorService {

    @Override
    public void process(Exchange exchange, byte[] pdf, byte[] xml) {
        exchange.getIn().setHeader("status", STATUS_REJECTED.getValue());
    }

    @Override
    public boolean supports(SriStatusType status) {
        return status.equals(STATUS_REJECTED);
    }

}
