package com.project.anygymadmin.DataHolder;


import java.util.HashMap;

public class UserDataHolder {
    private String name;
    private String gender;
    private int age;
    private String email;
    private String mobile;
    private long credit;
    private double height;
    private double weight;

    private boolean isProfile;
    private String[] favourites;
    private String[] blocked;
    private HashMap<String,Integer> memberships = new HashMap<>();

    public UserDataHolder() {
    }

    public UserDataHolder(String name, String gender, int age, String email, String mobile, long credit, double height, double weight, boolean isProfile) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.mobile = mobile;
        this.credit = credit;
        this.height = height;
        this.weight = weight;
        this.isProfile = isProfile;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public boolean getIsProfile() {
        return isProfile;
    }

    public long getCredit() {
        return credit;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public int getMembership(String name) {
        if (memberships.containsKey(name))
        {
            return memberships.get(name);
        }
        else
        {
            return 0;
        }
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public void setMembership(String gymName,int days) {
        memberships.put(gymName,days);
    }
}
