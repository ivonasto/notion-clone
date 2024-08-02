package org.example.notionclone.authentication.user;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;
 interface UserRepository extends JpaRepository<UserAccountEntity, UUID> {
     Optional<UserAccountEntity> findByEmail(String email);
}
