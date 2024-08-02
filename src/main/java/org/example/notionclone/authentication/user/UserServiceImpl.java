package org.example.notionclone.authentication.user;

import lombok.AllArgsConstructor;
import org.example.notionclone.exceptions.DuplicateEntityException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserAccount createUser(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username can't be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email can't be blank");
        }

        findByEmail(email).ifPresent(user -> {
            throw new DuplicateEntityException("A profile", "this", "email");
        });

        UserAccountEntity user = new UserAccountEntity();
        user.setEmail(email);
        user.setUsername(username);
        user = userRepository.save(user);

        return new UserAccount(user.getId(),
                user.getUsername(),
                user.getEmail(),
                Set.of());

    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserServiceImpl::toUserAccount);

    }

    private static UserAccount toUserAccount(UserAccountEntity accountEntity) {

        Set<FidoCredential> credentials =
                accountEntity.getCredentials().stream()
                        .map(
                                c ->
                                        new FidoCredential(
                                                c.getId(), c.getType(), accountEntity.getId(), c.getPublicKeyCose()))
                        .collect(Collectors.toSet());

        return new UserAccount(
                accountEntity.getId(), accountEntity.getUsername(), accountEntity.getEmail(), credentials);
    }
}
