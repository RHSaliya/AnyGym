package com.project.anygymowner.DataHolder;

import java.util.HashMap;

public class OwnerDataHolder {
    private String name;
    private String gender;
    private String age;
    private String email;
    private HashMap<String, String> ownerGyms;
    private String mobile;
    private String upiID;
    private long credit;
    private boolean profile, blocked;

    public OwnerDataHolder() {
    }

    public OwnerDataHolder(String name, String gender, String age, String email, String mobile, String upiID, int credit, boolean profile) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.mobile = mobile;
        this.upiID = upiID;
        this.credit = credit;
        this.profile = profile;
        blocked = false;
    }

    public HashMap<String, String> getOwnerGyms() {
        if (ownerGyms == null) {
            ownerGyms = new HashMap<>();
        }
        return ownerGyms;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }


    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public long getCredit() {
        return credit;
    }

    public String getUpiID() {
        return upiID;
    }

    public boolean isProfile() {
        return profile;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void addGym(String gymName) {
        if (ownerGyms == null) {
            ownerGyms = new HashMap<>();
        }
        ownerGyms.put(gymName, "N");
    }
}
