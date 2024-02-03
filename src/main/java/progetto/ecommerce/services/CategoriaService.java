package progetto.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import progetto.ecommerce.entity.Categoria;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.exceptions.CategoriaAlreadyExistException;
import progetto.ecommerce.exceptions.CategoriaNotExistException;
import progetto.ecommerce.repository.CategoriaRepository;
import progetto.ecommerce.repository.ProdottoRepository;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("ALL")
@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Categoria addCategoria(Categoria categoria) throws CategoriaAlreadyExistException {
        if(categoriaRepository.existsById(categoria.getNome()))
            throw new CategoriaAlreadyExistException();
        return categoriaRepository.save(categoria);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeCategoria(String nome) throws CategoriaNotExistException {
        if(!categoriaRepository.existsById(nome))
            throw new CategoriaNotExistException();
        List<Prodotto> prodotti = prodottoRepository.findByCategoria(nome);
        for(Prodotto p:prodotti)
            p.setVisibilita(false);
        categoriaRepository.deleteById(nome);
    }

    @Transactional(readOnly = true)
    public List<Categoria> getAllCategorie(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Categoria> pageResult = categoriaRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Categoria> searchCategoria(String nomeCategoria, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Categoria> pageResult = categoriaRepository.findByNomeStartingWith(nomeCategoria,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }
}
