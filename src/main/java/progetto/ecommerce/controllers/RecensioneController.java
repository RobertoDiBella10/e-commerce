package progetto.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import progetto.ecommerce.entity.Recensione;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.services.RecensioneService;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/recensione")
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<Object> addRecensione(@RequestBody @Valid Recensione recensione){
        try{
            Recensione added = recensioneService.addRecensione(recensione);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (RecensioneAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("hai già recensito questo prodotto");
        }catch (ValutazioneOutOfBoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Inserire una valutazione tra 1 e 5");
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }catch (ProdottoNotExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch(AssociatoNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("non hai acquistato il seguente prodotto");
        }
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<String> removeRecensione(@PathVariable Long id){
        try{
            recensioneService.removeRecensione(id);
            return ResponseEntity.status(HttpStatus.OK).body("Recensione rimossa con successo");
        }catch (RecensioneNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recensione inesistente");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }

    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllRecensioni(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(value = "sortBy", defaultValue = "data") String sortBy){
        List<Recensione> result = recensioneService.getAllRecensioni(pageNumber, pageSize, sortBy);
        if(result.size()==0)
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/cliente/{cf}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> getAllRecensioniCliente(@PathVariable String cf,@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                  @RequestParam(value = "sortBy", defaultValue = "data") String sortBy){
        try {
            List<Recensione> result = recensioneService.getAllRecensioniCliente(cf, pageNumber, pageSize, sortBy);
            if(result.size()==0)
                return ResponseEntity.status(HttpStatus.OK).body("No results!");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }
}
