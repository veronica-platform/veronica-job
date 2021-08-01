package ec.veronica.job.factory;

import com.rolandopalermo.facturacion.ec.common.types.SriStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessorFactory {

    private List<ProcessorService> processorServiceList;

    @Autowired
    public ProcessorFactory(List<ProcessorService> processorServiceList) {
        this.processorServiceList = processorServiceList;
    }

    public ProcessorService get(SriStatusType status) {
        return processorServiceList
                .stream()
                .filter(service -> service.supports(status))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
