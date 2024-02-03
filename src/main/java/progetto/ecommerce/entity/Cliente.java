package progetto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Collection;
import java.util.LinkedList;

@Data
@Entity
public class Cliente {

    @Id
    @Column(length = 25)
    private String CF;

    @Column(nullable = false, length = 30)
    private String nome;

    @Column(nullable = false, length = 30)
    private String cognome;

    @Column(nullable = false, length = 50)
    private String citta;

    @Column(nullable = false, length = 50)
    private String provincia;

    @Column(nullable = false, length = 50)
    private String via;

    @Column(nullable = false, length = 50)
    private int cap;

    @Column(unique = true, nullable = false, length = 20)
    private long telefono;

    @Column(unique = true, nullable = false, length = 90)
    private String email;

    @Column
    private boolean visibile;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Role role;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL} , mappedBy = "clienteOrdine", fetch = FetchType.LAZY)
    private Collection<Ordine> ordiniEffettuati = new LinkedList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL} , mappedBy = "cliente", fetch = FetchType.LAZY)
    private Collection<Reso> resiEffettuati = new LinkedList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL} , mappedBy = "cliente", fetch = FetchType.LAZY)
    private Collection<Recensione> recensioni = new LinkedList<>();

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(nullable = false, unique = true)
    private Carrello carrello;

    @Override
    public String toString() {
        return "Cliente{" +
                "CF='" + CF + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", citta='" + citta + '\'' +
                ", provincia='" + provincia + '\'' +
                ", via='" + via + '\'' +
                ", cap=" + cap +
                ", telefono=" + telefono +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

