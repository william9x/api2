package simpleapi2.io.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserUpdateRequest {

    @Size(max = 100, message = "Email can only have maximum 100 characters")
    @Email(message = "Please provide a valid email")
    @ApiModelProperty(
            example = "nam@gmail.com",
            notes = "If empty, email will not be updated",
            required = false
    )
    private String email;

    @Size(max = 100, message = "Address can only have maximum 100 characters")
    @ApiModelProperty(
            example = "Hanoi",
            notes = "If empty, address will not be updated",
            required = false
    )
    private String address;
}
