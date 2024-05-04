package com.locengin.business;

import com.locengin.model.Client;
import com.locengin.model.RentedItem;
import com.locengin.model.StockItem;
import com.locengin.model.Equipment;
import com.locengin.utils.FileIOUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryProcessor {

    //-----------------------------------------------------------------------------
    public static Set<Equipment> getAllItems() {
        return FileIOUtils.loadEquipmentItems();
    }
    //-----------------------------------------------------------------------------
    public Set<RentedItem> getRentedItems() {
        return FileIOUtils.loadRentals();
    }
    //-----------------------------------------------------------------------------
    public static Equipment getItemByCode(int codeEquip) {
        return getAllItems().stream()
                             .filter(item -> item.getCodeEquip() == codeEquip)
                             .findFirst()
                             .orElse(null);  // Returns null if no item found
    }
    //-----------------------------------------------------------------------------
    public static Client getClientById(int clientId) {
        return FileIOUtils.loadClients().stream()
                                         .filter(client -> client.getIdloc() == clientId)
                                         .findFirst()
                                         .orElse(null);  
    }
    //-----------------------------------------------------------------------------
    public RentedItem getRentedItemByCode(int rentalCode) {
        return getRentedItems().stream()
                               .filter(item -> item.getItem().getCodeEquip() == rentalCode)
                               .findFirst()
                               .orElse(null);  // Returns null if no rented item found
    }
    //-----------------------------------------------------------------------------
    public Set<RentedItem> getRentedItemByClient(Client client) {
        return getRentedItems().stream()
                               .filter(item -> item.getClient().getIdloc() == client.getIdloc())
                               .collect(Collectors.toSet());
    }
    //-----------------------------------------------------------------------------
    public Set<Equipment> overdueItems(Client client, Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
        return getRentedItems().stream()
            .filter(rentedItem -> {
                try {
                    // Normalize the due date to remove time portion
                    Date dueDateNormalized = sdf.parse(sdf.format(rentedItem.getDueDate()));
                    return dueDateNormalized.before(currentDate) &&
                           rentedItem.getClient().getIdloc() == client.getIdloc();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;  // Skip this item if there's a parsing error
                }
            })
            .map(RentedItem::getItem)
            .collect(Collectors.toSet());
    }
    
    //-----------------------------------------------------------------------------
    public double soldeClient(Client client) {
        return client.getSolde();
    }
}
