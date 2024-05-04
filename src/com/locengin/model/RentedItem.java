package com.locengin.model;

import java.util.Date;

public class RentedItem {
    private Date datesortie;
    private Date dueDate;
    private double montant;
    private Equipment item;
    private Client client;

    public RentedItem(Date datesortie,Date dueDate, Equipment item, Client client, double montant) {
        this.datesortie = datesortie;
        this.dueDate = dueDate;  
        this.item = item;
        this.client = client;
        this.montant = montant;
    }


    // Getters
    public Date getDatesortie() {
        return datesortie;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Equipment getItem() {
        return item;
    }

    public Client getClient() {
        return client;
    }

    public double getMontant() {
        return montant;
    }

    // Setters
    public void setDatesortie(Date datesortie) {
        this.datesortie = datesortie;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
}
