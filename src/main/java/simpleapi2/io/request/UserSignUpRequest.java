package simpleapi2.io.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class UserSignUpRequest {

    @NotNull(message = "Username is required")
    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username can only have maximum 50 characters")
    @ApiModelProperty(
            example="william9x",
            notes="Username cannot be empty",
            required=true
    )
    private String username;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email can only have maximum 100 characters")
    @Email(message = "Please provide a valid email")
    @ApiModelProperty(
            example="nam@gmail.com",
            notes="Email cannot be empty",
            required=true
    )
    private String email;

    @NotNull(message = "Address is required")
    @NotBlank(message = "Address is required")
    @Size(max = 100, message = "Address can only have maximum 100 characters")
    @ApiModelProperty(
            example="Hanoi",
            notes="Address cannot be empty",
            required=true
    )
    private String address;
}
