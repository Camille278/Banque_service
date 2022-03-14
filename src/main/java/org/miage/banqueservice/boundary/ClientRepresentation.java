package org.miage.banqueservice.boundary;

import org.miage.banqueservice.Repository.ClientRessource;
import org.miage.banqueservice.entity.Client;
import org.miage.banqueservice.entity.Reponse;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
//@RequestMapping(value="/clients", produces= MediaType.APPLICATION_JSON_VALUE)
//@ExposesResourceFor(Client.class)
public class ClientRepresentation {

    private Environment environment;
    private ClientRessource cr;

    public ClientRepresentation(Environment environment, ClientRessource cr) {
        this.environment = environment;
        this.cr = cr;
    }

    @GetMapping()
    public ResponseEntity<?> getAllTrajets(){

        return ResponseEntity.ok(cr.findAll());
    }

    @GetMapping(value= "/clients/{idClient}/prix/{prix}/payer")
    public Reponse payerCompteClient(@PathVariable("idClient") String id, @PathVariable("prix") Float prix){
        Optional<Client> toUpdate = cr.findById(id);
        String reponseMessage;
        Client toSave = toUpdate.get();
        Reponse toReturn = new Reponse();

        if(toUpdate.isEmpty()){
            toReturn.setMessage("Client inexistant");
        }else{
            if(prix <= toUpdate.get().getMontantCompte()){
                reponseMessage = "Financement effectué";
                toSave.setMontantCompte(toUpdate.get().getMontantCompte()-prix);
                cr.save(toSave);
            }else{
                reponseMessage = "Financement impossible";
            }
            toReturn.setMessage(reponseMessage);
        }

        toReturn.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
        return toReturn;

    }
}
