package org.example.notionclone.authentication.user;

import java.util.Objects;
import java.util.UUID;

public record FidoCredential(String keyId, String keyType, UUID userid, String publicKey) {
    public FidoCredential {
        Objects.requireNonNull(keyId);
    }
}