package com.locengin.model;

public class StockItem {
    private ItemCategory category;
    private int quantite;

    public StockItem(ItemCategory category, int quantite) {
        this.category = category;
        this.quantite = quantite;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

}