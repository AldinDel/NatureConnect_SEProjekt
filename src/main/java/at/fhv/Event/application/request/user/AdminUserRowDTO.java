package at.fhv.Event.application.request.user;

public record AdminUserRowDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        boolean active,
        String roles
) {
}
