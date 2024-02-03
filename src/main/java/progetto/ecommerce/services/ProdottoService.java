package progetto.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.entity.Recensione;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.repository.CategoriaRepository;
import progetto.ecommerce.repository.ProdottoRepository;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class ProdottoService {

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Prodotto addProdotto(Prodotto prodotto) throws IllegalQuantitaException, CostoNegativoException, CategoriaNotExistException {
        if(prodotto.getQuantita()<=0)
            throw new IllegalQuantitaException();
        if(prodotto.getPrezzo()<=0)
            throw new CostoNegativoException();
        if(!categoriaRepository.existsById(prodotto.getCategoria().getNome()))
            throw new CategoriaNotExistException();
        if(prodottoRepository.existsById(prodotto.getId())){
            Prodotto p = prodottoRepository.findById(prodotto.getId()).get();
            p.setQuantita(p.getQuantita()+ prodotto.getQuantita());
            p.setProductImages(prodotto.getProductImages());
            return prodottoRepository.save(p);
        }else{
            prodotto.setStato("Disponibile");
            prodotto.setVisibilita(true);
            return prodottoRepository.save(prodotto);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeProdotto(String id) throws ProdottoNotExistsException {
        if(!prodottoRepository.existsById(id))
            throw new ProdottoNotExistsException();
        Prodotto prodotto = prodottoRepository.findById(id).get();
        prodotto.setQuantita(0);
        prodotto.setStato("Non Disponibile");
        prodotto.setVisibilita(false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void recuperaProdotto(String id) throws ProdottoNotExistsException {
        if(!prodottoRepository.existsById(id))
            throw new ProdottoNotExistsException();
        Prodotto prodotto = prodottoRepository.findById(id).get();
        prodotto.setStato("Disponibile");
        prodotto.setVisibilita(true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeProdotto(String id, int quantita) throws ProdottoNotExistsException, IllegalQuantitaException, QuantitaNegativaException {
        if(!prodottoRepository.existsById(id))
            throw new ProdottoNotExistsException();
        if(quantita<=0)
            throw new QuantitaNegativaException();
        Prodotto p = prodottoRepository.findById(id).get();
        if(p.getQuantita()<quantita)
            throw new IllegalQuantitaException();
        p.setQuantita(p.getQuantita()-quantita);
        if(p.getQuantita()==0)
            p.setStato("Non Disponibile");

    }

    @Transactional(readOnly = true)
    public List<Prodotto> getAllProdotto(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> pageResult = prodottoRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getAllProdottiEliminati(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> pageResult = prodottoRepository.findProdottiEliminati(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Prodotto> searchProdotto(String id, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> pageResult = prodottoRepository.findByIdStartingWith(id,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Prodotto> searchProdottoNome(String nome, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> pageResult = prodottoRepository.findByNomeStartingWith(nome,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public double calcolaMediaRecensioneProdotto(String id) throws ProdottoNotExistsException {
        if(!prodottoRepository.existsById(id))
            throw new ProdottoNotExistsException();
        return prodottoRepository.calcolaMediaRecensioneProdotto(id);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> searchProdottoEliminato(String id, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> pageResult = prodottoRepository.searchProdottoEliminato(id,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public  List<Prodotto> filtraProdotto(Integer quantita,String stato, String categoria, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> pageResult = prodottoRepository.filtraProdotto(quantita,stato,categoria,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void aggiornaPrezzoProdotto(String id, double newPrezzo) throws ProdottoNotExistsException, CostoNegativoException {
        Prodotto modified = prodottoRepository.getReferenceById(id);
        if(!prodottoRepository.existsById(id) || !modified.isVisibilita())
            throw new ProdottoNotExistsException();
        if(newPrezzo<=0)
            throw new CostoNegativoException();
        modified.setPrezzo(newPrezzo);
        prodottoRepository.save(modified);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void aggiornaQuantitaProdotto(String id, int newQta) throws ProdottoNotExistsException, IllegalQuantitaException {
        Prodotto modified = prodottoRepository.getReferenceById(id);
        if(!prodottoRepository.existsById(id) || !modified.isVisibilita())
            throw new ProdottoNotExistsException();
        if(newQta<=0)
            throw new IllegalQuantitaException();
        modified.setStato("Disponibile");
        modified.setQuantita(newQta);
        prodottoRepository.save(modified);
    }

    @Transactional(readOnly = true)
    public List<Recensione> getAllRecensioniProdotto(String id, int pageNumber, int pageSize, String sortBy) throws ProdottoNotExistsException {
        Prodotto prodotto = prodottoRepository.getReferenceById(id);
        if(!prodottoRepository.existsById(id) || !prodotto.isVisibilita())
            throw new ProdottoNotExistsException();
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Recensione> pageResult = prodottoRepository.findRecensioniProdotto(prodotto,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }
}
