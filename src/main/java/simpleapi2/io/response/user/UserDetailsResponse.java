package simpleapi2.io.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
    private String userId;
    private String username;
    private String email;
    private String address;
    private int loyalty;
}
