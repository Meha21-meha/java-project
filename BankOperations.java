/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package banking;
public interface BankOperations {
    Account createAccount(String name, int pin) throws BankingException;
    Account login(String name, int pin) throws BankingException;
    void transfer(Account sender, int recipientNumber, int amount) 
        throws BankingException, InsufficientFundsException;
    boolean deleteAccount(int accountNumber, int pin) throws BankingException;
}

