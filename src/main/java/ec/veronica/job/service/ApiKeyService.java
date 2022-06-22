package ec.veronica.job.service;

import ec.veronica.job.dto.ApiKeyDto;
import ec.veronica.job.repository.sql.ApiKeyRepository;
import ec.veronica.job.repository.sql.entity.ApiKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyDto save(ApiKeyDto dto) {
        return toDto(apiKeyRepository.save(toDomain(dto)));
    }

    public void delete(Long id) {
        apiKeyRepository.deleteById(id);
    }

    public List<ApiKeyDto> findAll() {
        return apiKeyRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private ApiKey toDomain(ApiKeyDto apiKeyDto) {
        return ApiKey.builder()
                .keyValue(apiKeyDto.getKeyValue())
                .insertionDate(LocalDateTime.now())
                .build();
    }

    private ApiKeyDto toDto(ApiKey apiKey) {
        ApiKeyDto apiKeyDto = new ApiKeyDto();
        apiKeyDto.setKeyValue(apiKey.getKeyValue());
        apiKeyDto.setId(apiKey.getId());
        apiKeyDto.setInsertionDate(apiKey.getInsertionDate());
        return apiKeyDto;
    }

    @Transactional
    public void deleteAll() {
        apiKeyRepository.deleteAll();
    }

}
