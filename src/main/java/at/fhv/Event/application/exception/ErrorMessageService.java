package at.fhv.Event.application.exception;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ErrorMessageService {
    private final MessageSource messageSource;

    public ErrorMessageService(@Qualifier("errorMessageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String errorCode, Object... params) {
        return messageSource.getMessage(
                errorCode,
                params,
                "Error:" + errorCode,
                Locale.ENGLISH
        );
    }

    public String getMessage(String errorCode) {
        return getMessage(errorCode, (Object[]) null);
    }
}
