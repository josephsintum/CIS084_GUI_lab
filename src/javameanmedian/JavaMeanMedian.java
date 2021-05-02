// Program Name: JavaMeanMedia -- compute mean and median of a disk file
// Programmer:   Sintumnyuy Joseph
// Class:        CIS-084 Java Programming
// Date:         May 02, 2021
// Version:      1.0

package javameanmedian;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class JavaMeanMedian {
    static final String FILEPATH = "Desktop";   //path name where file is located

    public static void main(String[] args) {
        // variables to control the disk file
        Scanner infile;         // input file object
        String filename;        // name of the file
        int recordCount = 0;   // number of records (lines) in the file
        int recordsToSkip = 0; // used when computing the median
        // variables for fields of each record in the file
        String lineFromFile;    // used when reading a line at a time from file
        int acctNo = 0;      // account number from a record in the file
        String customer = "";   // customer name from a record in the file
        double acctBal = 0.0;   // account balance from a record in the file
        // varible used to determine the mean and median
        double total = 0;       // used when computing the mean
        double mean = 0;         // mean (average) of all account balances
        double median = 0.0;    // median value of account balances

        // create the Scanner objects for the keyboard and disk access
        Scanner stdin = new Scanner(System.in);

        // get just the name of the file from the keyboard
        // build the pathname and filename
        // identify the folder where the file is located
        // The User name is in the environment variable USER on MacOS and Linux
        //   but it is in the environment variable USERNAME on Windows
        System.out.printf("Enter the name of the data file: ");
        filename = stdin.nextLine();  // read the filename from standard in
        String loginID = System.getenv("USER");  // MacOS or Linux?
        if (loginID == null) loginID = System.getenv("USERNAME"); // Windows?
        String BalancesFileName = "/Users/" + loginID +
                "/" + FILEPATH + "/" + filename;
        try {
            // ---- PART 1, Count the number of records in the file
            //      Determine the mean when you know the 
            //          record count and the total of all balances
            infile = new Scanner(new File(BalancesFileName));

            while (infile.hasNextInt()) // while there is still data in the file
            {
                acctNo = infile.nextInt();      // read int for account number
                customer = infile.next();       // read string for customer name
                acctBal = infile.nextDouble();  // read double for acct balance  
                total += acctBal;
                recordCount++;
            }
            infile.close();
            System.out.printf("%d Records in %s\n", recordCount, filename);
            mean = total / recordCount;

            // ---- PART 2, Determine the number of records to skip
            if (recordCount % 2 == 1) recordsToSkip = recordCount / 2;      // Odd number of records
            else recordsToSkip = recordCount / 2 - 1;  // Even number of records

            // --- PART 3, open the file, skip leading records, determine median
            infile = new Scanner(new File(BalancesFileName));
            while (recordsToSkip-- > 0) infile.nextLine();

            // compute the median
            infile.nextInt();      // read int for account number
            infile.next();       // read string for customer name
            median = infile.nextDouble();  // read double for acct balance

            if (recordCount % 2 == 0) {  // if even number of records
                infile.nextInt();      // read int for account number
                infile.next();       // read string for customer name
                median = (median + infile.nextDouble()) / 2;  // read double for acct balance and take mean of median
            }
            infile.close();

        } // end of try block
        catch (IOException ioe) {
            System.out.println("Exception occured reading Balances.txt");
        }

        // display the results
        System.out.printf("""
                The stats of %s are\s
                Mean:  %.2f
                Median:  %.2f
                Total:  %.2f""", filename, mean, median, total);

    }

}