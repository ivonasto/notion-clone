package org.example.notionclone.authentication.login;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface LoginFlowRepository extends JpaRepository<LoginFlowEntity, UUID> {}
