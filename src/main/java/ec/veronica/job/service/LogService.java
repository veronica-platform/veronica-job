package ec.veronica.job.service;

import ec.veronica.job.domain.Log;

import java.util.List;

public interface LogService {

    void save(Log log);

    void delete(Long id);

    void deleteAll();

    List<Log> findAll();

    Log findById(Long id);

}
