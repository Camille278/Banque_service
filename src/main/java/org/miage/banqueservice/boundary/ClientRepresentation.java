package org.miage.banqueservice.boundary;

import org.miage.banqueservice.Repository.ClientRessource;
import org.miage.banqueservice.entity.Client;
import org.miage.banqueservice.entity.Reponse;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Optional;

@RestController
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
    @Transactional
    public Reponse payerCompteClient(@PathVariable("idClient") String id, @PathVariable("prix") Float prix){
        Optional<Client> toUpdate = cr.findById(id);
        String reponseMessage;
        Reponse toReturn = new Reponse();

        if(toUpdate.isEmpty()){
            toReturn.setMessage("Client inexistant");
        }else{
            Client toSave = toUpdate.get();
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
