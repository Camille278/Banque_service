package org.miage.banqueservice.Repository;

import org.miage.banqueservice.entity.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRessource extends CrudRepository<Client, String> {
}
