package progetto.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import progetto.ecommerce.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, String> {

    Page<Categoria> findByNomeStartingWith(String nome, Pageable pageable);
}
