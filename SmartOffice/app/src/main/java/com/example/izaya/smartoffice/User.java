package com.example.izaya.smartoffice;

/**
 * Created by Izaya on 11/11/2016.
 */

public class User {
    private String email, name;

    private int accountId;

    public User(String userName, String emailAddress, int id) {
        this.name = userName;
        this.email = emailAddress;
        this.accountId = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
