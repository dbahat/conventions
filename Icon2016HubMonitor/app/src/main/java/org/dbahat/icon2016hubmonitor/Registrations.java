package org.dbahat.icon2016hubmonitor;

public class Registrations {
    private int android;
    private int iOS;
    private int total;

    public static Registrations parse(String registrationsJson) {
        Registrations registrations = new Registrations();
        registrations.iOS = registrationsJson.split("<AppleRegistrationDescription").length - 1;
        registrations.android = registrationsJson.split("<GcmRegistrationDescription").length - 1;

        // Calculating the total in separate in case we somehow get a registration from a different platform
        registrations.total = registrationsJson.split("<entry").length - 1;
        return registrations;
    }

    public String toString() {
        return "Total: " + this.total + ", iOS: " + this.iOS + ", Android: " + this.android;
    }

    public int getTotal() {
        return total;
    }

    public int getAndroid() {
        return android;
    }

    public int getiOS() {
        return iOS;
    }

    public void addToTotal(int amountToAdd) {
        total += amountToAdd;
    }

    public void addToiOS(int amountToAdd) {
        iOS += amountToAdd;
    }

    public void addToAndroid(int amountToAdd) {
        android += amountToAdd;
    }
}
