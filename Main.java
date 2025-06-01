package banking;

//import banking.exceptions.BankingException;
//import banking.exceptions.InsufficientFundsException;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            while (true) {
                System.out.println("\n=== Banking System ===");
                System.out.println("1. Create Account");
                System.out.println("2. Login");
                System.out.println("3. Use ATM");
                System.out.println("4. Exit");
                System.out.print("Choose option: ");
                
                int choice = getIntInput(scanner);
                
                switch (choice) {
                    case 1:
                        createAccount(scanner);
                        break;
                    case 2:
                        login(scanner);
                        break;
                    case 3:
                        new ATM().run();
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void createAccount(Scanner scanner) throws BankingException {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine();
        System.out.print("Create 4-digit PIN: ");
        int pin = getIntInput(scanner);
        BankManager bankManager = new BankManager();
        Account account = bankManager.createAccount(name, pin);
        System.out.printf("Account created! Number: %d%n", account.getAccountNumber());
    }

    private static void login(Scanner scanner) {
        try {
            System.out.print("\nEnter your name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter your PIN: ");
            int pin = getIntInput(scanner);
            
            BankManager bankManager = new BankManager();
            Account account = bankManager.login(name, pin);
            accountMenu(scanner, account);
        } catch (BankingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void accountMenu(Scanner scanner, Account account) {
        try {
            while (true) {
                System.out.println("\nWelcome, " + account.getName() + "!");
                System.out.println("1. View Balance");
                System.out.println("2. Transfer Money");
                System.out.println("3. View Transactions");
                System.out.println("4. Delete Account");
                System.out.println("5. Logout");
                System.out.print("Choose option: ");
                
                int choice = getIntInput(scanner);
                
                switch (choice) {
                    case 1:
                        System.out.printf("\nBalance: $%,d%n", account.getBalance());
                        break;
                    case 2:
                        transferMoney(scanner, account);
                        break;
                    case 3:
                        viewTransactions(account);
                        break;
                    case 4:
                        if (deleteAccount(scanner, account)) return;
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void transferMoney(Scanner scanner, Account sender) {
        try {
            System.out.print("\nEnter recipient account number: ");
            int recipient = getIntInput(scanner);
            
            System.out.print("Enter amount: ");
            int amount = getIntInput(scanner);
            
            BankManager bankManager = new BankManager();
            bankManager.transfer(sender, recipient, amount);
            System.out.println("Transfer successful!");
        } catch (BankingException | InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewTransactions(Account account) {
        try {
            System.out.println("\nTransaction History:");
            List<String> transactions = FileDataStore.getTransactionHistory(account.getAccountNumber());
            
            if (transactions.isEmpty()) {
                System.out.println("No transactions found");
                return;
            }
            
            for (String tx : transactions) {
                String[] parts = tx.split("\\|");
                String timestamp = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm")
                    .format(new java.util.Date(Long.parseLong(parts[1])));
                System.out.printf("%s - %s: $%,d%n", timestamp, parts[2], Integer.parseInt(parts[3]));
            }
        } catch (BankingException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static boolean deleteAccount(Scanner scanner, Account account) {
        try {
            System.out.print("\nAre you sure? (y/n): ");
            if (!scanner.nextLine().equalsIgnoreCase("y")) return false;
            
            System.out.print("Enter PIN to confirm: ");
            int pin = getIntInput(scanner);
            
            BankManager bankManager = new BankManager();
            if (bankManager.deleteAccount(account.getAccountNumber(), pin)) {
                System.out.println("Account deleted successfully");
                return true;
            }
            System.out.println("Account deletion failed");
        } catch (BankingException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    private static int getIntInput(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}