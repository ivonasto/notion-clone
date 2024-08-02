package org.example.notionclone.authentication.user;

import java.util.Set;
import java.util.UUID;

public record UserAccount(UUID id, String username, String email, Set<FidoCredential> credentials) {
}
