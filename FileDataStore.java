package banking;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileDataStore {
    private static final String DESKTOP_PATH = System.getProperty("user.home") + "/Desktop/";
    private static final String DATA_DIR = DESKTOP_PATH + "BankksData";
    private static final String ACCOUNTS_FILE = DATA_DIR + "/accountss.txt";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactionsss.log";
    private static final String HEADER = "Account Number|Name|Balance|PIN";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            System.out.println("Data stored at: " + DATA_DIR);
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    public static synchronized void saveAccount(Account account) throws BankingException {
        try {
            List<Account> accounts = loadAllAccounts();
            accounts.removeIf(a -> a.getAccountNumber() == account.getAccountNumber());
            accounts.add(account);
            saveAllAccounts(accounts);
        } catch (Exception e) {
            throw new BankingException("Failed to save account", e);
        }
    }

    public static synchronized List<Account> loadAllAccounts() throws BankingException {
        List<Account> accounts = new ArrayList<>();
        if (!Files.exists(Paths.get(ACCOUNTS_FILE))) return accounts;

        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length != 4) {
                    throw new BankingException("Corrupted data in account.txt: " + line, null);
                }
                try {
                    int accountNumber = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int balance = Integer.parseInt(parts[2]);
                    int pin = Integer.parseInt(parts[3]);
                    Account account = new SavingsAccount(accountNumber, name, balance, pin);
                    accounts.add(account);
                } catch (NumberFormatException e) {
                    throw new BankingException("Invalid number format in account.txt: " + line, e);
                }
            }
        } catch (IOException e) {
            throw new BankingException("Failed to load accounts", e);
        }
        return accounts;
    }

    private static synchronized void saveAllAccounts(List<Account> accounts) throws BankingException {
        File tempFile = new File(ACCOUNTS_FILE + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(tempFile))) {
            for (Account account : accounts) {
                String record = String.format("%d|%s|%d|%d%n",
                    account.getAccountNumber(),
                    account.getName(),
                    account.getBalance(),
                    account.getPin());
                writer.write(record);
            }
        } catch (IOException e) {
            throw new BankingException("Failed to save accounts", e);
        }
        try {
            Files.move(tempFile.toPath(), Paths.get(ACCOUNTS_FILE),
                      java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BankingException("Failed to update accounts file", e);
        }
    }

    public static synchronized void logTransaction(int accountNumber, String type, int amount) throws BankingException {
        try (BufferedWriter writer = new BufferedWriter(
             new java.io.FileWriter(TRANSACTIONS_FILE, true))) {
            String record = String.format("%d|%d|%s|%d%n",
                accountNumber, System.currentTimeMillis(), type, amount);
            writer.write(record);
        } catch (Exception e) {
            throw new BankingException("Failed to log transaction", e);
        }
    }

    public static synchronized List<String> getTransactionHistory(int accountNumber) throws BankingException {
        List<String> transactions = new ArrayList<>();
        if (!Files.exists(Paths.get(TRANSACTIONS_FILE))) return transactions;
        try (BufferedReader reader = new BufferedReader(
             new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(accountNumber + "|")) {
                    transactions.add(line);
                }
            }
        } catch (Exception e) {
            throw new BankingException("Failed to load transactions", e);
        }
        return transactions;
    }

    public static synchronized Account findAccount(int accountNumber) throws BankingException {
        return loadAllAccounts().stream()
            .filter(a -> a.getAccountNumber() == accountNumber)
            .findFirst()
            .orElse(null);
    }

    public static synchronized boolean deleteAccount(int accountNumber) throws BankingException {
        List<Account> accounts = loadAllAccounts();
        boolean removed = accounts.removeIf(a -> a.getAccountNumber() == accountNumber);
        if (removed) saveAllAccounts(accounts);
        return removed;
    }
}          
