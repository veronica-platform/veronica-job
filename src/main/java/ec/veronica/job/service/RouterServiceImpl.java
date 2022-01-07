package ec.veronica.job.service;

import ec.veronica.common.StringUtils;
import ec.veronica.common.exception.ResourceNotFoundException;
import ec.veronica.job.commons.SessionUtils;
import ec.veronica.job.domain.Router;
import ec.veronica.job.dto.RouterDto;
import ec.veronica.job.processor.FileProcessor;
import ec.veronica.job.repository.sql.RouterRepository;
import ec.veronica.job.router.VeronicaRoute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouterServiceImpl implements RouterService {

    private final CamelContext camelContext;
    private final FileProcessor fileProcessor;
    private final RouterRepository routerRepository;

    @Override
    public void create(final RouterDto routerDto) {
        log.debug("Registering route [{}]", routerDto);
        Optional<Router> optionalRouter = routerRepository.findFirstBySupplierNumberOrRootFolder(routerDto.getSupplierNumber(), routerDto.getRootFolder());
        if (optionalRouter.isPresent()) {
            log.error("The supplier [{}] already has a route at [{}]", routerDto.getSupplierNumber(), optionalRouter.get().getRootFolder());
            return;
        }
        RouteBuilder routeBuilder = toRoute(routerDto);
        try {
            camelContext.addRoutes(routeBuilder);
            routerRepository.save(toDomain(routerDto));
        } catch (Exception ex) {
            log.error("An error occurred trying to create the route due to: [{}]", ex.getMessage(), ex);
        }
    }

    @Override
    public void start(String routeId) {
        log.debug("Starting route [{}]", routeId);
        try {
            RouterDto routerDto = toDto(routerRepository.findById(routeId).orElseThrow(() -> new ResourceNotFoundException(format("La ruta %s", routeId))));
            camelContext.addRoutes(toRoute(routerDto));
            routerRepository.updateStatus(routeId, true);
        } catch (Exception ex) {
            log.error("Unable to start the route due to: [{}]", ex.getMessage(), ex);
        }
    }

    @Override
    public void stop(String routeId) {
        log.debug("Stopping route [{}]", routeId);
        try {
            removeRouteFromContext(routeId);
            routerRepository.updateStatus(routeId, false);
        } catch (Exception ex) {
            log.error("Unable to stop the route due to: [{}]", ex.getMessage(), ex);
        }
    }

    @Override
    public void delete(String routeId) {
        log.debug("Deleting route [{}]", routeId);
        removeRouteFromContext(routeId);
        routerRepository.deleteById(routeId);
    }

    @Override
    public void disableAll() {
        routerRepository.disableAll();
    }

    @Override
    public List<RouterDto> findAll() {
        return routerRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private RouteBuilder toRoute(RouterDto routerDto) {
        if (StringUtils.isEmpty(routerDto.getRouteId())) {
            routerDto.setRouteId(UUID.randomUUID().toString());
        }
        return VeronicaRoute.builder()
                .routeId(routerDto.getRouteId())
                .rootFolder(routerDto.getRootFolder())
                .fileProcessor(fileProcessor)
                .build();
    }

    private Router toDomain(RouterDto dto) {
        Router router = new Router();
        router.setEnabled(dto.isEnabled());
        router.setId(dto.getRouteId());
        router.setRootFolder(dto.getRootFolder());
        router.setSupplierNumber(dto.getSupplierNumber());
        router.setUsername(SessionUtils.getAuthentication().getName());
        router.setPassword(SessionUtils.getAuthentication().getCredentials().toString());
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

    private void removeRouteFromContext(String routeId) {
        try {
            RouteDefinition routeDefinition = camelContext.getRouteDefinition(routeId);
            if (routeDefinition != null) {
                camelContext.removeRouteDefinition(routeDefinition);
            }
        } catch (Exception ex) {
            log.error("Unable to delete the route due to: [{}]", ex.getMessage(), ex);
        }
    }

}
