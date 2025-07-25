package pro.dkart.wordflow.user.application;

import org.springframework.stereotype.Service;
import pro.dkart.wordflow.user.domain.model.User;
import pro.dkart.wordflow.user.infrastructure.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Optional<User> findById(Long id) {
        return repo.findById(id);
    }
}
