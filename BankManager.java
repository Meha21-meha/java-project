/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

public class BankManager implements BankOperations {
    private static int nextAccountNumber = 1000;

    @Override
   

    public Account createAccount(String name, int pin) throws BankingException {
        validateName(name);
        validatePin(pin);
        Account newAccount = new SavingsAccount(nextAccountNumber++, name, 1000, pin);
        FileDataStore.saveAccount(newAccount);
        FileDataStore.logTransaction(newAccount.getAccountNumber(), "ACCOUNT_CREATION", 1000);
        return newAccount;
    }

    public Account login(String name, int pin) throws BankingException {
        Account account = FileDataStore.loadAllAccounts().stream()
            .filter(a -> a.getName().equalsIgnoreCase(name) && a.verifyPin(pin))
            .findFirst()
            .orElse(null);
        
        if (account == null) throw new BankingException("Invalid credentials", null);
        return account;
    }

    public void transfer(Account sender, int recipientNumber, int amount) 
            throws BankingException, InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive");
        
        Account recipient = FileDataStore.findAccount(recipientNumber);
        if (recipient == null) throw new BankingException("Recipient account not found", null);
        if (sender.getAccountNumber() == recipientNumber) throw new BankingException("Cannot transfer to yourself", null);

        sender.withdraw(amount);
        recipient.deposit(amount);
        
        FileDataStore.saveAccount(sender);
        FileDataStore.saveAccount(recipient);
        FileDataStore.logTransaction(sender.getAccountNumber(), "TRANSFER_OUT:" + recipientNumber, -amount);
        FileDataStore.logTransaction(recipientNumber, "TRANSFER_IN:" + sender.getAccountNumber(), amount);
    }

    public boolean deleteAccount(int accountNumber, int pin) throws BankingException {
        Account account = FileDataStore.findAccount(accountNumber);
        if (account == null || !account.verifyPin(pin)) return false;
        
        FileDataStore.deleteAccount(accountNumber);
        FileDataStore.logTransaction(accountNumber, "ACCOUNT_DELETED", 0);
        return true;
    }

    private static void validateName(String name) throws BankingException {
        if (name == null || name.trim().isEmpty()) {
            throw new BankingException("Name cannot be empty", null);
        }
        if (!name.matches("[A-Za-z ]+")) {
            throw new BankingException("Name can only contain letters and spaces", null);
        }
        if (name.length() > 50) {
            throw new BankingException("Name too long (max 50 chars)", null);
        }
    }

    private static void validatePin(int pin) throws BankingException {
        if (pin < 1000 || pin > 9999) {
            throw new BankingException("PIN must be 4 digits", null);
        }
    }
}