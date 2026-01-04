package at.fhv.Event.domain.model.user;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomerProfile {
    private Long id;
    private UserAccount user;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthday;
    private String street;
    private String postalCode;
    private String city;
    private String country;
    private String avatarUrl;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public CustomerProfile() {}

    public CustomerProfile(Long id, UserAccount user, String firstName, String lastName, String email, String phone,
                           LocalDate birthday, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public CustomerProfile(Long id, UserAccount user, String firstName, String lastName, String email, String phone,
                           LocalDate birthday, String street, String postalCode, String city, String country,
                           String avatarUrl, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
