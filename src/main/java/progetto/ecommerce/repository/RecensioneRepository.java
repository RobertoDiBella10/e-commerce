package progetto.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.entity.Recensione;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    boolean existsByClienteAndProdottoRecensito(Cliente cliente, Prodotto prodotto);

    Page<Recensione> findByCliente(Cliente cf, Pageable pageable);
}
