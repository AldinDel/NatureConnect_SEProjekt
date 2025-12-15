package at.fhv.Event.application.request.user;

public record AdminUserEditRequest(
        String firstName,
        String lastName,
        String email,
        String role,
        boolean active,
        String password
) {
}
