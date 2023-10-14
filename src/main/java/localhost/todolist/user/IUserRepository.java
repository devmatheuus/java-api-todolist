package localhost.todolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.annotation.Nullable;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    @Nullable
    UserModel findByUsername(String username);
}
