// Program Name: Automatic Teller Machine (ATM)
// Programmer:   Sintumnyuy Joseph
// Class:        CIS-084 Java Programming
// Date:         May 08, 2021
// Version:      1.0

package javaatm;

import java.util.Scanner;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.EOFException;

public class JavaATM {
    // select either the binary search or the sequential search working with the customer data file
    static final boolean BINARY_SEARCH = false; // false = sequential search

    // Location of the ATM data file on the disk
    //  Change this definition to the location of where you placed the ATM_accounts.dat file
    static final String ATM_FILENAME = "/Users/josephsintum/Documents/school /Java/code/gui_lab/src/javaatm/ATM_accounts.bin";

    static Scanner stdin;  // used by the input methods

    public static void main(String[] args) {

        long customerIndex;
        char anotherRequest = ' ';
        stdin = new Scanner(System.in); // initialize the scanner object

        while (true) {  // infinite loop - typical for embedded systems
            System.out.print("\n\n===========================\n");
            System.out.print("== Welcome to Phony Bank ==\n");
            System.out.print("===========================\n\n");

            customerIndex = searchForCustomer();
            do {
                if (customerIndex == -1) {
                    return;  // can't open the customer file. Exit program
                } else if (customerIndex == -2) {
                    System.out.print("Account number not found\n");
                    continue;
                } else if (customerIndex == -3) {
                    System.out.print("Incorrect PIN\n");
                    continue;
                } else if (customerIndex == -4)  // the customer file was displayed
                    continue;
                char checkingOrSavings = selectAccount(customerIndex); // 'C' = checking, 'S' = savings, 'X' = cancel
                if (checkingOrSavings == 'X')
                    break;  // done with this customer

                char transaction = selectTransaction();
                switch (transaction) {
                    case 'B':  // balance
                        System.out.printf("$%.2f\n", getBalance(customerIndex, checkingOrSavings));
                        break;
                    case 'D':  // deposit
                        deposit(customerIndex, checkingOrSavings);
                        break;
                    case 'W':  // withdraw
                        withdraw(customerIndex, checkingOrSavings);
                        break;
                    case 'X':  // cancel
                        break;
                }
                // Do you want another requst for the same customer?
                do { // get a 'Y' or 'N' response
                    System.out.print("Another transaction for the same customer? (Y/N)? ");
                    anotherRequest = Character.toUpperCase(getChar());
                } while (anotherRequest != 'Y' && anotherRequest != 'N');

            } while (anotherRequest == 'Y');
        } // end of while(true)
    } // end of main( )

