package at.fhv.Event.domain.model.user;

import java.time.OffsetDateTime;
import java.util.Set;

public class UserAccount {
    private Long _id;
    private String _email;
    private String _passwordHash;
    private String _firstName;
    private String _lastName;
    private Boolean _IsActive = true;
    private OffsetDateTime _createdAt;
    private OffsetDateTime _updatedAt;
    private Set<Role> _roles;

    public UserAccount(Long id, String email, String passwordHash,
                       String firstName, String lastName,
                       Boolean IsActive, OffsetDateTime createdAt,
                       OffsetDateTime updatedAt) {
        _id = id;
        _email = email;
        _passwordHash = passwordHash;
        _firstName = firstName;
        _lastName = lastName;
        _IsActive = IsActive;
        _createdAt = createdAt;
        _updatedAt = updatedAt;

    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_passwordHash() {
        return _passwordHash;
    }

    public void set_passwordHash(String _passwordHash) {
        this._passwordHash = _passwordHash;
    }

    public String get_firstName() {
        return _firstName;
    }

    public void set_firstName(String _firstName) {
        this._firstName = _firstName;
    }

    public String get_lastName() {
        return _lastName;
    }

    public void set_lastName(String _lastName) {
        this._lastName = _lastName;
    }

    public Boolean get_IsActive() {
        return _IsActive;
    }

    public void set_IsActive(Boolean _IsActive) {
        this._IsActive = _IsActive;
    }

    public OffsetDateTime get_createdAt() {
        return _createdAt;
    }

    public void set_createdAt(OffsetDateTime _createdAt) {
        this._createdAt = _createdAt;
    }

    public OffsetDateTime get_updatedAt() {
        return _updatedAt;
    }

    public void set_updatedAt(OffsetDateTime _updatedAt) {
        this._updatedAt = _updatedAt;
    }

    public Set<Role> get_roles() {
        return _roles;
    }

    public void set_roles(Set<Role> _roles) {
        this._roles = _roles;
    }
}