package progetto.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import progetto.ecommerce.entity.Image;
import progetto.ecommerce.entity.Prodotto;
import progetto.ecommerce.entity.Recensione;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.services.ProdottoService;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/prodotto")
public class ProdottoController {

    @Autowired
    private ProdottoService prodottoService;

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> addProdotto(@RequestPart("product") Prodotto prodotto, @RequestPart("imageFile") MultipartFile[] file){
        try{
            Set<Image> images = uploadImage(file);
            prodotto.setProductImages(images);
            Prodotto added = prodottoService.addProdotto(prodotto);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (IllegalQuantitaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero di elementi inseriti non validi");
        }catch (CostoNegativoException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Costo inserito non valido");
        }catch (CategoriaNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria inesistente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossibile caricare l'immagine");
        }
    }

    public Set<Image> uploadImage(MultipartFile[] multipartFiles) throws IOException {
        Set<Image> images = new HashSet<>();
        for(MultipartFile file: multipartFiles){
            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setType(file.getContentType());
            image.setPicByte(file.getBytes());
            images.add(image);
        }
        return images;
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> removeProdotto(@PathVariable String id){
        try{
            prodottoService.removeProdotto(id);
            return ResponseEntity.status(HttpStatus.OK).body("Prodotto rimosso con successo");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @PutMapping("/recupera/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> recuperaProdotto(@PathVariable String id){
        try{
            prodottoService.recuperaProdotto(id);
            return ResponseEntity.status(HttpStatus.OK).body("Prodotto recuperato con successo");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @DeleteMapping("/remove/{ID}/{quantita}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> removeProdotto(@PathVariable String ID, @PathVariable int quantita){
        try{
            prodottoService.removeProdotto(ID,quantita);
            return ResponseEntity.status(HttpStatus.OK).body("Aggiornamento quantità prodotto effettuata con successo");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (QuantitaNegativaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero di elementi inseriti non validi");
        }catch (IllegalQuantitaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero elementi inferiori a quanto richiesto");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<Object> getAllProdotto(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Prodotto> result = prodottoService.getAllProdotto(pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/all/onlyDelete")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllProdottiEliminati(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                 @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Prodotto> result = prodottoService.getAllProdottiEliminati(pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchProdotto(@PathVariable String id, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Prodotto> result = prodottoService.searchProdotto(id, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/searchNome/{nome}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> searchProdottoNome(@PathVariable String nome, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                 @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Prodotto> result = prodottoService.searchProdottoNome(nome, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/calcolaMediaRecensioni/{id}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> calcolaMediaRecensioneProdotto(@PathVariable String id){
        try{
            double media = prodottoService.calcolaMediaRecensioneProdotto(id);
            return ResponseEntity.status(HttpStatus.OK).body(media);
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @GetMapping("/searchDelete/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchProdottoEliminato(@PathVariable String id, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                 @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Prodotto> result = prodottoService.searchProdottoEliminato(id, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/filtra")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> filtraProdotto( @RequestParam(required = false) Integer quantita, @RequestParam(required = false) String stato, @RequestParam(required = false) String categoria,
                                          @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                          @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Prodotto> result = prodottoService.filtraProdotto(quantita, stato, categoria, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/aggiornaPrezzo")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> aggiornaPrezzoProdotto(@RequestParam String id,@RequestParam double newPrezzo){
        try{
            prodottoService.aggiornaPrezzoProdotto(id, newPrezzo);
            return ResponseEntity.status(HttpStatus.OK).body("Prezzo aggiornato con successo");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (CostoNegativoException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Costo inserito non valido");
        }
    }

    @PutMapping("/aggiornaQuantita")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> aggiornaQuantitaProdotto(@RequestParam String id, @RequestParam int newQta){
        try{
            prodottoService.aggiornaQuantitaProdotto(id, newQta);
            return ResponseEntity.status(HttpStatus.OK).body("Quantità aggiornata con successo");
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (IllegalQuantitaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero elementi inseriti non validi");
        }
    }

    @GetMapping("/{id}/recensione/all")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> getAllRecensioniProdotto(@PathVariable String id, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                   @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        try {
            List<Recensione> result = prodottoService.getAllRecensioniProdotto(id, pageNumber, pageSize, sortBy);
            if (result.size() == 0) {
                return ResponseEntity.status(HttpStatus.OK).body("No results!");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (ProdottoNotExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }
}
