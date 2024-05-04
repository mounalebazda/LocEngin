package com.locengin.utils;

import com.locengin.business.QueryProcessor;
import com.locengin.model.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FileIOUtils {

    // File paths
    public static final String STOCK_FILE = "./com/locengin/utils/data/stock.csv";
    public static final String EQUIPMENT_FILE = "./com/locengin/utils/data/equipment.csv";
    public static final String CLIENT_FILE = "./com/locengin/utils/data/clients.csv";
    public static final String RENTAL_FILE = "./com/locengin/utils/data/rentals.csv";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    //-----------------------------------------------------------------------------
    public static Set<Client> loadClients() {
        Set<Client> clients = new HashSet<>();
        File file = new File(CLIENT_FILE);
        if (!file.exists()) {
            System.err.println("Client file not found at: " + file.getAbsolutePath());
            return clients;  // Return empty set if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {  // Ensure there are exactly 6 parts as expected
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String address = parts[2];
                        String profession = parts[3];
                        String telephone = parts[4];
                        double balance = Double.parseDouble(parts[5]);

                        clients.add(new Client(id, name, address, profession, telephone, balance));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing client data: " + e.getMessage());
                    }
                } else {
                    System.err.println("Malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load client data: " + e.getMessage());
        }
        return clients;
    }

    //-----------------------------------------------------------------------------
    public static void saveClient(Client client) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CLIENT_FILE, true))) {  // true to append data
            // Write a single client's data
            bw.write(client.getIdloc() + "," + client.getnom() + "," +
                     client.getAddresse() + "," + client.getProfession() + "," +
                     client.getTelephone() + "," + client.getSolde());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------
    public static void updateClient(Client updatedClient) {
        Set<Client> clients = loadClients();  // Load all clients
        boolean isUpdated = false;
    
        // Attempt to find and update the client
        for (Client client : clients) {
            if (client.getIdloc() == updatedClient.getIdloc()) {
                client.setnom(updatedClient.getnom());
                client.setAddresse(updatedClient.getAddresse());
                client.setProfession(updatedClient.getProfession());
                client.setTelephone(updatedClient.getTelephone());
                client.setSolde(updatedClient.getSolde());
                isUpdated = true;
                break;
            }
        }
    
        if (isUpdated) {
            saveClients(clients);  // Re-write all clients back into the file
        }
    }
    
    //-----------------------------------------------------------------------------
    public static void saveClients(Set<Client> clients) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CLIENT_FILE, false))) {  // false to overwrite data
            bw.write("Idloc,nom,adresse,profession,téléphone,solde");
            bw.newLine();
            for (Client client : clients) {
                bw.write(client.getIdloc() + "," + client.getnom() + "," +
                         client.getAddresse() + "," + client.getProfession() + "," +
                         client.getTelephone() + "," + client.getSolde());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //-----------------------------------------------------------------------------
    public static void saveEquipmentItem(Equipment item) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EQUIPMENT_FILE,true))) {
            // Write only the required fields: codeEquip, idCat from the item's category, and the item's status
            bw.write(item.getCodeEquip() + "," + item.getCategory().getIdCat() + "," + item.getEtat());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------
    public static void saveEquipmentItems(Set<Equipment> items) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EQUIPMENT_FILE))) {
            bw.write("Codequip,IDcat,ETAT");  // Header corresponding to the fields you want to save
            bw.newLine();
            for (Equipment item : items) {
                // Write only the required fields: codeEquip, idCat from the item's category, and the item's status
                bw.write(item.getCodeEquip() + "," + item.getCategory().getIdCat() + "," + item.getEtat());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //-----------------------------------------------------------------------------
    public static Set<Equipment> loadEquipmentItems() {
        Set<Equipment> items = new HashSet<>();
        File file = new File(EQUIPMENT_FILE);
        if (!file.exists()) {
            System.err.println("Equipment file not found at: " + file.getAbsolutePath());
            return items; 
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) { // Ensure there are exactly 3 parts as expected
                    try {
                        int codeEquip = Integer.parseInt(parts[0]);
                        int idCat = Integer.parseInt(parts[1]);
                        String etat = parts[2];
    
                        // Since `designation` and `marque` are not provided, use default or placeholder values
                        ItemCategory category = new ItemCategory(idCat);
    
                        Equipment item = new Equipment(codeEquip, etat, category);
                        items.add(item);
                    } catch (NumberFormatException nfe) {
                        System.err.println("Error parsing equipment data: " + nfe.getMessage());
                    }
                } else {
                    System.err.println("Malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load equipment items: " + e.getMessage());
        }
        return items;
    }
    
    //-----------------------------------------------------------------------------
    public static void saveStockItems(Set<StockItem> stockItems) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STOCK_FILE))) {
            bw.write("IDcat,DESIGNATION,MARQUE,QUANTITE");
            bw.newLine();
            for (StockItem item : stockItems) {
                bw.write(item.getCategory().getIdCat() + "," +
                         item.getCategory().getDesignation() + "," + item.getCategory().getMarque() + "," +
                         item.getQuantite());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------
    public static Set<StockItem> loadStockItems() {
        Set<StockItem> stockItems = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(STOCK_FILE))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int idCat = Integer.parseInt(parts[0]);
                String designation = parts[1];  
                String marque = parts[2];       
                int quantite = Integer.parseInt(parts[3]);       
                stockItems.add(new StockItem(new ItemCategory(idCat, designation, marque),quantite));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockItems;
    }

    //-----------------------------------------------------------------------------
    public static void saveCategory(StockItem category) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STOCK_FILE, true))) {
            bw.write(category.getCategory().getIdCat() + "," + category.getCategory().getDesignation() + "," +
                     category.getCategory().getMarque() + "," + category.getQuantite());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------
    public static void saveRentalOnAdd(RentedItem rented) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RENTAL_FILE, true))) {
            Date dateremise = calculateDueDate(rented.getDatesortie());
            bw.write(rented.getItem().getCodeEquip() + "," + rented.getClient().getIdloc() + "," +
                     dateFormat.format(rented.getDatesortie()) + "," +
                     dateFormat.format(dateremise) + "," + rented.getMontant());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------
    public static void saveRentalOnRemove(RentedItem rentedToRemove) {
        Set<RentedItem> rentedItems = loadRentals(); // Load existing rentals
    
        // Remove the specific item completely based on equipment code and client ID
        rentedItems.removeIf(item -> item.getItem().getCodeEquip() == rentedToRemove.getItem().getCodeEquip() &&
                                     item.getClient().getIdloc() == rentedToRemove.getClient().getIdloc());
    
        // Rewrite the file with the remaining items
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RENTAL_FILE))) {
            bw.write("codequip,idloc,datesortie,dateremise,montant");
            bw.newLine();
            for (RentedItem remainingRental : rentedItems) {
                bw.write(remainingRental.getItem().getCodeEquip() + "," + remainingRental.getClient().getIdloc() + "," +
                         dateFormat.format(remainingRental.getDatesortie()) + "," +
                         dateFormat.format(remainingRental.getDueDate()) + "," + remainingRental.getMontant());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to update rentals file after removal: " + e.getMessage());
        }
    }
    
    //-----------------------------------------------------------------------------
    public static Date calculateDueDate(Date rentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(rentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 15);  // Add 15 days
        return calendar.getTime();
    }

    //-----------------------------------------------------------------------------
    public static Set<RentedItem> loadRentals() {
        Set<RentedItem> rentedItems = new HashSet<>();
        File file = new File(RENTAL_FILE);
        if (!file.exists()) {
            System.err.println("Rental file not found at: " + file.getAbsolutePath());
            return rentedItems;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        int codeEquip = Integer.parseInt(parts[0]);
                        int clientId = Integer.parseInt(parts[1]);
                        Date datesortie = dateFormat.parse(parts[2]);
                        Date dateremise = dateFormat.parse(parts[3]);
                        double montant = Double.parseDouble(parts[4]);

                        // Fetch client and item from your data handling layer
                        Client client = QueryProcessor.getClientById(clientId);  // Hypothetical method to fetch client
                        Equipment item = QueryProcessor.getItemByCode(codeEquip);  // Hypothetical method to fetch item

                        if (client != null && item != null) {
                            RentedItem rentedItem = new RentedItem(datesortie,dateremise, item, client, montant);
                            rentedItems.add(rentedItem);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing rental data: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load rental items: " + e.getMessage());
        }
        return rentedItems;
    }

   
}
