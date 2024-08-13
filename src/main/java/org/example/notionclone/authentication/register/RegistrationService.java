package org.example.notionclone.authentication.register;

import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.RegistrationFailedException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.example.notionclone.authentication.user.FidoCredential;
import org.example.notionclone.authentication.user.UserAccount;
import org.example.notionclone.authentication.user.UserService;
import org.example.notionclone.authentication.yubico.YubicoUtils;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final RelyingParty relyingParty;


    @Transactional(propagation = Propagation.REQUIRED)
    PublicKeyCredentialCreationOptions startRegistration(RegistrationStartRequest formData) {
        UserAccount user = userService.createUser(formData.getUsername(), formData.getEmail());
        return createPublicKeyCredentialCreationOptions(user);
    }

    private PublicKeyCredentialCreationOptions createPublicKeyCredentialCreationOptions(
            UserAccount user) {
        var userIdentity =
                UserIdentity.builder()
                        .name(user.email())
                        .displayName(user.username())
                        .id(YubicoUtils.toByteArray(user.id()))
                        .build();

        var authenticatorSelectionCriteria =
                AuthenticatorSelectionCriteria.builder()
                        .userVerification(UserVerificationRequirement.DISCOURAGED)
                        .authenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM)
                        .build();

        var startRegistrationOptions =
                StartRegistrationOptions.builder()
                        .user(userIdentity)
                        .timeout(30_000)
                        .authenticatorSelection(authenticatorSelectionCriteria)
                        .build();

        return this.relyingParty.startRegistration(startRegistrationOptions);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void finishRegistration(RegistrationFinishRequest request, PublicKeyCredentialCreationOptions options)
            throws RegistrationFailedException {
        FinishRegistrationOptions finishOptions =
                FinishRegistrationOptions.builder()
                        .request(options)
                        .response(request.getCredential())
                        .build();
        RegistrationResult registrationResult = this.relyingParty.finishRegistration(finishOptions);

        var fidoCredential =
                new FidoCredential(
                        registrationResult.getKeyId().getId().getBase64Url(),
                        registrationResult.getKeyId().getType().name(),
                        YubicoUtils.toUUID(options.getUser().getId()),
                        registrationResult.getPublicKeyCose().getBase64Url());

        this.userService.addCredential(fidoCredential);
    }

}
