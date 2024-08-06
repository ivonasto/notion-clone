package org.example.notionclone.authentication.yubico;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import com.yubico.webauthn.data.exception.Base64UrlException;
import lombok.AllArgsConstructor;
import org.example.notionclone.authentication.user.FidoCredential;
import org.example.notionclone.authentication.user.FidoCredentialService;
import org.example.notionclone.authentication.user.UserAccount;
import org.example.notionclone.authentication.user.UserService;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CredentialRepositoryImpl implements CredentialRepository {

    private final UserService userService;
    private final FidoCredentialService fidoService;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {

        // find user, get his credentials, map them to descriptor

        return userService
                .findByEmail(username)
                .map(
                        user ->
                                user.credentials().stream()
                                        .map(CredentialRepositoryImpl::toPublicKeyCredentialDescriptor)
                                        .collect(Collectors.toSet()))
                .orElse(Set.of());

    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String s) {
        if (s.isEmpty()) {
            return Optional.empty();
        }

        return userService.findByEmail(s).map(user -> YubicoUtils.toByteArray(user.id()));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray byteArray) {
        if (byteArray.isEmpty()) {
            return Optional.empty();
        }

        return userService.findById(YubicoUtils.toUUID(byteArray)).map(UserAccount::email);
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return this.userService
                .findById(YubicoUtils.toUUID(userHandle))
                .map(UserAccount::credentials)
                .orElse(Set.of())
                .stream()
                .filter(
                        cred -> {
                            try {
                                return credentialId.equals(ByteArray.fromBase64Url(cred.keyId()));
                            } catch (Base64UrlException e) {
                                throw new RuntimeException(e);
                            }
                        })
                .findFirst()
                .map(CredentialRepositoryImpl::toRegisteredCredential);
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return this.fidoService
                .findCredentialById(credentialId.getBase64Url())
                .map(CredentialRepositoryImpl::toRegisteredCredential)
                .map(Set::of)
                .orElse(Set.of());
    }

    private static RegisteredCredential toRegisteredCredential(FidoCredential fidoCredential) {
        try {
            return RegisteredCredential.builder()
                    .credentialId(ByteArray.fromBase64Url(fidoCredential.keyId()))
                    .userHandle(YubicoUtils.toByteArray(fidoCredential.userid()))
                    .publicKeyCose(ByteArray.fromBase64Url(fidoCredential.publicKey()))
                    .build();
        } catch (Base64UrlException e) {
            throw new RuntimeException(e);
        }
    }

    private static PublicKeyCredentialDescriptor toPublicKeyCredentialDescriptor(
            FidoCredential cred) {
        try {
            return PublicKeyCredentialDescriptor.builder()
                    .id(ByteArray.fromBase64Url(cred.keyId()))
                    .type(PublicKeyCredentialType.valueOf(cred.keyType()))
                    .build();

        } catch (Base64UrlException e) {
            throw new RuntimeException(e);
        }
    }
}
