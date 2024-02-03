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
import progetto.ecommerce.repository.ClienteRepository;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> getAllClienti(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Cliente> pageResult = clienteRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public Cliente getCliente(String cf) throws ClienteNotExistException {
        if(!clienteRepository.existsById(cf))
            throw new ClienteNotExistException();
        return clienteRepository.findById(cf).get();
    }

    @Transactional(readOnly = true)
    public List<Cliente> getAllClientiEliminati(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Cliente> pageResult = clienteRepository.findClientiEliminati(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Cliente> searchCliente(String cf, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Cliente> pageResult = clienteRepository.findByCFStartingWith(cf,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Cliente> searchClienteEliminato(String cf, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Cliente> pageResult = clienteRepository.searchClienteEliminato(cf,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Cliente> filtraCliente(String nome, String cognome, String citta, String via, Integer cap, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Cliente> pageResult = clienteRepository.filtraClienti(nome,cognome,citta,via,cap,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Cliente updateCliente(Cliente cliente) throws ClienteNotExistException {
        if(!clienteRepository.existsById(cliente.getCF()))
            throw new ClienteNotExistException();
        Cliente update = clienteRepository.findById(cliente.getCF()).get();
        update.setCap(cliente.getCap());
        update.setCitta(cliente.getCitta());
        update.setCognome(cliente.getCognome());
        update.setNome(cliente.getNome());
        update.setVia(cliente.getVia());
        update.setCitta(cliente.getCitta());
        update.setProvincia(cliente.getProvincia());
        return update;
    }

    @Transactional(readOnly = true)
    public boolean isVisibile(String username) throws ClienteNotExistException {
        if(!clienteRepository.existsByUsername(username))
            throw new ClienteNotExistException();
        return clienteRepository.findByUsernameIsVisibile(username);
    }
}