    ////////////////////////////////////////////////////////////
    // searchForCustomer()
    //    return:  
    //      index into the file for the selected customer, or
    //      -1 = unable to open customer file
    //      -2 = customer number not found in the file
    //      -3 = PIN that is input does not match PIN in the file
    //      -4 = entire customer file was displayed to stdout
    static long searchForCustomer() {
        int accountNo;
        int pin = 0;
        long customerIndex;      // -2=customer not found,  -3=PIN does not match
        // customer record (acctNo, PIN, chk, sav)
        ATM customer = new ATM();

        System.out.println("Enter a customer account number, ");
        System.out.println("0 displays the customer data file ");
        System.out.println("or -1 to quit\n");   // 0 = display file
        System.out.println("Account? ");   // 0 = display file
        accountNo = getInt();
        if (accountNo == -1) return -1; // quit the program

        if (accountNo == 0) { // display file
            displayFile();
            return -4;  // entire customer file was displayed
        }
        if (accountNo >= 0) {
            System.out.print("Enter PIN? ");
            pin = getInt();
        }

        if (BINARY_SEARCH) {
            // the first thing to do is determine the number of customers in file
            // open the customer data file at the end of the file
            RandomAccessFile ATM_file;
            try {
                ATM_file = new RandomAccessFile(ATM_FILENAME, "r");

                // number of customers = file size divided by customer record size
                long nbytes = ATM_file.length(); // length in bytes
                long customerCount = nbytes / ATM.ATM_RECORD_SIZE;

                // prepare for binary search
                long imin = 0;                   // start of current  search
                long imax = customerCount - 1;   // end index for current search
                long imid;                       // midpoint for roughly equal parts

                customerIndex = -2; // assume customer not found until it happens
                while (imax >= imin) {
                    imid = (imin + imax) / 2;
                    // seek to the selected customer
                    long filePointer = imid * ATM.ATM_RECORD_SIZE;
                    ATM_file.seek(filePointer);
                    // read customer record into the customer object
                    customer.setAcctNo(ATM_file.readInt());
                    customer.setPIN(ATM_file.readInt());
                    customer.setChecking(ATM_file.readDouble());
                    customer.setSavings(ATM_file.readDouble());

                    if (accountNo == customer.getAcctNo()) { // found the customer in the file
                        if (pin == customer.getPIN())
                            customerIndex = imid;  // customer an PIN match
                        else
                            customerIndex = -3; // PIN does not match return code
                        break;  // customer has been found. Exit loop
                    } // end of if that is comparing requested acct number to one in the file
                    else if (accountNo < customer.getAcctNo())
                        imax = imid - 1; // divide search to use lower half
                    else
                        imin = imid + 1;  // divide to use upper half
                } // end of while statement that is searching the file
            } // end of try block
            catch (Exception e) {
                System.out.print("Unable to open ATM_accounts ");
                return -1;
            }
        } // end of binary search 


        else {  // SEQUENTIAL SEARCH
            // open the customer data file
            RandomAccessFile ATM_file;
            try {
                ATM_file = new RandomAccessFile(ATM_FILENAME, "r");
                // read first customer record
                customer.setAcctNo(ATM_file.readInt());
                customer.setPIN(ATM_file.readInt());
                customer.setChecking(ATM_file.readDouble());
                customer.setSavings(ATM_file.readDouble());

                for (int i = 0; true; i++) {
                    if (accountNo == customer.getAcctNo()) { // found the customer in the file
                        if (pin == customer.getPIN()) customerIndex = i;  // customer an PIN match
                        else customerIndex = -3; // PIN does not match return code
                        break;  // customer and PIN match the request. Exit loop
                    }
                    // read next customer record
                    customer.setAcctNo(ATM_file.readInt());
                    customer.setPIN(ATM_file.readInt());
                    customer.setChecking(ATM_file.readDouble());
                    customer.setSavings(ATM_file.readDouble());
                }
                ATM_file.close();
            } // end of try block
            catch (EOFException e) { // reached the end of file without finding customer
                customerIndex = -2;  // customer not found
            } catch (Exception e) {
                System.out.print("Unable to open ATM_accounts ");
                return -1;
            }
        } // end of sequential search

        if (customerIndex >= 0) {  // customer has been found. Display balances
            System.out.printf("\nChecking $%.2f   Savings $%.2f\n\n",
                    customer.getChecking(), customer.getSavings());
        }
        return customerIndex;
    }

    ////////////////////////////////////////////////////////////
    // char selectTransaction()
    //    return:  'B', 'D', 'W' or 'X'
    static char selectTransaction() { // D=deposit  W=withdraw  X=cancel
        System.out.println("Select transaction:");
        System.out.println("  B = Balance");
        System.out.println("  D = deposit");
        System.out.println("  W = withdraw");
        System.out.println("  X = cancel");

        char transactionType = ' ';
        do {
            transactionType = getChar();
            if (transactionType == 'B' || transactionType == 'D' || transactionType == 'W' || transactionType == 'X')
                break; // legal selection
            System.out.println("  Illegal selection. Try again.");
        } while (transactionType != 'B' && transactionType != 'D' && transactionType != 'W' && transactionType != 'X');
        return transactionType;
    }

    ////////////////////////////////////////////////////////////
    // displayFile()
    //   Displays all of the customer records in the file
    //     return:  0 = success,  -1 = error displaying file
    static int displayFile() {
        // customer record (acctNo, PIN, chk, sav)
        ATM customer = new ATM();
        RandomAccessFile ATM_file;
        try {
            // open the customer file
            ATM_file = new RandomAccessFile(ATM_FILENAME, "r");

            // display header information
            System.out.printf(" %-7s   %-4s  %-8s    %-8s\n",
                    "Acct #", "PIN", "Checking", "Savings");

            // read first customer record
            customer.setAcctNo(ATM_file.readInt());
            customer.setPIN(ATM_file.readInt());
            customer.setChecking(ATM_file.readDouble());
            customer.setSavings(ATM_file.readDouble());

            while (true) {  // read until the end of file has been reached
                System.out.println(customer);
                // read next customer record
                customer.setAcctNo(ATM_file.readInt());
                customer.setPIN(ATM_file.readInt());
                customer.setChecking(ATM_file.readDouble());
                customer.setSavings(ATM_file.readDouble());
            }
        } // end of try block
        catch (EOFException e) { // reached the end of file without finding customer
            return 0;  // success            
        } catch (Exception e) {
            System.out.print("Unable to open ATM_accounts ");
            return -1;
        }
    }

