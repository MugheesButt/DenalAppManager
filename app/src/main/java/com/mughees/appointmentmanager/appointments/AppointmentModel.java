package com.mughees.appointmentmanager.appointments;

public class AppointmentModel {

    int day , month , year , hrs , mins ;
    String tooth , treatment , description , App_Id , Pat_Id , admin ;

    public AppointmentModel() {

        this.day = day;
        this.month = month;
        this.year = year;
        this.hrs = hrs;
        this.mins = mins;
        this.tooth = "N/A";
        this.treatment = "N/A";
        this.description = "N/A";
        this.App_Id = "N/A";
        this.Pat_Id = "N/A";
        this.admin = "N/A" ;
    }

    public AppointmentModel(int day, int month, int year, int hrs, int mins, String tooth,
                            String treatment, String description, String app_Id, String pat_Id, String admin) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hrs = hrs;
        this.mins = mins;
        this.tooth = tooth;
        this.treatment = treatment;
        this.description = description;
        this.App_Id = app_Id;
        this.Pat_Id = pat_Id;
        this.admin = admin ;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHrs() {
        return hrs;
    }

    public void setHrs(int hrs) {
        this.hrs = hrs;
    }

    public int getMins() {
        return mins;
    }

    public void setMins(int mins) {
        this.mins = mins;
    }

    public String getTooth() {
        return tooth;
    }

    public void setTooth(String tooth) {
        this.tooth = tooth;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApp_Id() {
        return App_Id;
    }

    public void setApp_Id(String app_Id) {
        App_Id = app_Id;
    }

    public String getPat_Id() {
        return Pat_Id;
    }

    public void setPat_Id(String pat_Id) {
        Pat_Id = pat_Id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
