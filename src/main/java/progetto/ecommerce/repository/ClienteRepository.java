package progetto.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.Composizione;
import progetto.ecommerce.entity.Prodotto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    @Query("select c " +
            "from Cliente c " +
            "where c.CF like ?1% and c.visibile = true")
    Page<Cliente> findByCFStartingWith(String cf, Pageable pageable);

    @Query("select c " +
            "from Cliente c " +
            "where c.CF like ?1% and c.visibile = false")
    Page<Cliente> searchClienteEliminato(String cf, Pageable pageable);

    @Query("select c " +
            "from Cliente c " +
            "where c.visibile = true ")
    Page<Cliente> findAll(Pageable pageable);

    @Query("select c " +
            "from Cliente c " +
            "where c.visibile = false ")
    Page<Cliente> findClientiEliminati(Pageable pageable);

    @Query("select c " +
            "from Cliente c " +
            "where (c.nome = :nome or :nome is null or :nome = '') and " +
            "(c.cognome = :cognome or :cognome is null or :cognome = '') and " +
            "(c.citta = :citta or :citta is null or :citta = '') and " +
            "(c.via = :via or :via is null or :via = '') and " +
            "(c.cap = :cap or :cap is null) and c.visibile = true")
    Page<Cliente> filtraClienti(String nome, String cognome, String citta, String via, Integer cap, Pageable pageable);

    @Query("select com " +
            "from Carrello c, Composizione com " +
            "where c.cliente = :cliente and c = com.carrello and c.cliente.visibile = true " +
            "ORDER BY com.ID")
    List<Composizione> findProdottiCarrello(Cliente cliente);

    boolean existsByEmail(String email);

    boolean existsByTelefono(long numero);

    boolean existsByUsername(String username);

    @Query("select c.visibile " +
            "from Cliente c " +
            "where c.username = :username")
    boolean findByUsernameIsVisibile(String username);
}
