package org.example.notionclone.authentication.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import lombok.AllArgsConstructor;
import org.example.notionclone.authentication.user.UserAccount;
import org.example.notionclone.authentication.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

import static org.example.notionclone.json.JsonUtils.toJson;


@Service
@AllArgsConstructor
public class LoginService {
    private final RelyingParty relyingParty;
    private final UserService userService;
    private final LoginFlowRepository loginFlowRepository;


    @Transactional(propagation = Propagation.REQUIRED)
    public LoginStartResponse startLogin(LoginStartRequest loginStartRequest) {

        // Find the user in the user database
        UserAccount user =
                this.userService
                        .findByEmail(loginStartRequest.getEmail())
                        .orElseThrow(() -> new RuntimeException("Userid does not exist"));

        // make the assertion request to send to the client
        StartAssertionOptions options =
                StartAssertionOptions.builder()
                        .timeout(60_000)
                        .username(loginStartRequest.getEmail())
                        //     .userHandle(YubicoUtils.toByteArray(user.id()))
                        .build();
        AssertionRequest assertionRequest = this.relyingParty.startAssertion(options);

        LoginStartResponse loginStartResponse = new LoginStartResponse();
        loginStartResponse.setFlowId(UUID.randomUUID());
        loginStartResponse.setAssertionRequest(assertionRequest);


        LoginFlowEntity loginFlowEntity = new LoginFlowEntity();
        loginFlowEntity.setId(loginStartResponse.getFlowId());
        loginFlowEntity.setStartRequest(toJson(loginStartRequest));
        loginFlowEntity.setStartResponse(toJson(loginStartResponse));
        try {
            loginFlowEntity.setAssertionRequest(assertionRequest.toJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.loginFlowRepository.save(loginFlowEntity);

        return loginStartResponse;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AssertionResult finishLogin(LoginFinishRequest loginFinishRequest)
            throws AssertionFailedException {

        var loginFlowEntity =
                this.loginFlowRepository
                        .findById(loginFinishRequest.getFlowId())
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "flow id " + loginFinishRequest.getFlowId() + " not found"));

        var assertionRequestJson = loginFlowEntity.getAssertionRequest();
        AssertionRequest assertionRequest = null;
        try {
            assertionRequest = AssertionRequest.fromJson(assertionRequestJson);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cloud not deserialize the assertion Request");
        }

        FinishAssertionOptions options =
                FinishAssertionOptions.builder()
                        .request(assertionRequest)
                        .response(loginFinishRequest.getCredential())
                        .build();

        return this.relyingParty.finishAssertion(options);

    }
}
