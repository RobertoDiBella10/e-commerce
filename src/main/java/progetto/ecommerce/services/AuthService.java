package progetto.ecommerce.services;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import progetto.ecommerce.configurations.Credentials;
import progetto.ecommerce.entity.Carrello;
import progetto.ecommerce.entity.Cliente;
import progetto.ecommerce.entity.ERole;
import progetto.ecommerce.entity.Role;
import progetto.ecommerce.exceptions.*;
import progetto.ecommerce.repository.ClienteRepository;
import progetto.ecommerce.repository.RoleRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class AuthService {

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    private UsersResource usersResource;

    @Transactional(propagation = Propagation.REQUIRED)
    public void registraCliente(Cliente cliente) throws ClienteAlreadyExistException, EmailAlreadyExistException, TelefonoAlreadyExistException, ClienteNotRegistractionException {
        if(clienteRepository.existsById(cliente.getCF()))
            throw new ClienteAlreadyExistException();
        if(clienteRepository.existsByEmail(cliente.getEmail()))
            throw new EmailAlreadyExistException();
        if(clienteRepository.existsByTelefono(cliente.getTelefono()))
            throw new TelefonoAlreadyExistException();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Response response = registrazioneClienteToKeycloak(cliente);

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            Carrello carrello = new Carrello();
            carrello.setCliente(cliente);
            carrello.setTotale(0.0);
            cliente.setCarrello(carrello);
            cliente.setRole(userRole);
            cliente.setVisibile(true);
            cliente.setPassword(encoder.encode(cliente.getPassword()));
            clienteRepository.save(cliente);
        } else {
            throw new ClienteNotRegistractionException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeCliente(String cf) throws ClienteNotExistException, KeycloakCLienteNotFoundException, UsernameNotEqualsException {
        if(!clienteRepository.existsById(cf))
            throw new ClienteNotExistException();
        Cliente cliente = clienteRepository.findById(cf).get();
        cliente.setVisibile(false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void recuperaCliente(String cf) throws ClienteNotExistException {
        if(!clienteRepository.existsById(cf))
            throw new ClienteNotExistException();
        Cliente cliente = clienteRepository.findById(cf).get();
        String userId = getKeycloakUserIdByCF(cliente.getUsername());
        if(userId == null) {
            Response response = registrazioneClienteToKeycloak(cliente);

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                cliente.setVisibile(true);
            }
        }else{
            cliente.setVisibile(true);
        }
    }

    private String getKeycloakUserIdByCF(String username) {
        RealmResource realmResource = keycloak.realm("ecommerce");
        usersResource = realmResource.users();
        List<UserRepresentation> users = usersResource.search(username);
        if (users != null && !users.isEmpty()) {
            return users.get(0).getId();
        }
        return null;
    }

    private Response registrazioneClienteToKeycloak(Cliente cliente){
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(cliente.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(cliente.getUsername());
        user.setFirstName(cliente.getNome());
        user.setLastName(cliente.getCognome());
        user.setEmail(cliente.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        user.setAttributes(Collections.singletonMap("cf", Collections.singletonList(cliente.getCF())));
        user.setClientRoles(Collections.singletonMap(cliente.getCF(), Arrays.asList("role_user")));
        user.setEmailVerified(false);
        user.setEnabled(true);
        RealmResource realmResource = keycloak.realm("ecommerce");
        usersResource = realmResource.users();
        Response response = usersResource.create(user);
        if (response.getStatus() == 200 || response.getStatus() == 201) {
            //getting client
            ClientRepresentation client = realmResource.clients()
                    .findByClientId("spring-client").get(0);
            String userId = getKeycloakUserIdByCF(cliente.getUsername());
            UserResource userResource = usersResource.get(userId);
            RoleRepresentation userClientRole = realmResource.clients().get(client.getId())
                    .roles().get("role_user").toRepresentation();
            userResource.roles()
                    .clientLevel(client.getId()).add(Arrays.asList(userClientRole));
        }
        return response;
    }



}
