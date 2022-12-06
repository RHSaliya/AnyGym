package com.project.anygymowner.DataHolder;

public class GymLocationDataHolder {
    private double latitude, longitude;
    private String gymName;

    public GymLocationDataHolder() {
    }

    public GymLocationDataHolder(double latitude, double longitude, String gymName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.gymName = gymName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getGymName() {
        return gymName;
    }
}
