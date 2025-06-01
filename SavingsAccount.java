/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

public class SavingsAccount extends Account {
    private static final long serialVersionUID = 2L;
    private static final double INTEREST_RATE = 0.02;

    public SavingsAccount(int accountNumber, String name, int balance, int pin) {
        super(accountNumber, name, balance, pin);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    public void applyInterest() {
        int balance = getBalance();
        balance += (int)(balance * INTEREST_RATE);
        deposit((int)(balance * INTEREST_RATE));
    }
}
