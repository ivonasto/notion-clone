package org.example.notionclone.authentication.register;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;


@AllArgsConstructor
@Getter
@Setter
public class RegistrationStartRequest {
    @NotEmpty
    @Size(min = 3, max = 33, message = "Username must be between 3 and 33 symbols")
    private String username;
    @NotEmpty(message = "Email can't be empty!")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "Please provide a valid email address. The email format should be like user@example.com"
    )
    private String email;
}