    ////////////////////////////////////////////////////////////
    // selectAccount()
    //    return:  'C  or  'S'   for checking or savings
    static char selectAccount(long customerIndex) {
        System.out.println("Select account:");
        System.out.println("  C = checking");
        System.out.println("  S = savings");
        System.out.println("  X = cancel");

        char accountType = ' ';  // C=checking,  S=savings

        do {
            System.out.print("? ");
            accountType = Character.toUpperCase(getChar());
            if (accountType == 'C' || accountType == 'S' || accountType == 'X')
                break; // legal selection
            System.out.print("  Illegal selection. Try again.\n");
        } while (accountType != 'C' && accountType != 'S' && accountType != 'X');
        return accountType;
    }

    ////////////////////////////////////////////////////////////
    // getBalance(long customerIndex, char accountType)
    //    where:
    //      customerIndex = customer number in the file
    //      accountType     'C'=checking,  'S'=savings
    //    return:  balance of either customer's checking or savings account
    static double getBalance(long customerIndex, char accountType) {
        // customer record (acctNo, PIN, chk, sav)
        ATM customer = new ATM();
        double balance = -1;
        RandomAccessFile ATM_file;
        try {
            // open the customer file
            ATM_file = new RandomAccessFile(ATM_FILENAME, "r");
            // seek to the selected customer record
            ATM_file.seek(customerIndex * ATM.ATM_RECORD_SIZE);
            // read customer record
            customer.setAcctNo(ATM_file.readInt());
            customer.setPIN(ATM_file.readInt());
            customer.setChecking(ATM_file.readDouble());
            customer.setSavings(ATM_file.readDouble());
            // return the balance for the selected account type
            if (accountType == 'C')
                balance = customer.getChecking();
            else if (accountType == 'S')
                balance = customer.getSavings();
            ATM_file.close();
            return balance;
        } // end try block
        catch (EOFException e) { // reached the end of file without finding customer
            return -1;
        } catch (Exception e) {
            System.out.print("Unable to open ATM_accounts ");
            return -2;
        }
    }

    // int withdraw(int customerIndex, char accountType)
    //    where:
    //      customerIndex = customer within the file
    //      accountType:  'C'=checking   'S'=savings
    //    The function requests the amount to withdraw, then
    //      validates that the amount is an even multiple of $20.00
    //      maximum withdrawal is $500.00
    //      reads the customer record and validates sufficient funds
    //      deducts the deposit and updates the customer data file
    static int withdraw(long customerIndex, char accountType) {
        // customer record (acctNo, PIN, chk, sav)
        ATM customer = new ATM();
        RandomAccessFile ATM_file;
        double withdrawAmount;
        double newBalance = 0.0;

        System.out.print("Enter the amount of the withdrawal in increments of $20 up to $500: ");
        withdrawAmount = getDouble();  // check for > 0 after getting current balance

        // compute amount of withdrawal in pennies used to check for increments of $20.00
        int intWithdrawX100 = (int) (withdrawAmount * 100);  // convert to pennies

        if (withdrawAmount <= 0.00)
            System.out.print("Withdrawal must be greater than zero\n\n");
        else if (withdrawAmount > 500.00)
            System.out.print("Withdrawal must not exceed $500.00\n\n");
        else if (intWithdrawX100 % 2000 != 0)  // 2000 = $20 in pennies, 
            System.out.print("Withdrawal must be in increments of $20.00\n\n");
        else // proceed with withdrawal
        {
            try {
                // open the customer file in both read and write mode
                ATM_file = new RandomAccessFile(ATM_FILENAME, "rw");
                // seek to the selected customer record
                ATM_file.seek(customerIndex * ATM.ATM_RECORD_SIZE);
                // read customer record
                customer.setAcctNo(ATM_file.readInt());
                customer.setPIN(ATM_file.readInt());
                customer.setChecking(ATM_file.readDouble());
                customer.setSavings(ATM_file.readDouble());
                // update the balance for the selected account type
                if (accountType == 'C') {   // update the customer's checking balance
                    newBalance = customer.getChecking() - withdrawAmount;
                    customer.setChecking(newBalance);
                } else if (accountType == 'S') { // update customer's savings balance
                    newBalance = customer.getSavings() - withdrawAmount;
                    customer.setSavings(newBalance);
                }
                // seek back to the same record and write the updated record back to disk
                // seek to the selected customer record
                ATM_file.seek(customerIndex * ATM.ATM_RECORD_SIZE);
                ATM_file.writeInt(customer.getAcctNo());
                ATM_file.writeInt(customer.getPIN());
                ATM_file.writeDouble(customer.getChecking());
                ATM_file.writeDouble(customer.getSavings());
                ATM_file.close(); // close the file

                // display the updated balance
                System.out.printf("Your balance is $%.2f\n", newBalance);
            } // end try block
            catch (EOFException e) { // reached the end of file without finding customer
                return -1;
            } catch (Exception e) {
                System.out.print("Unable to open ATM_accounts ");
                return -2;
            }
        } // end of processing the withdrawal
        return 0;   // no error

    } // end of the withdraw() method


