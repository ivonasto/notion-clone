package org.example.notionclone.authentication.login;

import com.yubico.webauthn.AssertionRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginStartResponse {
    private UUID flowId;

    private AssertionRequest assertionRequest;
}
