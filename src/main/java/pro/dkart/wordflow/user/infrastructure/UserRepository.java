package pro.dkart.wordflow.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.dkart.wordflow.user.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {}