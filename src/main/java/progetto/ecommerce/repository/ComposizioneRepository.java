package progetto.ecommerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.Carrello;
import progetto.ecommerce.entity.Composizione;
import progetto.ecommerce.entity.Prodotto;

@Repository
public interface ComposizioneRepository extends JpaRepository<Composizione, Long> {

    boolean existsByProdottoAndCarrello(Prodotto prodotto, Carrello carrello);
}
