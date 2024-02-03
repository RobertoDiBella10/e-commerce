package progetto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class Reso{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String motivazione;

    @OneToOne
    @JoinColumn(name = "numero_ordine",nullable = false, unique = true)
    private Ordine ordine;

    //tolto JsonIGNORE
    @ManyToOne
    @JoinColumn(name = "CLIENTE", nullable = false)
    private Cliente cliente;

}
