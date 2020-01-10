package simpleapi2.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter

public class UserDTO implements Serializable {
    private static final long serialVersionUID = -390599312533832918L;
    private long id;
    private String userId;
    private String username;
    private String email;
    private String address;
    private Integer loyalty;
}
