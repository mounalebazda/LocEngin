package com.locengin.model;

public class ItemCategory {
    private int idCat;
    private String designation;
    private String marque;

    public ItemCategory(int idCat, String designation, String marque) {
        this.idCat = idCat;
        this.designation = designation;
        this.marque = marque;
    }

    public ItemCategory(int idCat) {
        this.idCat = idCat;
        this.designation = null;  
        this.marque = null;       
    }

    public int getIdCat() {
        return idCat;
    }

    public String getDesignation() {
        return designation;
    }

    public String getMarque() {
        return marque;
    }
}
