package progetto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Collection;
import java.util.LinkedList;

@Data
@Entity
public class Carrello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @Column
    private double totale;


    @JsonIgnore
    @OneToOne(mappedBy = "carrello")
    @JoinColumn
    private Cliente cliente;

    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "carrello", fetch = FetchType.LAZY)
    private Collection<Composizione> composizioneCarrello = new LinkedList<>();

}
