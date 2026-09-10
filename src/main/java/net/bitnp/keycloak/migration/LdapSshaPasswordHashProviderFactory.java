package net.bitnp.keycloak.migration;

import org.keycloak.Config;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.credential.hash.PasswordHashProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public final class LdapSshaPasswordHashProviderFactory implements PasswordHashProviderFactory {
    public static final String ID = "ldap-ssha";

    @Override
    public PasswordHashProvider create(KeycloakSession session) {
        return new LdapSshaPasswordHashProvider();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int order() {
        return -1000;
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }
}
