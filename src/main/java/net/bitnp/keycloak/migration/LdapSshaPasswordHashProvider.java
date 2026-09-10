package net.bitnp.keycloak.migration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.credential.dto.PasswordCredentialData;
import org.keycloak.models.credential.dto.PasswordSecretData;

/** Verifies imported OpenLDAP SSHA credentials without creating new SSHA hashes. */
public final class LdapSshaPasswordHashProvider implements PasswordHashProvider {

    @Override
    public boolean policyCheck(PasswordPolicy policy, PasswordCredentialModel credential) {
        return false;
    }

    @Override
    public PasswordCredentialModel encodedCredential(String rawPassword, int iterations) {
        throw new UnsupportedOperationException("The ldap-ssha provider only verifies imported credentials");
    }

    @Override
    public boolean verify(String rawPassword, PasswordCredentialModel credential) {
        if (rawPassword == null || credential == null) {
            return false;
        }

        PasswordCredentialData data = credential.getPasswordCredentialData();
        PasswordSecretData secret = credential.getPasswordSecretData();
        if (data == null || secret == null
                || !LdapSshaPasswordHashProviderFactory.ID.equals(data.getAlgorithm())
                || data.getHashIterations() != 1
                || secret.getValue() == null
                || secret.getSalt() == null
                || secret.getSalt().length == 0) {
            return false;
        }

        byte[] expected;
        try {
            expected = Base64.getDecoder().decode(secret.getValue());
        } catch (IllegalArgumentException e) {
            return false;
        }
        if (expected.length != 20) {
            return false;
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 is unavailable for legacy credential verification", e);
        }
        digest.update(rawPassword.getBytes(StandardCharsets.UTF_8));
        digest.update(secret.getSalt());
        return MessageDigest.isEqual(digest.digest(), expected);
    }

    @Override
    public void close() {
    }
}
