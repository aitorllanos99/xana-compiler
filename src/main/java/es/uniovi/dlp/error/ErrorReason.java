package es.uniovi.dlp.error;

public enum ErrorReason {
    LVALUE_REQUIRED("Lvalue required"),
    FIELD_ALREADY_DECLARED("Field already declared in struct"),
    ;

    private final String message;

    ErrorReason(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}