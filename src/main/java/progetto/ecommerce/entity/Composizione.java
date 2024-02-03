package progetto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ID_PRODOTTO", "CARRELLO"}))
public class Composizione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @ManyToOne
    @JoinColumn(name = "ID_PRODOTTO")
    private Prodotto prodotto;

    @ManyToOne
    @JoinColumn(name = "CARRELLO")
    private Carrello carrello;

    @Column(nullable = false)
    private int quantita;

    @Column
    private double subtotale;
}