    ////////////////////////////////////////////////////////////
    // void deposit(int customerIndex, char accountType)
    //    where:
    //      customerIndex = customer within the file
    //      accountType:  'C'=checking   'S'=savings
    //    The function requests the amount to deposit, then
    //      validates that the amount is greater than zero
    //      reads the customer record and adds the deposit
    //    ` updates the customer data file
    static int deposit(long customerIndex, char accountType) {
        // customer record (acctNo, PIN, chk, sav)
        ATM customer = new ATM();
        RandomAccessFile ATM_file;
        double depositAmount;
        double newBalance = 0.0;

        System.out.print("Enter the amount of the deposit: ");
        depositAmount = getDouble();  // check for > 0 after getting current balance

        if (depositAmount <= 0.00)
            System.out.print("Deposit must be greater than zero\n\n");
        else // proceed with deposit
        {
            try {
                // open the customer file in both read and write mode
                ATM_file = new RandomAccessFile(ATM_FILENAME, "rw");
                // seek to the selected customer record
                ATM_file.seek(customerIndex * ATM.ATM_RECORD_SIZE);
                // read customer record
                customer.setAcctNo(ATM_file.readInt());
                customer.setPIN(ATM_file.readInt());
                customer.setChecking(ATM_file.readDouble());
                customer.setSavings(ATM_file.readDouble());
                // update the balance for the selected account type
                if (accountType == 'C') {   // update the customer's checking balance
                    newBalance = customer.getChecking() + depositAmount;
                    customer.setChecking(newBalance);
                } else if (accountType == 'S') { // update customer's savings balance
                    newBalance = customer.getSavings() + depositAmount;
                    customer.setSavings(newBalance);
                }
                // seek back to the same record and write the updated record back to disk
                // seek to the selected customer record
                ATM_file.seek(customerIndex * ATM.ATM_RECORD_SIZE);
                ATM_file.writeInt(customer.getAcctNo());
                ATM_file.writeInt(customer.getPIN());
                ATM_file.writeDouble(customer.getChecking());
                ATM_file.writeDouble(customer.getSavings());
                ATM_file.close(); // close the file

                // display the updated balance
                System.out.printf("Your balance is $%.2f\n", newBalance);
            } // end try block
            catch (EOFException e) { // reached the end of file without finding customer
                return -1;
            } catch (Exception e) {
                System.out.print("Unable to open ATM_accounts ");
                return -2;
            }
        } // end of processing the withdrawal
        return 0;   // no error
    }


    ////////////////////////////////////////////////////////////    
    //// The following methods read from the keyboard and 
    ////   verify that valid data was entered
    ////////////////////////////////////////////////////////////    
    // char getChar()
    //  reads a line of text from standard input (keyboard)
    //  returns the first character that was input
    //  the rest of the line is discarded
    static char getChar() {
        return (stdin.next().toUpperCase().charAt(0));
    }

    // int getInt()
    //  reads an integer from standard input (keyboard)
    //  returns the first character that was input
    //  displays an error message and tries again if a non-integer is input
    static int getInt() {
        int result = 0;
        boolean tryAgain;
        do {
            tryAgain = false;
            try {
                result = stdin.nextInt();
            } catch (Exception e) {
                System.out.print("*** Illegal entry. Enter an integer. Try again\n? ");
                stdin.next();  // clear the input buffer
                tryAgain = true;
            }
        } while (tryAgain);
        return result;
    }

    // double getDouble()
    //  reads a real number from standard input (keyboard)
    //  returns the first character that was input
    //  displays an error message and tries again if an error is detected
    static double getDouble() {
        double result = 0.0;
        boolean tryAgain;

        do {
            tryAgain = false;
            try {
                result = stdin.nextDouble();
            } catch (Exception e) {
                System.out.print("*** Illegal entry. Enter an number. Try again\n? ");
                tryAgain = true;
            }
        } while (tryAgain);
        return result;
    }
}

