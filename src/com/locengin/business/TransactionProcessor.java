package com.locengin.business;

import com.locengin.model.Client;
import com.locengin.model.Equipment;
import com.locengin.model.ItemCategory;
import com.locengin.model.RentedItem;
import com.locengin.model.StockItem;
import com.locengin.utils.FileIOUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionProcessor {

    private Set<Equipment> items = new HashSet<>();
    private Set<StockItem> stockItems = new HashSet<>();
    private Set<Client> clients = new HashSet<>();
    private Set<RentedItem> rentedItems = new HashSet<>();
    private Set<ItemCategory> itemCategories = new HashSet<>();

    public TransactionProcessor() {
        this.clients = FileIOUtils.loadClients();  // Correctly loading clients at initialization
    }

    //-----------------------------------------------------------------------------
    public boolean checkOutItem(Equipment item, Client client, Date rentdate) {
        if (item != null && item.getEtat().equals("disponible")) {
            item.setEtat("rented");
            Date dueDate = FileIOUtils.calculateDueDate(rentdate);
            RentedItem rentedItem = new RentedItem(rentdate, dueDate, item, client, 0); 
            updateStock(item.getCategory().getIdCat(), -1); 
            rentedItems.add(rentedItem);
            FileIOUtils.saveRentalOnAdd(rentedItem);
            updateAndSaveEquipment(item);
            return true;
        }
        return false;
    }
    //-----------------------------------------------------------------------------
    private void updateAndSaveEquipment(Equipment item) {
        // You might need to reload all items and update the particular item in case the in-memory list is outdated
        Set<Equipment> allItems = FileIOUtils.loadEquipmentItems();
        allItems.removeIf(i -> i.getCodeEquip() == item.getCodeEquip());  // Remove the old entry
        allItems.add(item);  // Add the updated item
    
        FileIOUtils.saveEquipmentItems(allItems);  // Save the updated list back to the file
    }
    
    //-----------------------------------------------------------------------------
    public boolean checkInItem(RentedItem rentedItem) {
        if (rentedItem != null) {
            Equipment item = rentedItem.getItem();
            item.setEtat("disponible");
            updateAndSaveEquipment(item);
            rentedItems.remove(rentedItem);
            updateStock(rentedItem.getItem().getCategory().getIdCat(), 1);
            FileIOUtils.saveRentalOnRemove(rentedItem);
            return true;
        }
        return false;
    }
    //-----------------------------------------------------------------------------
    public boolean addIitem(Equipment item) {

        Set<Equipment> items = FileIOUtils.loadEquipmentItems();
        // Check if equipment ID already exists
        for (Equipment existingItem : items) {
            if (existingItem.getCodeEquip() == item.getCodeEquip()) {
                System.out.println("Equipment ID already exists. Failed to add new equipment.");
                return false;
            }
        }
        
        // If ID is unique, add the item
        boolean added = items.add(item);
        if (added) {
            updateStock(item.getCategory().getIdCat(), 1);
            FileIOUtils.saveEquipmentItem(item);
        }
        return added;
    }
    
    //-----------------------------------------------------------------------------
    public boolean addCategory(StockItem category) {
    // Load existing categories from the file
    Set<StockItem> existingCategories = FileIOUtils.loadStockItems();

    // Check if category ID already exists
    for (StockItem existingCategory : existingCategories) {
        if (existingCategory.getCategory().getIdCat() == category.getCategory().getIdCat()) {
            System.out.println("Category ID already exists. Failed to add new category.");
            return false;
        }
    }
    
    // If ID is unique, add the category
    boolean added = stockItems.add(category);
    if (added) {
        FileIOUtils.saveCategory(category);
    }
    return added;
}

    //-----------------------------------------------------------------------------
    public boolean addClient(Client client) {

        Set<Client> clients = FileIOUtils.loadClients();

        // Check if client ID already exists
        for (Client existingClient : clients) {
            if (existingClient.getIdloc() == client.getIdloc()) {
                System.out.println("Client ID already exists. Failed to add new client.");
                return false;
            }
        }
        
        // If ID is unique, add the client
        boolean added = clients.add(client);
        if (added) {
            FileIOUtils.saveClient(client);
        }
        return added;
    }

    //-----------------------------------------------------------------------------
    private void updateStock(int idCat, int quantityChange) {
        Set<StockItem> stockItems = FileIOUtils.loadStockItems();
        boolean found = false;
    
        // Check if the category already exists and update the quantity
        for (StockItem item : stockItems) {
            if (item.getCategory().getIdCat() == idCat) {
                item.setQuantite(item.getQuantite() + quantityChange);
                found = true;
                break;
            }
        }
    
        // If the category does not exist, add a new item with quantity = 1 if adding, or skip if reducing
        if (!found && quantityChange > 0) {
            ItemCategory newCategory = new ItemCategory(idCat);
            StockItem newItem = new StockItem(newCategory, quantityChange); // Assumes positive quantity for new additions
            stockItems.add(newItem);
        }
    
        // Save the updated stock items back to the file
        FileIOUtils.saveStockItems(stockItems);
    }
    
    
}
