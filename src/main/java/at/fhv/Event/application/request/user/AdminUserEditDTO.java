package at.fhv.Event.application.request.user;

public record AdminUserEditDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        boolean active,
        String role
) {
}
