package progetto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"CLIENTE", "PRODOTTO_RECENSITO"}))
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @Column
    private String testo;

    @Column(nullable = false)
    private int valutazione;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date data;

    @ManyToOne
    @JoinColumn(name = "CLIENTE", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "PRODOTTO_RECENSITO", nullable = false)
    private Prodotto prodottoRecensito;

}
