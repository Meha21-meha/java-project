/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

import java.util.List;
import java.util.Scanner;

public class ATM {
    private Scanner scanner = new Scanner(System.in);
    private Account currentAccount;

    public void run() {
        System.out.println("\n=== ATM Interface ===");
        
        try {
            while (true) {
                if (currentAccount == null) {
                    if (!authenticate()) {
                        continue;
                    }
                }
                
                showMainMenu();
                
                System.out.print("\nPerform another transaction? (y/n): ");
                if (!scanner.nextLine().equalsIgnoreCase("y")) {
                    currentAccount = null;
                    return;
                }
            }
        } catch (BankingException e) {
            System.err.println("ATM Error: " + e.getMessage());
        }
    }

    private boolean authenticate() throws BankingException {
        try {
            System.out.print("\nEnter account number: ");
            int accNumber = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Enter 4-digit PIN: ");
            int pin = Integer.parseInt(scanner.nextLine());
            
            if (pin < 1000 || pin > 9999) {
                System.out.println("PIN must be 4 digits!");
                return false;
            }
            
            currentAccount = FileDataStore.findAccount(accNumber);
            if (currentAccount == null || !currentAccount.verifyPin(pin)) {
                System.out.println("Invalid account number or PIN!");
                currentAccount = null;
                return false;
            }
            
            System.out.println("\nWelcome, " + currentAccount.getName() + "!");
            return true;
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numbers only.");
            return false;
        }
    }

    private void showMainMenu() throws BankingException {
        System.out.println("\n1. Check Balance");
        System.out.println("2. Withdraw Cash");
        System.out.println("3. Deposit Funds");
        System.out.println("4. View Transactions");
        System.out.println("5. Exit");
        System.out.print("Choose option (1-5): ");
        
        String input = scanner.nextLine();
        
        try {
            int choice = Integer.parseInt(input);
            
            switch (choice) {
                case 1:
                    checkBalance();
                    break;
                case 2:
                    withdrawCash();
                    break;
                case 3:
                    depositFunds();
                    break;
                case 4:
                    viewTransactions();
                    break;
                case 5:
                    currentAccount = null;
                    return;
                default:
                    System.out.println("Invalid choice! Please select 1-5");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number (1-5)");
        }
    }

    private void checkBalance() {
        System.out.printf("\nCurrent Balance: $%,d%n", currentAccount.getBalance());
    }

    private void withdrawCash() throws BankingException {
        try {
            System.out.print("\nEnter withdrawal amount: $");
            int amount = Integer.parseInt(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return;
            }
            
            if (amount % 20 != 0) {
                System.out.println("ATM dispenses $20 bills only. Please enter multiples of 20.");
                return;
            }
            
            currentAccount.withdraw(amount);
            FileDataStore.saveAccount(currentAccount);
            FileDataStore.logTransaction(currentAccount.getAccountNumber(), "ATM_WITHDRAWAL", -amount);
            
            System.out.printf("Dispensing $%,d...%n", amount);
            System.out.printf("New Balance: $%,d%n", currentAccount.getBalance());
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter numbers only.");
        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void depositFunds() throws BankingException {
        try {
            System.out.print("\nEnter deposit amount: $");
            int amount = Integer.parseInt(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return;
            }
            
            System.out.print("Insert envelope with cash/checks now...");
            Thread.sleep(2000);
            
            currentAccount.deposit(amount);
            FileDataStore.saveAccount(currentAccount);
            FileDataStore.logTransaction(currentAccount.getAccountNumber(), "ATM_DEPOSIT", amount);
            
            System.out.printf("\nDeposit of $%,d accepted.%n", amount);
            System.out.printf("New Balance: $%,d%n", currentAccount.getBalance());
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter numbers only.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Transaction interrupted!");
        }
    }

    private void viewTransactions() {
        try {
            System.out.println("\nRecent Transactions:");
            System.out.println("-----------------------------------------------");
            System.out.printf("%-12s | %-20s | %-10s%n", "Date", "Type", "Amount");
            System.out.println("-----------------------------------------------");
            
            List<String> transactions = FileDataStore.getTransactionHistory(currentAccount.getAccountNumber());
            
            if (transactions.isEmpty()) {
                System.out.println("No transactions found");
                return;
            }
            
            int count = Math.min(5, transactions.size());
            for (int i = transactions.size() - 1; i >= transactions.size() - count; i--) {
                String[] parts = transactions.get(i).split("\\|");
                String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm")
                                     .format(new java.util.Date(Long.parseLong(parts[1])));
                
                System.out.printf("%-12s | %-20s | $%,-10d%n", 
                    date, parts[2], Integer.parseInt(parts[3]));
            }
            System.out.println("-----------------------------------------------");
            
        } catch (BankingException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    }