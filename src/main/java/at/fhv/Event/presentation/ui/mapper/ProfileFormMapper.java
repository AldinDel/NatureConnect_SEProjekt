package at.fhv.Event.presentation.ui.mapper;

import at.fhv.Event.domain.model.user.CustomerProfile;
import at.fhv.Event.presentation.ui.dto.ProfileForm;

public class ProfileFormMapper {

    public static ProfileForm toForm(CustomerProfile profile) {
        ProfileForm form = new ProfileForm();
        form.setFirstName(profile.getFirstName());
        form.setLastName(profile.getLastName());
        form.setPhone(profile.getPhone());
        form.setBirthday(profile.getBirthday());
        form.setStreet(profile.getStreet());
        form.setPostalCode(profile.getPostalCode());
        form.setCity(profile.getCity());
        form.setCountry(profile.getCountry());
        form.setAvatarUrl(profile.getAvatarUrl());
        return form;
    }

    public static void applyToDomain(ProfileForm form, CustomerProfile profile) {
        profile.setFirstName(form.getFirstName());
        profile.setLastName(form.getLastName());
        profile.setPhone(form.getPhone());
        profile.setBirthday(form.getBirthday());
        profile.setStreet(form.getStreet());
        profile.setPostalCode(form.getPostalCode());
        profile.setCity(form.getCity());
        profile.setCountry(form.getCountry());
    }
}
