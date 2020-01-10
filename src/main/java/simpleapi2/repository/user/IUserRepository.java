package simpleapi2.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import simpleapi2.entity.user.UserEntity;

import java.util.ArrayList;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
    UserEntity findByUserId(String userId);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    ArrayList<UserEntity> findAllByUsernameNotNull();
}
