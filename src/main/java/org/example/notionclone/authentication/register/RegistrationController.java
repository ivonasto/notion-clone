package org.example.notionclone.authentication.register;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.notionclone.exceptions.DuplicateEntityException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Controller
public class RegistrationController {
    RegistrationService registrationService;



    @PostMapping("notionclone/webauth/registration/start")
    public ResponseEntity<Map<String, String>> startRegistration(
            @Valid RegistrationStartRequest formData,
            BindingResult bindingResult, HttpSession session) {

        Map<String, String> errors = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            PublicKeyCredentialCreationOptions options = registrationService.startRegistration(formData);
            ObjectMapper objectMapper = new ObjectMapper();
            return ResponseEntity.ok().body(objectMapper.convertValue(options, new TypeReference<>() {}));
        } catch (DuplicateEntityException e) {
            return ResponseEntity.badRequest().body(Map.of("email", e.toString()));
        }
    }



}
