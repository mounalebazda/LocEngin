package com.locengin.model;

public class Equipment {
    private int codeEquip;
    private String etat; 
    private ItemCategory category;  // Reference to the category of the item

    public Equipment(int codeEquip, String etat, ItemCategory category) {
        this.codeEquip = codeEquip;
        this.etat = etat;
        this.category = category;
    }

    public int getCodeEquip() {
        return codeEquip;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }
}
