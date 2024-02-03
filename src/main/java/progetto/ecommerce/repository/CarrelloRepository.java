package progetto.ecommerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.Carrello;
import progetto.ecommerce.entity.Cliente;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello, Long> {

    @Query("select c.carrello " +
            "from Cliente c " +
            "where c.CF = :cliente")
    Carrello findCarrello(String cliente);
}
