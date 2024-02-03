package progetto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

@Data
@Entity
public class Prodotto {

    @Id
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private int quantita;

    @Column(nullable = false)
    private String descrizione;

    @Column
    private String stato;

    @Column
    private boolean visibilita;

    @Column(nullable = false)
    private double prezzo;

    @Column(name = "valutazione_media")
    private double avgValutazione;

    @ManyToOne
    @JoinColumn(name = "CATEGORIA")
    private Categoria categoria;

    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "prodotto", fetch = FetchType.LAZY)
    private Collection<Composizione> composizioneCarrello = new LinkedList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "prodotto", fetch = FetchType.LAZY)
    private Collection<Associato> composizioneOrdine = new LinkedList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL} , mappedBy = "prodottoRecensito", fetch = FetchType.LAZY)
    private Collection<Recensione> recensioni = new LinkedList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "product_images", joinColumns = {@JoinColumn(name = "product_id")}, inverseJoinColumns = {@JoinColumn(name = "image_id")})
    private Set<Image> productImages;

    @Override
    public String toString() {
        return "Prodotto{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", marca='" + marca + '\'' +
                ", quantita=" + quantita +
                ", descrizione='" + descrizione + '\'' +
                ", stato='" + stato + '\'' +
                ", visibilita=" + visibilita +
                ", prezzo=" + prezzo +
                ", avgValutazione=" + avgValutazione +
                '}';
    }
}
