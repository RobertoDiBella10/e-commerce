package progetto.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import progetto.ecommerce.entity.Carrello;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.Composizione;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.repository.CarrelloRepository;
import progetto.ecommerce.repository.ClienteRepository;
import progetto.ecommerce.repository.ComposizioneRepository;
import progetto.ecommerce.repository.ProdottoRepository;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class CarrelloService {

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Autowired
    private ComposizioneRepository composizioneRepository;

    @Autowired
    private ClienteRepository clienteRepository;


    @Transactional(propagation = Propagation.REQUIRED)
    public Composizione addProdottoCarrello(String prodottoID, String cfCliente, int quantita) throws ProdottoNotExistsException, CarrelloNotExistException, IllegalQuantitaException, VincoloUniqueException {
        Carrello carrello = carrelloRepository.findCarrello(cfCliente);
        Prodotto prodotto = prodottoRepository.findById(prodottoID).get();
        if(composizioneRepository.existsByProdottoAndCarrello(prodotto, carrello))
            throw new VincoloUniqueException();
        if(!prodottoRepository.existsById(prodottoID))
            throw new ProdottoNotExistsException();
        if(!carrelloRepository.existsById(carrello.getID()))
            throw new CarrelloNotExistException();
        if(quantita <= 0 || quantita > prodotto.getQuantita())
            throw new IllegalQuantitaException();
        double subtotale = quantita * prodotto.getPrezzo();
        Composizione composizione = new Composizione();
        composizione.setCarrello(carrello);
        composizione.setProdotto(prodotto);
        composizione.setQuantita(quantita);
        composizione.setSubtotale(subtotale);
        carrello.setTotale(carrello.getTotale() + subtotale);
        return composizioneRepository.save(composizione);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Carrello removeProdottoCarrello(Long id) throws ComposizioneNotExistException {
        if(!composizioneRepository.existsById(id))
            throw new ComposizioneNotExistException();
        Composizione composizione = composizioneRepository.getReferenceById(id);
        Carrello carrello = carrelloRepository.getReferenceById(composizione.getCarrello().getID());
        carrello.setTotale(carrello.getTotale() - composizione.getSubtotale());
        composizioneRepository.deleteById(id);
        return carrello;
    }

    @Transactional(readOnly = true)
    public List<Composizione> visualizzaCarrello(String cf) throws ClienteNotExistException {
        if(!clienteRepository.existsById(cf))
            throw new ClienteNotExistException();
        Cliente cliente = clienteRepository.getReferenceById(cf);
        List<Composizione> results = clienteRepository.findProdottiCarrello(cliente);
        if(results.size() > 0){
            return results;
        }else{
            return new LinkedList<>();
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Carrello aggiornaQuantita(Long id, int quantita) throws ComposizioneNotExistException, ProdottoNotExistsException, CarrelloNotExistException, IllegalQuantitaException {
        Composizione modified = composizioneRepository.getById(id);
        if(!composizioneRepository.existsByProdottoAndCarrello(modified.getProdotto(),modified.getCarrello()))
            throw new ComposizioneNotExistException();
        Prodotto prodotto = prodottoRepository.getById(modified.getProdotto().getId());
        if(quantita <= 0)
            throw new IllegalQuantitaException();
        modified.setQuantita(quantita);
        Carrello carrello = carrelloRepository.getById(modified.getCarrello().getID());
        carrello.setTotale(carrello.getTotale() - modified.getSubtotale());
        double subtotale = modified.getQuantita() * prodotto.getPrezzo();
        modified.setSubtotale(subtotale);
        carrello.setTotale(carrello.getTotale() + subtotale);
        return carrello;
    }
}
