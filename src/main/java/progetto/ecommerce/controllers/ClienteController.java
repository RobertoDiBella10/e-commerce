package progetto.ecommerce.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.services.ClienteService;
import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/update")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<String> updateCliente(@RequestBody @Valid Cliente cliente){
        try {
            clienteService.updateCliente(cliente);
            return ResponseEntity.status(HttpStatus.OK).body("cliente registrato con successo");
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllClienti(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                        @RequestParam(value = "sortBy", defaultValue = "cognome") String sortBy){
        List<Cliente> result = clienteService.getAllClienti(pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{cf}")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<Object> getCliente(@PathVariable String cf){
        try {
            Cliente result = clienteService.getCliente(cf);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }

    @GetMapping("/all/onlyDelete")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllClientiEliminati(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Cliente> result = clienteService.getAllClientiEliminati(pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{cf}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchCliente(@PathVariable String cf, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                        @RequestParam(value = "sortBy", defaultValue = "cognome") String sortBy){
        List<Cliente> result = clienteService.searchCliente(cf, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/searchDelete/{cf}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchClienteEliminato(@PathVariable String cf, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Cliente> result = clienteService.searchClienteEliminato(cf, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/filtra")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> filtraCliente(@RequestParam(required = false) String nome, @RequestParam(required = false) String cognome, @RequestParam(required = false) String citta,
                                        @RequestParam(required = false) String via, @RequestParam(required = false) Integer cap,
                                        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                        @RequestParam(value = "sortBy", defaultValue = "cognome") String sortBy) {

        List<Cliente> result = clienteService.filtraCliente(nome, cognome, citta, via, cap, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/checkVisibile/{username}")
    @PermitAll
    public ResponseEntity<Object> isVisibile(@PathVariable String username){
        try{
            boolean isVisibile = clienteService.isVisibile(username);
            return ResponseEntity.status(HttpStatus.OK).body(isVisibile);
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}
