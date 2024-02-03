package progetto.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import progetto.ecommerce.entity.*;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.repository.*;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class OrdineService {

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AssociatoRepository associatoRepository;

    @Autowired
    private ProdottoService prodottoService;

    @Autowired
    private ComposizioneRepository composizioneRepository;

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ProdottoNotExistsException.class, IllegalQuantitaException.class, QuantitaNegativaException.class})
    public Ordine creaOrdine(Ordine ordine) throws OrdineAlreadyExistexception, ProdottoNotExistsException, QuantitaNegativaException, CarrelloNoSuchElementException, ClienteNotExistException, IllegalScontoException, IllegalQuantitaException {
        if(ordineRepository.existsByNumeroOrdine(ordine.getNumeroOrdine()))
            throw new OrdineAlreadyExistexception();
        if(ordine.getScontoApplicato() < 0)
            throw new IllegalScontoException();
        Cliente cliente = clienteRepository.getById(ordine.getClienteOrdine().getCF());
        if(!clienteRepository.existsById(cliente.getCF()))
            throw new ClienteNotExistException();
        Carrello carrello = carrelloRepository.getById(cliente.getCarrello().getID());
        double totale = carrello.getTotale();
        ordine.setTotale(totale - ((totale * ordine.getScontoApplicato()) / 100));
        Date data = new Date();
        ordine.setData(data);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date dataConsegna = calendar.getTime();
        ordine.setDataConsegna(dataConsegna);
        List<Composizione> prodottiCarrello = ordineRepository.findProdottiCarrello(carrello);
        if(prodottiCarrello.size() == 0)
            throw new CarrelloNoSuchElementException();
        ordine.setStato("Completato");
        ordineRepository.save(ordine);
        ordine.setNumeroOrdine(ordine.getId().toString());
        for(Composizione c:prodottiCarrello){
            Associato associato = new Associato();
            associato.setOrdine(ordine);
            associato.setProdotto(c.getProdotto());
            associato.setQuantita(c.getQuantita());
            associato.setSubtotale(c.getSubtotale());
            associatoRepository.save(associato);
            prodottoService.removeProdotto(c.getProdotto().getId(),c.getQuantita());
            composizioneRepository.delete(c);
        }
        carrello.setTotale(0);
        return ordine;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOrdine(String numeroOrdine) throws OrdineNotExistException, IllegalQuantitaException, ProdottoNotExistsException {
        Ordine ordine = ordineRepository.findByNumeroOrdine(numeroOrdine);
        if(!ordineRepository.existsByNumeroOrdine(numeroOrdine) || ordine.getStato().equals("Annullato"))
            throw new OrdineNotExistException();
        List<Associato> prodottiOrdine = associatoRepository.findByOrdine(ordine);
        for(Associato a: prodottiOrdine){
            prodottoService.aggiornaQuantitaProdotto(a.getProdotto().getId(), (a.getProdotto().getQuantita() + a.getQuantita()));
        }
        ordine.setStato("Annullato");

    }

    @Transactional(readOnly = true)
    public List<Ordine> getAllOrdine(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Ordine> pageResult = ordineRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Ordine> searchOrdine(String numeroOrdine, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Ordine> pageResult = ordineRepository.findByNumeroOrdineStartingWith(numeroOrdine,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Ordine> searchOrdineCliente(String cf, String numeroOrdine, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Ordine> pageResult = ordineRepository.findByNumeroOrdineStartingWith(cf,numeroOrdine,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Ordine> filtraOrdine(String stato, Integer anno, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Ordine> pageResult = ordineRepository.filtraOrdine(stato,anno,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Ordine> filtraOrdineCliente(String cf,String stato, Integer anno, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Ordine> pageResult = ordineRepository.filtraOrdineCliente(cf,stato,anno,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Ordine> getAllOrdiniCliente(String cf, int pageNumber, int pageSize, String sortBy) throws ClienteNotExistException {
        if(!clienteRepository.existsById(cf))
            throw new ClienteNotExistException();
        Cliente cliente = clienteRepository.findById(cf).get();
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Ordine> pageResult = ordineRepository.findByClienteOrdine(cliente,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void aggiornaStatoOrdine(String numeroOrdine, String stato) throws OrdineNotExistException {
        if(!ordineRepository.existsByNumeroOrdine(numeroOrdine))
            throw new OrdineNotExistException();
        Ordine modified = ordineRepository.findByNumeroOrdine(numeroOrdine);
        modified.setStato(stato);
    }

    @Transactional(readOnly = true)
    public List<Associato> visualizzaDettagliOrdine(String idOrder) throws OrdineNotExistException {
        if(!ordineRepository.existsByNumeroOrdine(idOrder))
            throw new OrdineNotExistException();
        List<Associato> pageResult = ordineRepository.findProdottiOrdine(idOrder);
        if(pageResult.size() > 0){
            return pageResult;
        }else{
            return new LinkedList<>();
        }
    }

}
