package net.mikoto.pixiv.central.dao;

import net.mikoto.pixiv.core.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("clientRepository")
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client getClientByClientId(int clientId);
}
