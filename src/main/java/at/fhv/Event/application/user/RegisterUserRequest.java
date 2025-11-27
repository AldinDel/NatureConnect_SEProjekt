package at.fhv.Event.application.user;

public record RegisterUserRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        Boolean termsAccepted) {
}
