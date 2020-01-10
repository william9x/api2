package simpleapi2.io.response;

import lombok.Getter;

@Getter
public enum SuccessResponse {
    FOUND_RECORD("Found record with provided information"),
    CREATED_RECORD("Create record successfully"),
    UPDATED_RECORD("Update record successfully"),
    DELETED_RECORD("Delete record successfully");

    private String successResponse;
    SuccessResponse(String successResponse){
        this.successResponse = successResponse;
    }
}
