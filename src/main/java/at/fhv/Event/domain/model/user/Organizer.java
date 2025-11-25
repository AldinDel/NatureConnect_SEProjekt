package at.fhv.Event.domain.model.user;

import java.time.OffsetDateTime;

public class Organizer {
    private Long _id;
    private UserAccount _user;
    private String _name;
    private String _email;
    private String _phone;
    private Boolean _isActive = true;
    private OffsetDateTime _createdAt;
    private OffsetDateTime _updatedAt;

    public Organizer(Long id, UserAccount user, String name,
                     String email, String phone, Boolean isActive,
                     OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        _id = id;
        _user = user;
        _name = name;
        _email = email;
        _phone = phone;
        _isActive = isActive;
        _createdAt = createdAt;
        _updatedAt = updatedAt;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public UserAccount get_user() {
        return _user;
    }

    public void set_user(UserAccount _user) {
        this._user = _user;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public Boolean get_isActive() {
        return _isActive;
    }

    public void set_isActive(Boolean _isActive) {
        this._isActive = _isActive;
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
}
