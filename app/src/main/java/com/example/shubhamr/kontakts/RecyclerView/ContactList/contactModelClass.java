package com.example.shubhamr.kontakts.RecyclerView.ContactList;

import android.net.Uri;

public class contactModelClass {

    private String contactName;
    private String contactNumber;
    private Uri contactImage;
    private String ID;

    public contactModelClass(){}

    public contactModelClass(String ID,String contactName,String contactNumber,Uri contactImage){
        this.ID=ID;
        this.contactImage=contactImage;
        this.contactName=contactName;
        this.contactNumber=contactNumber;
    }

    public String getID() {
        return ID;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public Uri getContactImage() {
        return contactImage;
    }

    public void setContactImage(Uri contactImage) {
        this.contactImage = contactImage;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
