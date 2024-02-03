package progetto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

@Data
@Entity
public class Ordine{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_ordine", unique = true)
    private String numeroOrdine;

    @Column
    private String stato;

    @Column(name = "data_acquisto", nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date data;

    @Column(name = "data_consegna", nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dataConsegna;

    @ManyToOne
    @JoinColumn(name = "CLIENTE", nullable = false)
    private Cliente clienteOrdine;

    @Column(name = "sconto_applicato(%)", nullable = false)
    private float scontoApplicato;

    @Column
    private double totale;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "ordine", fetch = FetchType.LAZY)
    private Collection<Associato> composizioneOrdine = new LinkedList<>();

    @OneToOne(mappedBy = "ordine")
    @JoinColumn
    @JsonIgnore
    private Reso reso;

    @Override
    public String toString() {
        return "Ordine{" +
                "id=" + id +
                ", numeroOrdine='" + numeroOrdine + '\'' +
                ", stato='" + stato + '\'' +
                ", data=" + data +
                ", dataConsegna=" + dataConsegna +
                ", scontoApplicato=" + scontoApplicato +
                ", totale=" + totale +
                '}';
    }
}