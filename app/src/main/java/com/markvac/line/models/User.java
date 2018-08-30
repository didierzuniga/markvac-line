package com.markvac.line.models;

/**
 * Created by unicorn on 8/28/2018.
 */

public class User {
    public boolean active;
    public String company;
    public String email;
    public String position;

    public User() {
    }

    public boolean isActive() {
        return active;
    }

    public String getCompany() {
        return company;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }
}
