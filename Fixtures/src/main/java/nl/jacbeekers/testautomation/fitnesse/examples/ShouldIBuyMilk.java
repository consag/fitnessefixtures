/**
 * This fixture was copied from fitnesse.org, to explain how a FitNesse slim decision table works.
 * @author Edward Crain
 * @version 11 October 2014
 */ 
package nl.jacbeekers.testautomation.fitnesse.examples;
  
public class ShouldIBuyMilk { 
    private int dollars; 
      private int pints; 
      private boolean creditCard; 
  
      public void setCashInWallet(int dollars) { 
        this.dollars = dollars; 
      } 
  
      public void setPintsOfMilkRemaining(int pints) { 
        this.pints = pints; 
      } 
  
      public void setCreditCard(String valid) { 
        creditCard = "yes".equals(valid); 
      } 
  
      public String goToStore() { 
        if ((pints == 0 && (dollars > 2 || creditCard))) 
            return "yes"; 
        else
            return "no";  
      } 
} 