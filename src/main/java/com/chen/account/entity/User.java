package com.chen.account.entity;


public class User {
    private Integer id;

    private String phone;

    private String password;

    private String randomstr;

    private Long createat;

    private Long updateat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRandomstr() {
        return randomstr;
    }

    public void setRandomstr(String randomstr) {
        this.randomstr = randomstr;
    }

    public Long getCreateat() {
        return createat;
    }

    public void setCreateat(Long createat) {
        this.createat = createat;
    }

    public Long getUpdateat() {
        return updateat;
    }

    public void setUpdateat(Long updateat) {
        this.updateat = updateat;
    }
}