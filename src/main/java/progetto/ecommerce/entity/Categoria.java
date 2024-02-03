package progetto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.LinkedList;

@Entity
@Data
public class Categoria {

    @Id
    @Column(nullable = false)
    private String nome;

    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "categoria", fetch = FetchType.LAZY)
    private Collection<Prodotto> prodotti = new LinkedList<>();

}
