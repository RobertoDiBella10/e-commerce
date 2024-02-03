package progetto.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import progetto.ecommerce.entity.Categoria;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.exceptions.CategoriaAlreadyExistException;
import progetto.ecommerce.exceptions.CategoriaNotExistException;
import progetto.ecommerce.services.CategoriaService;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> addCategoria(@Valid @RequestBody Categoria categoria){
        try{
            Categoria added = categoriaService.addCategoria(categoria);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (CategoriaAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria già presente");
        }
    }

    @DeleteMapping("/remove/{nome}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> removeCategoria(@PathVariable String nome){
        try{
            categoriaService.removeCategoria(nome);
            return ResponseEntity.status(HttpStatus.OK).body("Categoria rimossa con successo");
        }catch (CategoriaNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria inesistente");

        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<Object> getAllCategorie(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                  @RequestParam(value = "sortBy", defaultValue = "nome") String sortBy){
        List<Categoria> result = categoriaService.getAllCategorie(pageNumber,pageSize,sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{nomeCategoria}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchCategoria(@PathVariable String nomeCategoria, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                  @RequestParam(value = "sortBy", defaultValue = "nome") String sortBy){
        List<Categoria> result = categoriaService.searchCategoria(nomeCategoria, pageNumber, pageSize, sortBy);
        if ( result.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
