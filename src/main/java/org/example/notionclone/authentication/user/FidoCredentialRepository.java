package org.example.notionclone.authentication.user;

import org.springframework.data.jpa.repository.JpaRepository;


interface FidoCredentialRepository extends JpaRepository<FidoCredentialEntity,String> {

}
