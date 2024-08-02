package org.example.notionclone.authentication.register;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import lombok.AllArgsConstructor;
import org.example.notionclone.authentication.user.UserAccount;
import org.example.notionclone.authentication.user.UserService;
import org.example.notionclone.authentication.yubico.YubicoUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final RelyingParty relyingParty;

    PublicKeyCredentialCreationOptions startRegistration(RegistrationStartRequest formData){
        UserAccount user = userService.createUser(formData.getUsername(),formData.getEmail());
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
                        .build();

        var startRegistrationOptions =
                StartRegistrationOptions.builder()
                        .user(userIdentity)
                        .timeout(30_000)
                        .authenticatorSelection(authenticatorSelectionCriteria)
                        .build();

        return this.relyingParty.startRegistration(startRegistrationOptions);
    }



}
