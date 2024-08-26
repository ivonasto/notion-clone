package org.example.notionclone.authentication.login;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginFinishRequest {
    private UUID flowId;

    private PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>
            credential;
}
