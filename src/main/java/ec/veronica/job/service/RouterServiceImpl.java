package ec.veronica.job.service;

import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import ec.veronica.job.domain.Router;
import ec.veronica.job.dto.RouterDto;
import ec.veronica.job.processor.FileProcessor;
import ec.veronica.job.repository.RouterRepository;
import ec.veronica.job.router.VeronicaRoute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouterServiceImpl implements RouterService {

    @Value("${veronica.folder.inbox}")
    private String inboxFolder;
    private final CamelContext camelContext;
    private final FileProcessor fileProcessor;
    private final RouterRepository routerRepository;

    @Override
    public RouterDto create(final RouterDto routerDto) {
        try {
            String routeId = UUID.randomUUID().toString();
            RouteBuilder routeBuilder = VeronicaRoute.builder()
                    .inboxFolder(inboxFolder)
                    .routeId(routeId)
                    .rootFolder(routerDto.getRootFolder())
                    .fileProcessor(fileProcessor)
                    .build();
            camelContext.addRoutes(routeBuilder);
            routerDto.setRouteId(routeId);
            routerRepository.save(toDomain(routerDto));
        } catch (Exception ex) {
            String message = format("No se pudo crear la ruta %s", routerDto);
            log.error(message, ex);
            throw new VeronicaException(message);
        }
        return routerDto;
    }

    @Override
    public void remove(String routeId) {
        try {
            camelContext.removeRouteDefinition(camelContext.getRouteDefinition(routeId));
        } catch (Exception ex) {
            String message = format("No se pudo eliminar la ruta %s", routeId);
            log.error(message, ex);
            throw new VeronicaException(message);
        }
    }

    @Override
    public List<RouterDto> findAll() {
        return routerRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private Router toDomain(RouterDto dto) {
        Router router = new Router();
        router.setEnabled(dto.isEnabled());
        router.setId(dto.getRouteId());
        router.setRootFolder(dto.getRootFolder());
        router.setSupplierNumber(dto.getSupplierNumber());
        return router;
    }

    private RouterDto toDto(Router domain) {
        RouterDto router = new RouterDto();
        router.setEnabled(domain.isEnabled());
        router.setRouteId(domain.getId());
        router.setRootFolder(domain.getRootFolder());
        router.setSupplierNumber(domain.getSupplierNumber());
        return router;
    }

}
