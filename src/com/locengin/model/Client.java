package com.locengin.model;

public class Client {
    private int Idloc;
    private String nom;
    private String addresse;
    private String profession;
    private String telephone;
    private double solde;

    // Constructor to initialize all fields
    public Client(int Idloc, String nom, String addresse, String profession, String telephone, double solde) {
        this.Idloc = Idloc;
        this.nom = nom;
        this.addresse = addresse;
        this.profession = profession;
        this.telephone = telephone;
        this.solde = solde;
    }

    // Getters
    public int getIdloc() {
        return Idloc;
    }

    public String getnom() {
        return nom;
    }

    public String getAddresse() {
        return addresse;
    }

    public String getProfession() {
        return profession;
    }

    public String getTelephone() {
        return telephone;
    }

    public double getSolde() {
        return solde;
    }

    // Setters
    public void setSolde(double solde) {
        this.solde = solde;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public void setnom(String nom) {
        this.nom = nom;
    }

}
