package ec.veronica.job.service;

import ec.veronica.job.domain.Listener;
import ec.veronica.job.dto.ChangeListenerStatusDto;
import ec.veronica.job.dto.CreateListenerDto;
import ec.veronica.job.dto.ListenerDto;
import ec.veronica.job.exceptions.VeronicaException;
import ec.veronica.job.processor.FileProcessor;
import ec.veronica.job.repository.sql.ListenerRepository;
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

import static ec.veronica.job.commons.FolderUtils.isDirectory;
import static java.text.MessageFormat.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListenerService {

    private final CamelContext camelContext;
    private final FileProcessor fileProcessor;
    private final ListenerRepository listenerRepository;

    public ListenerDto create(final CreateListenerDto createListenerDto) {
        log.debug("Registering route {}", createListenerDto);
        validateFolder(createListenerDto.getRootFolder());
        Optional<Listener> optionalRouter = listenerRepository.findFirstBySupplierNumberOrRootFolder(createListenerDto.getSupplierNumber(), createListenerDto.getRootFolder());
        if (optionalRouter.isPresent()) {
            throw new VeronicaException("Ya existe una ruta asociada asociada a la empresa y/o carpeta");
        }
        Listener savedListener = listenerRepository.save(toDomain(createListenerDto));
        RouteBuilder routeBuilder = toRoute(savedListener);
        try {
            camelContext.addRoutes(routeBuilder);
        } catch (Exception ex) {
            log.error("An error occurred trying to create the route", ex);
            throw new VeronicaException(format("Ocurrió un error al crear el proceso: {0}", ex.getMessage()));
        }
        return toDto(savedListener);
    }

    public void delete(String listenerId) {
        log.debug("Deleting route {}", listenerId);
        listenerRepository.deleteById(listenerId);
        removeRouteFromContext(listenerId);
    }

    public ListenerDto changeStatus(String listenerId, ChangeListenerStatusDto dto) {
        log.debug("Starting route {}", listenerId);
        Listener listener = listenerRepository.findById(listenerId).orElseThrow(() -> new VeronicaException(format("La ruta %s", listenerId)));
        return dto.isEnabled() ? start(listener) : stop(listener);
    }

    private void validateFolder(String folderPath) {
        if (!isDirectory(folderPath)) {
            throw new VeronicaException(format("La ruta {0} no existe o es incorrecta", folderPath));
        }
    }

    private ListenerDto start(Listener listener) {
        log.debug("Starting route {}", listener.getRootFolder());
        try {
            camelContext.addRoutes(toRoute(listener));
            listener.setEnabled(true);
            return toDto(listenerRepository.save(listener));
        } catch (Exception ex) {
            log.error("Unable to start the route due to", ex);
            throw new VeronicaException(format("Ocurrió un error al iniciar el proceso: {0}", ex.getMessage()));
        }
    }

    private ListenerDto stop(Listener listener) {
        log.debug("Stopping route {}", listener.getRootFolder());
        try {
            removeRouteFromContext(listener.getId());
            listener.setEnabled(false);
            return toDto(listenerRepository.save(listener));
        } catch (Exception ex) {
            log.error("Unable to stop the route", ex);
            throw new VeronicaException(format("Ocurrió un error al detener el proceso: {0}", ex.getMessage()));
        }
    }

    public List<ListenerDto> findAll() {
        return listenerRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public void disableAll() {
        listenerRepository.disableAll();
    }

    private RouteBuilder toRoute(Listener listener) {
        return VeronicaRoute.builder()
                .routeId(listener.getId())
                .rootFolder(listener.getRootFolder())
                .fileProcessor(fileProcessor)
                .build();
    }

    private Listener toDomain(CreateListenerDto dto) {
        Listener listener = new Listener();
        listener.setId(UUID.randomUUID().toString());
        listener.setEnabled(dto.isEnabled());
        listener.setRootFolder(dto.getRootFolder());
        listener.setSupplierNumber(dto.getSupplierNumber());
        return listener;
    }

    private ListenerDto toDto(Listener domain) {
        ListenerDto router = new ListenerDto();
        router.setEnabled(domain.isEnabled());
        router.setId(domain.getId());
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
            log.error("Unable to delete the route", ex);
            throw new VeronicaException(format("Ocurrió un error al eliminar el proceso: {0}", ex.getMessage()));
        }
    }

}
