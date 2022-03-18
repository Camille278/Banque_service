package org.miage.banqueservice;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.miage.banqueservice.Repository.ClientRessource;
import org.miage.banqueservice.entity.Client;
import org.miage.banqueservice.entity.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientRepresentationTests {

    @LocalServerPort
    int port;

    @Autowired
    ClientRessource cr;

    @MockBean
    Reponse reponse;

    @BeforeEach
    public void setupContext() {
        cr.deleteAll();
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Payer une réservation")
    public void payerReservation() {
        Client c1 = new Client("1", "Beirao", 140.00F);
        cr.save(c1);

        Response response = when().get("/clients/"+c1.getId()+"/prix/100.00/payer").then().statusCode(HttpStatus.SC_OK)
                .extract().response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("Financement effectué"));
    }

    @Test
    @DisplayName("Mise à jour du montant du compte")
    public void majMontant() {
        Client c1 = new Client("1", "Beirao", 140.00F);
        cr.save(c1);

        Response response = when().get("/clients/"+c1.getId()+"/prix/100.00/payer").then().statusCode(HttpStatus.SC_OK)
                .extract().response();


        Optional<Client> client = cr.findById("1");
        assertEquals(40.00F, client.get().getMontantCompte());
    }

    @Test
    @DisplayName("Paiement impossible")
    public void impossibilitePaiement() {
        Client c1 = new Client("1", "Beirao", 0.00F);
        cr.save(c1);

        Response response = when().get("/clients/"+c1.getId()+"/prix/100.00/payer").then().statusCode(HttpStatus.SC_OK)
                .extract().response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("Financement impossible"));
    }

    @Test
    @DisplayName("Client inexistant")
    public void clientInexistant() {

        Response response = when().get("/clients/1/prix/100.00/payer").then().statusCode(HttpStatus.SC_OK)
                .extract().response();

        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("Client inexistant"));
    }
}
