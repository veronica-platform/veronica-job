package ec.veronica.job.interfaces;

import ec.veronica.job.types.SriStatusType;
import org.apache.camel.Exchange;

public interface ProcessorService {

    void process(Exchange exchange, byte[] pdf, byte[] xml);

    default boolean supports(SriStatusType status) {
        return false;
    }

}
