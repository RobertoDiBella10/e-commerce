package progetto.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.Associato;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.Ordine;
import progetto.ecommerce.entity.Prodotto;

import java.util.List;

@Repository
public interface AssociatoRepository extends JpaRepository<Associato, Long> {

    List<Associato> findByOrdine(Ordine ordine);

    @Query("select CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "from Associato a, Ordine o " +
            "where a.prodotto.id = :idProdotto and a.ordine = o and o.clienteOrdine.CF = :cfCliente")
    boolean isClientBuyProduct(String cfCliente, String idProdotto);
}
