package progetto.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import progetto.ecommerce.entity.Associato;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.exceptions.CarrelloNoSuchElementException;
import progetto.ecommerce.entity.Ordine;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.services.OrdineService;
import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/ordine")
public class OrdineController {

    @Autowired
    private OrdineService ordineService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> creaOrdine(@Valid @RequestBody Ordine ordine){
        try{
            Ordine added = ordineService.creaOrdine(ordine);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (OrdineAlreadyExistexception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ordine presente");
        } catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (IllegalQuantitaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nel magazzino sono presenti un numero inferiore di elementi");
        }catch (QuantitaNegativaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantità inserita non valida");
        }catch (CarrelloNoSuchElementException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nessun prodotto presente nel carrello");
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }catch (IllegalScontoException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sconto inserito non valido");
        }
    }

    @DeleteMapping("/remove/{numeroOrdine}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> removeOrdine(@PathVariable String numeroOrdine){
        try{
            ordineService.removeOrdine(numeroOrdine);
            return ResponseEntity.status(HttpStatus.OK).body("Ordine rimosso con successo");
        }catch (OrdineNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ordine inesistente");
        }catch (IllegalQuantitaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("quantità inserita non valida");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @PutMapping("/aggiornaStato")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> aggiornaStatoOrdine(@RequestParam String numeroOrdine, @RequestParam String stato){
        try {
            ordineService.aggiornaStatoOrdine(numeroOrdine, stato);
            return ResponseEntity.status(HttpStatus.OK).body("Stato ordine aggiornato con successo");
        }catch (OrdineNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ordine inesistente");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllOrdine(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                              @RequestParam(value = "sortBy", defaultValue = "numeroOrdine") String sortBy){
        List<Ordine> result = ordineService.getAllOrdine(pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{numeroOrdine}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchOrdine(@PathVariable String numeroOrdine, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                       @RequestParam(value = "sortBy", defaultValue = "numeroOrdine") String sortBy){
            List<Ordine> result = ordineService.searchOrdine(numeroOrdine, pageNumber, pageSize, sortBy);
            if ( result.size() == 0 ) {
                return ResponseEntity.status(HttpStatus.OK).body("No results!");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{numeroOrdine}/{cf}")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<Object> searchOrdineCliente( @PathVariable String cf, @PathVariable String numeroOrdine, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                               @RequestParam(value = "sortBy", defaultValue = "numeroOrdine") String sortBy){
        List<Ordine> result = ordineService.searchOrdineCliente(cf,numeroOrdine, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    @GetMapping("/filtra")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> filtraOrdine(@RequestParam(required = false) String stato, @RequestParam(required = false) Integer anno,
                                               @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                               @RequestParam(value = "sortBy", defaultValue = "numeroOrdine") String sortBy){
        List<Ordine> result = ordineService.filtraOrdine(stato, anno, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/filtra/{cf}")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<Object> filtraOrdineCliente(@PathVariable String cf, @RequestParam(required = false) String stato, @RequestParam(required = false) Integer anno,
                                               @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                               @RequestParam(value = "sortBy", defaultValue = "numeroOrdine") String sortBy){
        List<Ordine> result = ordineService.filtraOrdineCliente(cf,stato, anno, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/all/{cf}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> getAllOrdiniCliente(@PathVariable String cf, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                             @RequestParam(value = "sortBy", defaultValue = "numeroOrdine") String sortBy){
        try{
            List<Ordine> result = ordineService.getAllOrdiniCliente(cf, pageNumber, pageSize, sortBy);
            if ( result.size() == 0 ) {
                return ResponseEntity.status(HttpStatus.OK).body("No results!");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }

    @GetMapping("/details/{idOrder}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> visualizzaDettagliOrdine(@PathVariable String idOrder){
        try{
            List<Associato> result = ordineService.visualizzaDettagliOrdine(idOrder);
            if ( result.size() == 0 ) {
                return ResponseEntity.status(HttpStatus.OK).body("No results!");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (OrdineNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ordine inesistente");
        }
    }

}
