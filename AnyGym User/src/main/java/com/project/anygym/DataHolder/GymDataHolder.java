package com.project.anygym.DataHolder;

public class GymDataHolder {
    private String title, relativeName, address, owner_name;
    private double rating, credits;
    private String mobile, status, timings;
    private int charge;
    private boolean logo;

    public GymDataHolder() {
    }

    public GymDataHolder(String title, String relativeName, String address, String owner_name, double rating, String mobile, String status, String timings, int charge, boolean logo) {
        this.title = title;
        this.relativeName = relativeName;
        this.address = address;
        this.owner_name = owner_name;
        this.rating = rating;
        this.mobile = mobile;
        this.status = status;
        this.timings = timings;
        this.charge = charge;
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public String getRelativeName() {
        return relativeName;
    }

    public String getAddress() {
        return address;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public double getRating() {
        return rating;
    }

    public String getMobile() {
        return mobile;
    }

    public String getStatus() {
        return status;
    }

    public String getTimings() {
        return timings;
    }

    public int getCharge() {
        return charge;
    }

    public boolean isLogo() {
        return logo;
    }

    public double getCredits() {
        return credits;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public void setLogo(boolean logo) {
        this.logo = logo;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }
}
