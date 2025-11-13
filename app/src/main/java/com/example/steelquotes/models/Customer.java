package com.example.steelquotes.models;

public class Customer {
    private Integer id;
    private String name;
    private String email;
    private String telephone;
    private String mobile;
    private String fax;
    private String address;

    public Customer(){

    }

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){ this.name = name; }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){ this.email = email; }

    public String getTelephone(){
        return telephone;
    }
    public void setTelephone(String telephone){ this.telephone = telephone; }

    public String getMobile(){ return mobile; }
    public void setMobile(String mobile){ this.mobile = mobile; }

    public String getFax(){ return fax; }
    public void setFax(String fax){ this.fax = fax; }

    public String getAddress(){ return address; }
    public void setAddress(String address){ this.address = address; }

}