package at.fhv.Event.domain.model.user;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomerProfile {
    private Long _id;
    private UserAccount _user;
    private String _firstName;
    private String _lastName;
    private String _email;
    private String _phone;
    private LocalDate _birthday;
    private OffsetDateTime _createdAt;
    private OffsetDateTime _updatedAt;

    public CustomerProfile(Long id, UserAccount user, String firstName,
                           String lastName, String email, String phone,
                           LocalDate birthday, OffsetDateTime createdAt,
                           OffsetDateTime updatedAt) {

        _id = id;
        _user = user;
        _firstName = firstName;
        _lastName = lastName;
        _email = email;
        _phone = phone;
        _birthday = birthday;
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

    public LocalDate get_birthday() {
        return _birthday;
    }

    public void set_birthday(LocalDate _birthday) {
        this._birthday = _birthday;
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
