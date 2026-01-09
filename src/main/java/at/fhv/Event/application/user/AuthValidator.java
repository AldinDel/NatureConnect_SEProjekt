package at.fhv.Event.application.user;

import at.fhv.Event.application.request.user.RegisterUserRequest;
import at.fhv.Event.domain.model.exception.ValidationError;
import at.fhv.Event.domain.model.exception.ValidationErrorFactory;
import at.fhv.Event.domain.model.exception.ValidationErrorType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthValidator {

    public List<ValidationError> validateRegister(RegisterUserRequest request) {
        List<ValidationError> errors = new ArrayList<>();

        if (request.getPassword() != null && request.getConfirmPassword() != null) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                errors.add(ValidationErrorFactory.create(
                        ValidationErrorType.FIELD_MISMATCH,
                        "confirmPassword",
                        "Passwords do not match"
                ));
            }
        }

        return errors;
    }
}