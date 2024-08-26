package org.example.notionclone.authentication.login;

import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.exception.AssertionFailedException;
import lombok.AllArgsConstructor;
import org.example.notionclone.json.JsonUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class FidoAuthenticationManager implements AuthenticationManager {
    private final LoginService loginService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var fidoToken = (FidoAuthenticationToken) authentication;

        try {
            AssertionResult assertionResult =
                    this.loginService.finishLogin(fidoToken.getLoginFinishRequest());
            if (assertionResult.isSuccess()) {
                var result = new FidoAuthentication(fidoToken, JsonUtils.toJson(assertionResult));
                return result;
            }
            throw new BadCredentialsException("FIDO WebAuthn failed");
        } catch (AssertionFailedException e) {
            throw new BadCredentialsException("unable to login", e);
        }
    }
}
