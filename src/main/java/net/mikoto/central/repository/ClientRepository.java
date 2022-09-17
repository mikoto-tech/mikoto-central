package net.mikoto.central.repository;

import net.mikoto.oauth2.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("clientRepository")
public interface ClientRepository extends JpaRepository<Client, Integer> {
    /**
     * Get client by client name.
     *
     * @param clientName The client name.
     * @return The client.
     */
    Client getClientByClientName(String clientName);
}
