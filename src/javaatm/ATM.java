package javaatm;
import java.io.Serializable;

public class ATM implements Serializable {
    private int acctNo;
    private int pin;
    private double checking;
    private double savings; 
    
    ATM() { // default constructor
        acctNo = 0;
        pin = 0;
        checking = 0.0;
        savings = 0.0;
    }
    // determine the size in bytes for the data in the ATM_record
    public final static int ATM_RECORD_SIZE = Integer.BYTES * 2 + Double.BYTES * 2;
    
    // setters
    public int setAcctNo (int acctNo) { return this.acctNo = acctNo; }
    public int setPIN (int pin) { return this.pin = pin; }
    public double setChecking (double checking) { return this.checking = checking; }
    public double setSavings (double savings) { return this.savings = savings; }
    
    //getters
    public int getAcctNo () { return acctNo; }
    public int getPIN() { return pin; }
    public double getChecking() { return checking; }
    public double getSavings() { return savings; }    
    
    @Override
    public String toString() {
        return String.format("%7s   %4s   %8s   %8s", acctNo, pin, checking, savings);
    }
}
