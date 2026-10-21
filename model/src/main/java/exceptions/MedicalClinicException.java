package exceptions;

import lombok.Getter;

@Getter
public abstract class MedicalClinicException extends RuntimeException {
    private final int status;

    protected MedicalClinicException(String message, int status) {
        super(message);
        this.status = status;
    }

}
