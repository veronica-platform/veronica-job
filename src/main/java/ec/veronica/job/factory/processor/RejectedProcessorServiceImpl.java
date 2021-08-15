package ec.veronica.job.factory.processor;

import com.rolandopalermo.facturacion.ec.common.types.SriStatusType;
import ec.veronica.job.factory.ProcessorService;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_REJECTED;

@Service
public class RejectedProcessorServiceImpl implements ProcessorService {

    @Override
    public void process(Exchange exchange, byte[] pdf, byte[] xml) {
        exchange.getIn().setHeader("status", STATUS_REJECTED.getValue());
    }

    @Override
    public boolean supports(SriStatusType status) {
        return status.equals(STATUS_REJECTED);
    }

}
