package ec.veronica.job.service;

import ec.veronica.job.dto.SupplierDto;
import ec.veronica.job.repository.sql.SupplierRepository;
import ec.veronica.job.repository.sql.entity.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Supplier save(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void saveAll(List<SupplierDto> suppliers) {
        supplierRepository.deleteAll();
        supplierRepository.saveAll(suppliers.stream().map(this::toDomain).collect(Collectors.toList()));
    }

    public void delete(Long id) {
        supplierRepository.deleteById(id);
    }

    public void deleteAll() {
        supplierRepository.deleteAll();
    }

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id).orElse(new Supplier());
    }

    private Supplier toDomain(SupplierDto supplierDto) {
        return Supplier.builder()
                .supplierName(supplierDto.getRazonSocial())
                .supplierNumber(supplierDto.getRucPropietario())
                .build();
    }

}
