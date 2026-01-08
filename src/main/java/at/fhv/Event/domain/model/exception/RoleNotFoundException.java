package at.fhv.Event.domain.model.exception;

public class RoleNotFoundException extends DomainException {
    private final String roleCode;

    public RoleNotFoundException(String roleCode) {
        super("USER_004");
        this.roleCode = roleCode;
    }

    public String getRoleCode() {
        return roleCode;
    }
}
