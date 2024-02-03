package progetto.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.entity.Recensione;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.repository.AssociatoRepository;
import progetto.ecommerce.repository.ClienteRepository;
import progetto.ecommerce.repository.ProdottoRepository;
import progetto.ecommerce.repository.RecensioneRepository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AssociatoRepository associatoRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Autowired
    private ProdottoService prodottoService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Recensione addRecensione(Recensione recensione) throws RecensioneAlreadyExistException, ValutazioneOutOfBoundException, ProdottoNotExistsException, ClienteNotExistException, AssociatoNotExistException {
        if(!prodottoRepository.existsById(recensione.getProdottoRecensito().getId()))
            throw new ProdottoNotExistsException();
        if(!clienteRepository.existsById(recensione.getCliente().getCF()))
            throw new ClienteNotExistException();
        if(recensioneRepository.existsByClienteAndProdottoRecensito(recensione.getCliente(), recensione.getProdottoRecensito()))
            throw new RecensioneAlreadyExistException();
        if(recensione.getValutazione()<1 || recensione.getValutazione() >5)
            throw new ValutazioneOutOfBoundException();
        if(!associatoRepository.isClientBuyProduct(recensione.getCliente().getCF(),recensione.getProdottoRecensito().getId()))
            throw new AssociatoNotExistException();
        recensione.setData(new Date());
        Prodotto prodotto = prodottoRepository.findById(recensione.getProdottoRecensito().getId()).get();
        Recensione saved = recensioneRepository.save(recensione);
        double media = prodottoService.calcolaMediaRecensioneProdotto(prodotto.getId());
        prodotto.setAvgValutazione(media);
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeRecensione(Long id) throws RecensioneNotExistException, ProdottoNotExistsException {
        if(!recensioneRepository.existsById(id))
            throw new RecensioneNotExistException();
        Recensione recensione = recensioneRepository.findById(id).get();
        Prodotto prodotto = prodottoRepository.findById(recensione.getProdottoRecensito().getId()).get();
        double media = prodottoService.calcolaMediaRecensioneProdotto(prodotto.getId());
        prodotto.setAvgValutazione(media);
        recensioneRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Recensione> getAllRecensioni(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Recensione> pageResult = recensioneRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Recensione> getAllRecensioniCliente(String cf,int pageNumber, int pageSize, String sortBy) throws ClienteNotExistException {
        if(!clienteRepository.existsById(cf))
            throw new ClienteNotExistException();
        Cliente cliente = clienteRepository.findById(cf).get(); //utilizzato questo perchè mi dava problemi nella serializzazione del JSON di risposta
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Recensione> pageResult = recensioneRepository.findByCliente(cliente, paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

}
