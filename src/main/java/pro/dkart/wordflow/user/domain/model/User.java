package pro.dkart.wordflow.user.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import pro.dkart.wordflow.kernel.UserRole;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;
}