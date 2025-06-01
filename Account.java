/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

import java.io.Serializable;

public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private int accountNumber;
    private String name;
    private int balance;
    private int pin;

    public Account(int accountNumber, String name, int balance, int pin) {
        validateInputs(accountNumber, name, balance, pin);
        this.accountNumber = accountNumber;
        this.name = name.trim();
        this.balance = balance;
        this.pin = pin;
    }

    private void validateInputs(int accountNumber, String name, int balance, int pin) {
        if (accountNumber <= 0) throw new IllegalArgumentException("Invalid account number");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (!name.matches("[A-Za-z ]+")) throw new IllegalArgumentException("Name can only contain letters and spaces");
        if (balance < 0) throw new IllegalArgumentException("Balance cannot be negative");
        if (pin < 1000 || pin > 9999) throw new IllegalArgumentException("PIN must be 4 digits");
    }

    public abstract String getAccountType();

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public int getBalance() { return balance; }
    public int getPin() { return pin; }

    public void deposit(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        balance += amount;
    }

    public void withdraw(int amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");
        if (amount > balance) throw new InsufficientFundsException("Insufficient funds");
        balance -= amount;
    }

    public boolean verifyPin(int pin) {
        return this.pin == pin;
    }
}
