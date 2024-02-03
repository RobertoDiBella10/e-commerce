package progetto.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.Ordine;
import progetto.ecommerce.entity.Reso;
import org.springframework.data.domain.Pageable;

@Repository
public interface ResoRepository extends JpaRepository<Reso, Long> {

    Page<Reso> findByCliente(Cliente cliente, Pageable pageable);

    boolean existsByOrdine(Ordine ordine);

}
