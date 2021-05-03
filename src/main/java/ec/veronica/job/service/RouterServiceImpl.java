package ec.veronica.job.service;

import ec.veronica.job.dto.RouterDto;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouterServiceImpl implements RouterService {

    private final CamelContext camelContext;
    private final ApplicationContext springContext;

    @Override
    public RouterDto create(RouterDto routerDto) {
        try {

        }
    }

}
