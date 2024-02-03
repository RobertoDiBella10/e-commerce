package progetto.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.*;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long> {

    Page<Ordine> findByNumeroOrdineStartingWith(String numeroOrdine, Pageable pageable);

    @Query("select o " +
            "from Ordine o " +
            "where o.clienteOrdine.CF = :cf and o.numeroOrdine like :numeroOrdine%")
    Page<Ordine> findByNumeroOrdineStartingWith(String cf, String numeroOrdine, Pageable pageable);

    Page<Ordine> findByClienteOrdine(Cliente cliente, Pageable pageable);

    boolean existsByNumeroOrdine(String numeroOrdine);

    @Query("select o " +
            "from Ordine o " +
            "where o.numeroOrdine = :numeroOrdine")
    Ordine findByNumeroOrdine(String numeroOrdine);

    @Query("select o " +
            "from Ordine o " +
            "where (o.stato = :stato or :stato is null or :stato = '') and " +
            "((year(o.data) = : anno ) or :anno is null )")
    Page<Ordine> filtraOrdine(String stato, Integer anno, Pageable pageable);

    @Query("select o " +
            "from Ordine o " +
            "where (o.stato = :stato or :stato is null or :stato = '') and " +
            "((year(o.data) = : anno ) or :anno is null ) and o.clienteOrdine.CF = :cf")
    Page<Ordine> filtraOrdineCliente(String cf, String stato, Integer anno, Pageable pageable);

    @Query("select com " +
            "from Carrello c, Composizione com " +
            "where c = :carrello and com.carrello = c")
    List<Composizione> findProdottiCarrello(Carrello carrello);

    @Query("select CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "from Ordine o " +
            "where o.numeroOrdine = :numeroOrdine and o.clienteOrdine.CF = :cf")
    boolean existsByClienteOrdineAndNumeroOrdine(String cf, String numeroOrdine);

    @Query("select a " +
            "from Associato a " +
            "where a.ordine.numeroOrdine = :idOrder")
    List<Associato> findProdottiOrdine(String idOrder);

}
