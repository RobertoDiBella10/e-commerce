package progetto.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import progetto.ecommerce.entity.Reso;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.services.ResoService;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/reso")
public class ResoController {

    @Autowired
    private ResoService resoService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> addReso(@Valid @RequestBody Reso reso){
        try{
            resoService.addReso(reso);
            return ResponseEntity.status(HttpStatus.OK).body("Reso effettuato con successo");
        }catch (ResoAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reso già richiesto");
        }catch (OrdineNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossibile richiedere reso. Ordine inesistente");
        }catch (IllegalQuantitaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("quantità inserita non valida");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (IllegalDateResoException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossibile annullare Reso: Ordine ancora in Consegna");
        }
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<String> removeReso(@PathVariable Long id){
        try {
            resoService.removeReso(id);
            return ResponseEntity.status(HttpStatus.OK).body("Reso rimosso con successo");
        }catch (ResoNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reso inesistente");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (QuantitaNegativaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero di elementi inseriti non validi");
        }catch (IllegalQuantitaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero elementi inferiori a quanto richiesto");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllReso(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                     @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Reso> result = resoService.getAllReso(pageNumber, pageSize, sortBy);
        if(result.size()==0)
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{cf}/all")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> getAllResiCliente(@PathVariable String cf, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        try {
            List<Reso> result = resoService.getAllResiCliente(cf, pageNumber, pageSize, sortBy);
            if (result.size() == 0)
                return ResponseEntity.status(HttpStatus.OK).body("No results!");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.OK).body("Cliente inesistente");
        }
    }
}
