package progetto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ID_PRODOTTO", "ID_ORDINE"}))
public class Associato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @ManyToOne
    @JoinColumn(name = "ID_PRODOTTO")
    private Prodotto prodotto;

    @ManyToOne
    @JoinColumn(name = "ID_ORDINE")
    private Ordine ordine;

    @Column(nullable = false)
    private int quantita;

    @Column
    private double subtotale;
}
