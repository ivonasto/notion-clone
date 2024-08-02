package org.example.notionclone.authentication.user;

import java.util.Optional;

public interface UserService {
    UserAccount createUser(String username, String email);
    Optional<UserAccount> findByEmail(String email);





}
