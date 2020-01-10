package simpleapi2.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@Entity(name="users")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 8017631105467339170L;

    @Id
    @GeneratedValue
    private long Id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String address;

}
