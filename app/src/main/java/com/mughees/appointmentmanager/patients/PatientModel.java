package com.mughees.appointmentmanager.patients;

public class PatientModel {
    String _ID ;
    String Name ;
    int Age ;
    String Phone ;
    String Email ;
    String Address ;
    int TDues ;
    int PDues ;
    int RDues ;
    String Img;
    String admin;



    public PatientModel (String id , String Name , int Age , String Phone , String Email , String Address , int TDues , int PDues , int RDues, String Img, String admin)
    {
        this._ID = id ;
        this.Name = Name ;
        this.Age = Age ;
        this.Phone = Phone ;
        this.Email = Email ;
        this.Address = Address ;
        this.TDues = TDues ;
        this.PDues = PDues ;
        this.RDues = TDues - PDues ;
        this.Img = Img;
        this.admin = admin;
    }

    public PatientModel() {


        this._ID = "N/A" ;
        this.Name = "N/A" ;
        this.Age = 0 ;
        this.Phone = "N/A" ;
        this.Email = "N/A" ;
        this.Address = "N/A" ;
        this.TDues = 0 ;
        this.PDues = 0 ;
        this.RDues = 0 ;
        this.Img = "default";
        this.admin = "N/A";

    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getTDues() {
        return TDues;
    }

    public void setTDues(int TDues) {
        this.TDues = TDues;
    }

    public int getPDues() {
        return PDues;
    }

    public void setPDues(int PDues) {
        this.PDues = PDues;
    }

    public int getRDues() {
        return RDues;
    }

    public void setRDues(int RDues) {
        this.RDues = RDues;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
