package org.miage.banqueservice.boundary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get toutes les clients", description = "Retourner toutes les clients", tags = {"clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succès",
                    content = @Content(schema = @Schema(implementation = Client.class)))})
    @GetMapping(value = "/clients")
    public ResponseEntity<?> getAllClients(){
        return ResponseEntity.ok(cr.findAll());
    }

    @Operation(summary = "GET un compte client", description = "Récupérer le compte d'un client", tags = {"client"})
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "idClient", description = "Client id"),
            @Parameter(in = ParameterIn.PATH, name = "prix", description = "prix à vérifier")})
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
