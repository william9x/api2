package simpleapi2.io.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OperationStatus {
    private int status;
    private String message;
    private Object data;

}
