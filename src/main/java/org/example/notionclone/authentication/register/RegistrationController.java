package org.example.notionclone.authentication.register;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.exception.RegistrationFailedException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.notionclone.exceptions.DuplicateEntityException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class RegistrationController {
    private final String START_REG_REQUEST = "start_reg_request";
    private final RegistrationService registrationService;



    @PostMapping("/notionclone/webauth/register/start")
    public ResponseEntity<Map<String, Object>> startRegistration(
            @Valid @RequestBody RegistrationStartRequest formData,
            BindingResult bindingResult, HttpSession session) {

        Map<String, String> errors = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(Map.of("errors",errors));
        }

        try {
            PublicKeyCredentialCreationOptions options = registrationService.startRegistration(formData);
            session.setAttribute(START_REG_REQUEST, options);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());
            return ResponseEntity.ok().body(objectMapper.convertValue(options, new TypeReference<>() {}));
        } catch (DuplicateEntityException e) {
            return ResponseEntity.badRequest().body(Map.of("email", e.toString()));
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error",e.toString()));
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.toString()));

        }
    }

    @PostMapping("/notionclone/webauth/register/finish")
    public void finishRegistration(
            @RequestBody RegistrationFinishRequest request, HttpSession session) throws
            RegistrationFailedException{
        PublicKeyCredentialCreationOptions options =  (PublicKeyCredentialCreationOptions)session.getAttribute(START_REG_REQUEST);
        if (options == null) {
            throw new RuntimeException("Could not find the original request");
        }
        registrationService.finishRegistration(
                request, options);
    }


}
