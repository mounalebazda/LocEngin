package com.locengin.ui;

import com.locengin.business.QueryProcessor;
import com.locengin.business.TransactionProcessor;
import com.locengin.model.Client;
import com.locengin.model.ItemCategory;
import com.locengin.model.RentedItem;
import com.locengin.model.StockItem;
import com.locengin.utils.FileIOUtils;
import com.locengin.model.Equipment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

public class GUI {
    private TransactionProcessor transactionProcessor;
    private QueryProcessor queryProcessor;

    private Scanner scanner;
    
    public static void main(String[] args) {
        new GUI();  
    }
    
    public GUI() {
        scanner = new Scanner(System.in);
        this.transactionProcessor = new TransactionProcessor();
        this.queryProcessor = new QueryProcessor();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            System.out.println("\n**********Main Menu:*************");
            System.out.println("1. Client Requests and Transactions");
            System.out.println("2. Equipment and Stock Management");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    clientMenu();
                    break;
                case 2:
                    equipmentMenu();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void clientMenu() {
        System.out.println("\n***********Client Menu:**********");
        System.out.println("1. Rent an Item");
        System.out.println("2. Return an Item");
        System.out.println("3. Display Rented Items by Client");
        System.out.println("4. Display Client Balance and Overdue Items");
        System.out.println("5. Add New Client");
        System.out.println("6. Go back to Main Menu");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                rentItemMenu();
                break;
            case 2:
                returnItemMenu();
                break;
            case 3:
                displayRentedItemsByClient();
                break;
            case 4:
                displayClientBalanceAndOverdueItems();
                break;
            case 5:
                addNewClient();
                break;
            case 6:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void equipmentMenu() {
        System.out.println("\n***********Equipment and Stock Menu:************");
        System.out.println("1. Display Available Items");
        System.out.println("2. Display All Items");
        System.out.println("3. Add New Equipment to Stock");
        System.out.println("4. Add New Category");
        System.out.println("5. Go back to Main Menu");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                displayAvailableItems();
                break;
            case 2:
                displayAllItems();
                break;
            case 3:
                addNewEquipment();
                break;
            case 4:
                addNewCategory();
            case 5:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    //-----------------------------------------------------------------------------
    private void rentItemMenu() {
        System.out.print("Enter item code: ");
        int code = scanner.nextInt();
        System.out.print("Enter client ID: ");
        int clientId = scanner.nextInt();

        Equipment item = QueryProcessor.getItemByCode(code);
        Client client = QueryProcessor.getClientById(clientId);

        if (item != null && client != null) {
            Date rentDate = new Date();

            if (transactionProcessor.checkOutItem(item, client, rentDate)) {
                System.out.println("Item rented successfully.");
            } else {
                System.out.println("Failed to rent item.");
            }
        } else {
            System.out.println("Invalid item code or client ID.");
        }
    }

    //-----------------------------------------------------------------------------
    private void returnItemMenu() {
    System.out.print("Enter rental item code: ");
    int rentalCode = scanner.nextInt();

    RentedItem rentedItem = queryProcessor.getRentedItemByCode(rentalCode);
    if (rentedItem != null) {
        Date currentDate = new Date();  // Capture the current date to determine if there's a delay
        boolean isLate = currentDate.after(rentedItem.getDueDate());

        if (transactionProcessor.checkInItem(rentedItem)) {  // Update to mark item as returned
            System.out.println("Item returned successfully.");
            if (isLate) {
                long daysLate = (currentDate.getTime() - rentedItem.getDueDate().getTime()) / (1000 * 60 * 60 * 24);
                double penalty = calculatePenalty(daysLate);
                Client client = rentedItem.getClient();
                client.setSolde(client.getSolde() - penalty);
                FileIOUtils.updateClient(client); // Update client record with new balance
                System.out.printf("Penalty applied for %d days late: $%.2f. New balance: $%.2f\n", daysLate, penalty, client.getSolde());
            }
        } else {
            System.out.println("Failed to return item.");
        }
    } else {
        System.out.println("Invalid rental item code.");
    }
}
    private double calculatePenalty(long daysLate) {
        final double dailyPenaltyRate = 5.0; 
        return daysLate * dailyPenaltyRate;
    }


    //-----------------------------------------------------------------------------
    public void displayAvailableItems() {
        Set<Equipment> items = QueryProcessor.getAllItems();
        System.out.println("Available Items:");
        items.forEach(item -> {
            if ("disponible".equals(item.getEtat())) {
                System.out.println("Code: " + item.getCodeEquip() +
                                   ", Category ID: " + item.getCategory().getIdCat() +
                                   ", Status: " + item.getEtat());
            }
        });
    }
    
    //-----------------------------------------------------------------------------
    private void displayAllItems() {
        Set<Equipment> items = QueryProcessor.getAllItems();
        System.out.println("All Items:");
        items.forEach(item -> {
            // We assume the category will never be null because it's created with default values in loadEquipmentItems
            System.out.println("Code: " + item.getCodeEquip() +
                               ", Category ID: " + item.getCategory().getIdCat() +
                               ", Status: " + item.getEtat());
        });
    }
    

    
    //-----------------------------------------------------------------------------
    private void displayRentedItemsByClient() {
        System.out.print("Enter client ID: ");
        int clientId = scanner.nextInt();
        Client client = QueryProcessor.getClientById(clientId);
        if (client != null) {
            Set<RentedItem> rentedItems = queryProcessor.getRentedItemByClient(client);
            rentedItems.forEach(item -> System.out.println("Item: " + item.getItem().getCodeEquip() + ", Due Date: " + item.getDueDate()));
        } else {
            System.out.println("No such client found.");
        }
    }

    //-----------------------------------------------------------------------------
    private void displayClientBalanceAndOverdueItems() {
        System.out.print("Enter client ID: ");
        int clientId = scanner.nextInt();
        Date currecDate = new Date();  // This line creates a new Date object capturing the current date and time.
        
        // Create a SimpleDateFormat instance to format your date in a readable form, e.g., "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Now print the formatted date using printf
        System.out.printf("Current date: %s%n", sdf.format(currecDate));
        Client client = QueryProcessor.getClientById(clientId);
        if (client != null) {
            double balance = queryProcessor.soldeClient(client);
            System.out.println("Client Balance: $" + balance);
            Set<Equipment> overdueItems = queryProcessor.overdueItems(client,currecDate);
            System.out.println("Overdue Items:");
            overdueItems.forEach(item -> System.out.println(item.getCodeEquip()));
        } else {
            System.out.println("No such client found.");
        }
    }

    //-----------------------------------------------------------------------------
    private void addNewEquipment() {
        System.out.print("Enter equipment code: ");
        int code = scanner.nextInt();
        System.out.print("Enter category ID: ");
        int catId = scanner.nextInt();
        System.out.print("Enter status (disponible, rented, hors-service): ");
        String status = scanner.next();
        Equipment newItem = new Equipment(code, status, new ItemCategory(catId, "New Item", "Generic Brand"));
        if (transactionProcessor.addIitem(newItem)) {
            System.out.println("New equipment added successfully.");
        } else {
            System.out.println("Failed to add new equipment.");
        }
    }

    //-----------------------------------------------------------------------------
    private void addNewCategory() {
        System.out.print("Enter category ID: ");
        int catId = scanner.nextInt();
        System.out.print("Enter Designation: ");
        String designation = scanner.next();
        System.out.print("enter marque: ");
        String marque = scanner.next();
        StockItem stockItem = new StockItem(new ItemCategory(catId, designation, marque),1);
        if (transactionProcessor.addCategory(stockItem)) {
            System.out.println("New equipment added successfully.");
        } else {
            System.out.println("Failed to add new equipment.");
        }
    }

    //-----------------------------------------------------------------------------
    private void addNewClient() {
        scanner.nextLine();  // Consume any leftover newline from previous inputs.
        System.out.print("Enter client ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume the newline after the integer input.
        System.out.print("Enter client name: ");
        String name = scanner.nextLine();
        System.out.print("Enter client address: ");
        String address = scanner.nextLine();
        System.out.print("Enter client profession: ");
        String profession = scanner.nextLine();
        System.out.print("Enter client telephone: ");
        String telephone = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();
    
        Client newClient = new Client(id, name, address, profession, telephone, balance);
        if (transactionProcessor.addClient(newClient)) {
            System.out.println("New client added successfully.");
        } else {
            System.out.println("Failed to add new client.");
        }
    }
    
}
