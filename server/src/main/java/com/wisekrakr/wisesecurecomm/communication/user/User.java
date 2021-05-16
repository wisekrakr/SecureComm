package com.wisekrakr.wisesecurecomm.communication.user;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 832104440266470228L;


    private long id;
    private String name;
    private Status status;
    private String profilePicture;

    public User(long id, String name, Status status, String profilePicture) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.profilePicture = profilePicture;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}
