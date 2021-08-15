package ec.veronica.job.factory;

import com.rolandopalermo.facturacion.ec.common.types.SriStatusType;
import org.apache.camel.Exchange;

public interface ProcessorService {

    void process(Exchange exchange, byte[] pdf, byte[] xml);

    default boolean supports(SriStatusType status) {
        return false;
    }

}
