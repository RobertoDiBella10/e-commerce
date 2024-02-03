package progetto.ecommerce.controllers;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.services.AuthService;

import javax.validation.Valid;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @PermitAll
    public ResponseEntity<String> registraCliente(@RequestBody @Valid Cliente cliente){
        try {
            authService.registraCliente(cliente);
            return ResponseEntity.status(HttpStatus.OK).body("cliente registrato con successo");
        }catch (ClienteAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente già presente");
        }catch (EmailAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email esistente");
        }catch (TelefonoAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero telefonico esistente");
        }catch (ClienteNotRegistractionException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore. Cliente non registrato");
        }
    }

    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<String> removeCliente(@PathVariable String userId){
        try {
            authService.removeCliente(userId);
            return ResponseEntity.status(HttpStatus.OK).body("Cliente rimosso con successo");
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }catch(KeycloakCLienteNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utente Keycloak non trovato");
        }catch (UsernameNotEqualsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Operazione illegale. Impossibile rimuovere un altro account");
        }
    }

    @PutMapping("/recupera/{cf}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> recuperaCliente(@PathVariable String cf){
        try{
            authService.recuperaCliente(cf);
            return ResponseEntity.status(HttpStatus.OK).body("Prodotto recuperato con successo");
        }catch (ClienteNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }

}