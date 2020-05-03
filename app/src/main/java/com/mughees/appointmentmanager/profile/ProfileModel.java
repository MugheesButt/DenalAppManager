package com.mughees.appointmentmanager.profile;

public class ProfileModel {

    String id, name, phone, email, image;

    public ProfileModel() {
        this.id = "N/A";
        this.name = "N/A";
        this.phone = "N/A";
        this.email = "N/A";
        this.image = "N/A";
    }

    public ProfileModel(String id ,String name, String phone, String email, String image) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
