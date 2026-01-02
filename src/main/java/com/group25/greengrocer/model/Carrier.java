package com.group25.greengrocer.model;

public class Carrier extends User {
    // Carrier specific fields if any, otherwise just role customization
    private byte[] licenseFront;
    private byte[] licenseBack;

    public Carrier(int id, String username, String password) {
        super(id, username, password, "carrier");
    }

    public void setLicenseFront(byte[] licenseFront) {
        this.licenseFront = licenseFront;
    }

    public byte[] getLicenseFront() {
        return licenseFront;
    }

    public void setLicenseBack(byte[] licenseBack) {
        this.licenseBack = licenseBack;
    }

    public byte[] getLicenseBack() {
        return licenseBack;
    }
}
