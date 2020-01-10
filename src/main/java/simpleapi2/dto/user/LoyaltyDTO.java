package simpleapi2.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoyaltyDTO implements Serializable {
    private static final long serialVersionUID = 2203473173033243985L;
    private int point;
}
