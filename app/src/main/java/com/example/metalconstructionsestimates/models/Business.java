package com.example.metalconstructionsestimates.models;

public class Business {
    private String name;
    private String email;
    private String telephone;
    private String mobile;
    private String fax;
    private String address;

    // ✅ Default constructor
    public Business() {
    }

    // ✅ Optional constructor with parameters (if you want)
    public Business(String name, String email, String phone, String mobile, String fax, String address) {
        this.name = name;
        this.email = email;
        this.telephone = phone;
        this.mobile = mobile;
        this.fax = fax;
        this.address = address;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return telephone; }
    public void setPhone(String phone) { this.telephone = phone; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}