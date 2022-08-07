package net.mikoto.pixiv.central.controller;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import net.mikoto.pixiv.central.dao.ClientRepository;
import net.mikoto.pixiv.core.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/7/29
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Component
public class SaOAuth2TemplateImpl extends SaOAuth2Template {
    @Qualifier("clientRepository")
    private final ClientRepository clientRepository;

    @Autowired
    public SaOAuth2TemplateImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @SuppressWarnings("AlibabaSwitchStatement")
    @Override
    public SaClientModel getClientModel(String clientId) {
        Client client = clientRepository.getClientByClientId(Integer.parseInt(clientId));
        return new SaClientModel()
                .setClientId(clientId)
                .setClientSecret(client.getClientSecret())
                .setAllowUrl(client.getAllowUrl())
                .setContractScope(client.getContractScope())
                .setIsAutoMode(true);
    }
}