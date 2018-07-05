/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokesimulator;

/**
 *
 * @author Wout
 */
public class PokeSimulator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Engine e = new Engine();
        if(!e.loadData())
        {
            System.err.println("Error during loading engine data! Aborting.");
            System.exit(0);
        }
        do{
            if(!e.doSingleBattle())
            {
                System.err.println("Error during single battle! Aborting.");
                System.exit(0);
            }
        }while(false);
        //e.countTest();
    }
}
