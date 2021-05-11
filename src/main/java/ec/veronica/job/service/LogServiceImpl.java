package ec.veronica.job.service;

import ec.veronica.job.domain.Log;
import ec.veronica.job.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Override
    public void save(Log log) {
        logRepository.save(log);
    }

    @Override
    public void delete(Long id) {
        logRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        logRepository.deleteAll();
    }

    @Override
    public List<Log> findAll() {
        return logRepository.findAll();
    }

}
