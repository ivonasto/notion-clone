package org.example.notionclone.authentication.user;

import java.util.Optional;

public interface FidoCredentialService {
   Optional<FidoCredential> findCredentialById(String credentialId);


}
