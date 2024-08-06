package org.example.notionclone.authentication.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@AllArgsConstructor
@Service
public class FidoCredentialServiceImpl implements FidoCredentialService{
    private final FidoCredentialRepository fidoCredentialRepository;


    @Override
    public Optional<FidoCredential> findCredentialById(String credentialId) {
        return fidoCredentialRepository.findById(credentialId).map(FidoCredentialServiceImpl::toFidoCredential);    }



    private static FidoCredential toFidoCredential(FidoCredentialEntity fidoCredentialEntity) {
        return new FidoCredential(fidoCredentialEntity.getId(), fidoCredentialEntity.getType(), fidoCredentialEntity.getUserId(), fidoCredentialEntity.getPublicKeyCose());
        }

}
