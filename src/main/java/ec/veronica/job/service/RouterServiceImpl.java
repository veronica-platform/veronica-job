package ec.veronica.job.service;

import com.rolandopalermo.facturacion.ec.common.StringUtils;
import com.rolandopalermo.facturacion.ec.common.exception.ResourceNotFoundException;
import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import ec.veronica.job.commons.SessionUtils;
import ec.veronica.job.domain.Router;
import ec.veronica.job.dto.RouterDto;
import ec.veronica.job.processor.FileProcessor;
import ec.veronica.job.repository.RouterRepository;
import ec.veronica.job.router.VeronicaRoute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static ec.veronica.job.commons.Constants.ERROR_ATTRIBUTE;
import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouterServiceImpl implements RouterService {

    private final CamelContext camelContext;
    private final FileProcessor fileProcessor;
    private final RouterRepository routerRepository;

    @Override
    public RouterDto create(final RouterDto routerDto, RedirectAttributes redirectAttributes) {
        Optional<Router> optionalRouter = routerRepository.findFirstBySupplierNumberOrRootFolder(routerDto.getSupplierNumber(), routerDto.getRootFolder());
        if (optionalRouter.isPresent()) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, format("Ya existe una ruta para la empresa %s", routerDto.getSupplierNumber()));
            return null;
        }
        RouteBuilder routeBuilder = toRoute(routerDto);
        try {
            camelContext.addRoutes(routeBuilder);
            routerRepository.save(toDomain(routerDto));
        } catch (Exception ex) {
            log.error("An error occurred trying to create the route - [{}]", ex) ;
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, "Ocurrió un error interno al crear la ruta");
        }
        return routerDto;
    }

    @Override
    public void start(String routeId, RedirectAttributes redirectAttributes) {
        try {
            RouterDto routerDto = toDto(routerRepository.findById(routeId).orElseThrow(() -> new ResourceNotFoundException(format("La ruta %s", routeId))));
            camelContext.addRoutes(toRoute(routerDto));
            routerRepository.updateStatus(routeId, true);
        } catch (Exception ex) {
            String message = format("No se pudo iniciar la ruta %s", routeId);
            log.error(message, ex);
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, message);
        }
    }

    @Override
    public void stop(String routeId, RedirectAttributes redirectAttributes) {
        try {
            removeRouteFromContext(routeId);
            routerRepository.updateStatus(routeId, false);
        } catch (Exception ex) {
            String message = format("No se pudo detener la ruta %s", routeId);
            log.error(message, ex);
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, message);
        }
    }

    @Override
    public void delete(String routeId, RedirectAttributes redirectAttributes) {
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
        router.setReceiptsCount(dto.getReceiptsCount());
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
        router.setReceiptsCount(domain.getReceiptsCount());
        return router;
    }

    private void removeRouteFromContext(String routeId) {
        try {
            RouteDefinition routeDefinition = camelContext.getRouteDefinition(routeId);
            if (routeDefinition != null) {
                camelContext.removeRouteDefinition(routeDefinition);
            }
        } catch (Exception ex) {
            String message = format("No se pudo eliminar la ruta %s", routeId);
            log.error(message, ex);
            throw new VeronicaException(message);
        }
    }

}
