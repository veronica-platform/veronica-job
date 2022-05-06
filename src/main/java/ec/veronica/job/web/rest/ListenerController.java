package ec.veronica.job.web.rest;

import ec.veronica.job.dto.ChangeListenerStatusDto;
import ec.veronica.job.dto.CreateListenerDto;
import ec.veronica.job.dto.ListenerDto;
import ec.veronica.job.service.ListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/listeners")
@RequiredArgsConstructor
public class ListenerController {

    private final ListenerService listenerService;

    @PostMapping
    public ResponseEntity<ListenerDto> create(@Valid @RequestBody CreateListenerDto router) {
        return new ResponseEntity<>(listenerService.create(router), HttpStatus.CREATED);
    }

    @DeleteMapping("{listener-id}")
    public ResponseEntity<?> delete(@PathVariable("listener-id") String listenerId) {
        listenerService.delete(listenerId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PatchMapping("{listener-id}/change-status")
    public ResponseEntity<ListenerDto> changeStatus(
            @PathVariable("listener-id") String listenerId,
            @Valid @RequestBody ChangeListenerStatusDto dto) {
        return new ResponseEntity<>(listenerService.changeStatus(listenerId, dto), HttpStatus.OK);
    }

}
