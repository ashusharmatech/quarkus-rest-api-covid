package com.rest.models;

public class CountryStats implements Comparable<CountryStats> {

    private String country;
    private int latestConfirmedCases;
    private int diffFromPrevDayConfirmedCases;

    private int latestDeathCases;
    private int diffFromPrevDayDeathCases;

    private int latestRecoveredCases;
    private int diffFromPrevDayRecoveredCases;

    private double recoveryPercentage;

    private double deathPercentage;

    public int getDiffFromPrevDayConfirmedCases() {
        return diffFromPrevDayConfirmedCases;
    }

    public void setDiffFromPrevDayConfirmedCases(int diffFromPrevDayConfirmedCases) {
        this.diffFromPrevDayConfirmedCases = diffFromPrevDayConfirmedCases;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLatestConfirmedCases() {
        return latestConfirmedCases;
    }

    public void setLatestConfirmedCases(int latestConfirmedCases) {
        this.latestConfirmedCases = latestConfirmedCases;
    }

    public int getLatestDeathCases() {
        return latestDeathCases;
    }

    public void setLatestDeathCases(int latestDeathCases) {
        this.latestDeathCases = latestDeathCases;
    }

    public int getDiffFromPrevDayDeathCases() {
        return diffFromPrevDayDeathCases;
    }

    public void setDiffFromPrevDayDeathCases(int diffFromPrevDayDeathCases) {
        this.diffFromPrevDayDeathCases = diffFromPrevDayDeathCases;
    }

    public int getLatestRecoveredCases() {
        return latestRecoveredCases;
    }

    public void setLatestRecoveredCases(int latestRecoveredCases) {
        this.latestRecoveredCases = latestRecoveredCases;
    }

    public int getDiffFromPrevDayRecoveredCases() {
        return diffFromPrevDayRecoveredCases;
    }

    public void setDiffFromPrevDayRecoveredCases(int diffFromPrevDayRecoveredCases) {
        this.diffFromPrevDayRecoveredCases = diffFromPrevDayRecoveredCases;
    }

    public double getRecoveryPercentage() {
        return recoveryPercentage;
    }

    public void setRecoveryPercentage(double recoveryPercentage) {
        this.recoveryPercentage = recoveryPercentage;
    }

    public double getDeathPercentage() {
        return deathPercentage;
    }

    public void setDeathPercentage(double deathPercentage) {
        this.deathPercentage = deathPercentage;
    }

    @Override
    public int compareTo(CountryStats o) {
        return this.getCountry().compareTo(o.getCountry());
    }
}
