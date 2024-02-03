package progetto.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import progetto.ecommerce.entity.Carrello;
import progetto.ecommerce.entity.Composizione;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.services.CarrelloService;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/carrello")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;

    @PostMapping("/addProdotto/{IDprodotto}/{cfCliente}/{quantita}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> addProdottoCarrello(@PathVariable String IDprodotto, @PathVariable String cfCliente, @PathVariable int quantita){
        try{
            Composizione added = carrelloService.addProdottoCarrello(IDprodotto, cfCliente, quantita);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (CarrelloNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Carrello inesistente");
        }catch (IllegalQuantitaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantità inserita non valida");
        }catch(VincoloUniqueException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto gia presente nel carrello");
        }
    }

    @DeleteMapping("/removeProdotto/{id}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> removeProdottoCarrello(@PathVariable Long id){
        try{
            Carrello update = carrelloService.removeProdottoCarrello(id);
            return ResponseEntity.status(HttpStatus.OK).body(update);
        }catch (ComposizioneNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto non presente nel carrello");
        }
    }

    @GetMapping("/visualizza/{cf}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> visualizzaCarrello(@PathVariable String cf){
        try {
            List<Composizione> result = carrelloService.visualizzaCarrello(cf);
            if (result.size() == 0) {
                return ResponseEntity.status(HttpStatus.OK).body("No results!");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }

    @PutMapping("/updateQuantita/{id}/{quantita}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> aggiornaQuantita(@PathVariable Long id,@PathVariable int quantita){
        try{
            Carrello update = carrelloService.aggiornaQuantita(id,quantita);
            return ResponseEntity.status(HttpStatus.OK).body(update);
        }catch (ComposizioneNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Composizione inesistente");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (CarrelloNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Carrello inesistente");
        }catch (IllegalQuantitaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantità inserita non valida");
        }
    }
}
