package progetto.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.entity.Recensione;
import java.util.List;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, String> {

    @Query("select p " +
            "from Prodotto p " +
            "where p.id like ?1% and p.visibilita = true")
    Page<Prodotto> findByIdStartingWith(String ID, Pageable pageable);

    @Query("select p " +
            "from Prodotto p " +
            "where p.id like ?1% and p.visibilita = false")
    Page<Prodotto> searchProdottoEliminato(String ID, Pageable pageable);

    @Query("select p " +
            "from Prodotto p " +
            "where (p.quantita >= :quantita or :quantita is null) and " +
            "(p.stato = :stato or :stato is null or :stato = '') and " +
            "(p.categoria.nome = :categoria or :categoria is null or :categoria = '') and p.visibilita = true")
    Page<Prodotto> filtraProdotto(Integer quantita,String stato,String categoria, Pageable pageable);

    @Query("select p " +
            "from Prodotto p " +
            "where p.visibilita = false")
    Page<Prodotto> findProdottiEliminati(Pageable pageable);

    @Query("select r " +
            "from Recensione r,Prodotto p " +
            "where p = :prodotto and r.prodottoRecensito = p and p.visibilita = true")
    Page<Recensione> findRecensioniProdotto(Prodotto prodotto, Pageable pageable);

    @Query("select p " +
            "from Prodotto p " +
            "where p.visibilita = true ")
    Page<Prodotto> findAll(Pageable pageable);

    @Query("select p " +
            "from Prodotto p " +
            "where p.categoria.nome = :categoria and p.visibilita = true ")
    List<Prodotto> findByCategoria(String categoria);

    @Query("select p " +
            "from Prodotto p " +
            "where p.nome like ?1% and p.visibilita = true")
    Page<Prodotto> findByNomeStartingWith(String nome, Pageable pageable);

    @Query("select AVG(r.valutazione) " +
            "from Recensione r " +
            "where r.prodottoRecensito.id = :id")
    double calcolaMediaRecensioneProdotto(String id);
}
