package org.example.notionclone.authentication.login;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.exception.AssertionFailedException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class LoginController {

    private static final String START_REG_REQUEST = "start_login_request";

    private final LoginService loginService;

    @GetMapping("/webauth/login")
    String get() {
        return "login";
    }


    @ResponseBody
    @PostMapping("/notionclone/webauth/login/start")
    public ResponseEntity<Map<String, Object>> startLogin(@Valid @RequestBody LoginStartRequest loginStartRequest,
                                                          BindingResult bindingResult
            , HttpSession session) {

        Map<String, String> errors = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        LoginStartResponse response = this.loginService.startLogin(loginStartRequest);
        session.setAttribute(START_REG_REQUEST, response.getAssertionRequest());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        return ResponseEntity.ok().body(objectMapper.convertValue(response, new TypeReference<>() {
        }));

    }

    @ResponseBody
    @PostMapping("/notionclone/webauth/login/finish")
    AssertionResult finishLogin(@RequestBody LoginFinishRequest request, HttpSession session)
            throws AssertionFailedException {
        var assertionRequest = (AssertionRequest) session.getAttribute(START_REG_REQUEST);
        if (assertionRequest == null) {
            throw new RuntimeException("Could not find the original request");
        }

        var result = this.loginService.finishLogin(request);
        if (result.isSuccess()) {
            session.setAttribute(AssertionRequest.class.getName(), result);
        }
        return result;
    }
}
