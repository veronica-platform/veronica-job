package ec.veronica.job.web.rest;

import ec.veronica.job.dto.RouterDto;
import ec.veronica.job.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/")
@RequiredArgsConstructor
public class RouterController {

    private final RouterService routerService;

    @PostMapping(value = "/routes")
    public ResponseEntity<RouterDto> addRoute(@Valid @RequestBody RouterDto routerDto) {
        return new ResponseEntity<>(routerService.create(routerDto), HttpStatus.CREATED);
    }

}
