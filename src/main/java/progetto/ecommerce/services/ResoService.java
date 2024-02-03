package progetto.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import progetto.ecommerce.entity.Associato;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.Ordine;
import progetto.ecommerce.entity.Reso;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.repository.AssociatoRepository;
import progetto.ecommerce.repository.ClienteRepository;
import progetto.ecommerce.repository.OrdineRepository;
import progetto.ecommerce.repository.ResoRepository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class ResoService {

    @Autowired
    private ResoRepository resoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private OrdineService ordineService;

    @Autowired
    private AssociatoRepository associatoRepository;

    @Autowired
    private ProdottoService prodottoService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Reso addReso(Reso reso) throws ResoAlreadyExistException, OrdineNotExistException, IllegalQuantitaException, ProdottoNotExistsException, IllegalDateResoException {
        Ordine ordine = ordineRepository.findById(reso.getOrdine().getId()).get();
        Cliente cliente = clienteRepository.findById(reso.getCliente().getCF()).get();
        if(resoRepository.existsByOrdine(ordine))
            throw new ResoAlreadyExistException();
        if(!ordineRepository.existsByClienteOrdineAndNumeroOrdine(cliente.getCF(), ordine.getNumeroOrdine()) || ordine.getStato().equals("Annullato"))
            throw new OrdineNotExistException();
        Date today = new Date();
        if(ordine.getDataConsegna().after(today))
            throw new IllegalDateResoException();
        reso.setOrdine(ordine);
        ordineService.removeOrdine(ordine.getNumeroOrdine());
        return resoRepository.save(reso);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeReso(Long id) throws ResoNotExistException, IllegalQuantitaException, ProdottoNotExistsException, QuantitaNegativaException {
        if(!resoRepository.existsById(id))
            throw new ResoNotExistException();
        Reso reso = resoRepository.findById(id).get();
        Ordine ordine = reso.getOrdine();
        ordine.setStato("Consegnato");
        List<Associato> prodottiOrdine = associatoRepository.findByOrdine(ordine);
        for(Associato a: prodottiOrdine){
            prodottoService.removeProdotto(a.getProdotto().getId(), a.getQuantita());
        }
        resoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Reso> getAllReso(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Reso> pageResult = resoRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Reso> getAllResiCliente(String cf, int pageNumber, int pageSize, String sortBy) throws ClienteNotExistException {
        if(!clienteRepository.existsById(cf))
            throw new ClienteNotExistException();
        Cliente cliente = clienteRepository.findById(cf).get();
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Reso> pageResult = resoRepository.findByCliente(cliente,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

}
