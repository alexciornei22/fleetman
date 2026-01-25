package ro.upb.acs.fleetman.exception;

public class FieldConflictException extends RuntimeException {

    private final String errorField;

    public FieldConflictException(String code, String message) {
        super(message);
        this.errorField = code;
    }

    public String getErrorField() {
        return errorField;
    }
}
