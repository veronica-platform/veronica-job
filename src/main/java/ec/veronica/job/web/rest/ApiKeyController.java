package ec.veronica.job.web.rest;

import ec.veronica.job.dto.ApiKeyDto;
import ec.veronica.job.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "api/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyDto> create(@Valid @RequestBody ApiKeyDto dto) {
        return new ResponseEntity<>(apiKeyService.save(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyDto>> findAll() {
        return new ResponseEntity<>(apiKeyService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping(value = "{api-key-id}")
    public ResponseEntity<?> delete(@PathVariable("api-key-id") Long apiKeyId) {
        apiKeyService.delete(apiKeyId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @DeleteMapping(value = "all")
    public ResponseEntity<?> deleteAll() {
        apiKeyService.deleteAll();
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
