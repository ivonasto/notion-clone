package org.example.notionclone.authentication.user;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserAccount createUser(String username, String email);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findById(UUID id);
    void addCredential(FidoCredential fidoCredential);







}
