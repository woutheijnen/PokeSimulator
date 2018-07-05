/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokesimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Wout
 */
public class TeamGenerator {
    private ArrayList<GeneratorPart> data = new ArrayList<>();
    private Engine engine = null;
    
    public boolean load(Engine e, String path) throws FileNotFoundException, IOException
    {
        engine = e;
        
        //Load the data from Smogon stat file for team generation
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        Boolean newPart = true;
        GeneratorPart p = null;
        
        Boolean receivingPokemon = false;
        Boolean receivingAbilities = false;
        Boolean receivingItems = false;
        Boolean receivingSpreads = false;
        Boolean receivingMoves = false;
        Boolean receivingTeammates = false;
        
        while ((line = br.readLine()) != null) {
            //Create new instance for storing the data
            if(newPart)
            {
                p = new GeneratorPart();
                receivingPokemon = true;
            }
            
            //If a receiver is open, execute until stop condition is met
            if(receivingPokemon)
            {
                if(!line.contains("+----------------------------------------+"))
                {
                    p.setSpecie(getSpecieFromName(line));
                    receivingPokemon = false;
                }
            }
            
            if(line.contains("Raw count"))
            {
                line = line.replaceAll("Raw count:", "").replaceAll("|", "").replaceAll(" ", "");
                p.setProbability(Integer.parseInt(line));
            }
            
            //TODO: Different spaces between ability/item and probability are possible, should check differently
            if(receivingAbilities || receivingItems)
            {
                line = line.replaceAll("  ", "").replaceAll(" | ", "").replaceAll("| ", "").replaceAll("| ", "");
                String[] chopped = line.split(" ");
                //If the ability had a space...
                if(chopped.length>2)
                {
                    for(int i=1; i<chopped.length-2; i++)
                    {
                        chopped[0] += " " + chopped[i];
                    }
                }
                //Clean the probability for conversion
                chopped[chopped.length-1] = chopped[chopped.length-1].replaceAll(".", "").replaceAll("%", "");
                
                if(receivingAbilities)
                {
                    if(!p.addAbility(chopped[0], Integer.parseInt(chopped[chopped.length-1])))
                    {
                        System.err.println("Error ability not found for: " + chopped[0]);
                        System.exit(0);
                    }
                }
                else
                {
                    p.addItem(chopped[0], Integer.parseInt(chopped[chopped.length-1]));
                }
            }
            
            //Enable a receiver if a condition has been met
            if(line.contains("Abilities"))
                receivingAbilities = true;
            if(line.contains("Items"))
                receivingItems = true;
            if(line.contains("Spreads"))
                receivingSpreads = true;
            if(line.contains("Moves"))
                receivingMoves = true;
            if(line.contains("Teammates"))
                receivingTeammates = true;
            if(line.contains("Checks and Counters"))
            {
                //Store and create new data part
                data.add(p);
                newPart = true;
            }
            
            //Desactivate receivers if stop condition has been met
            if(receivingAbilities || receivingItems || receivingSpreads || receivingMoves || receivingTeammates)
            {
                if(line.contains("+----------------------------------------+"))
                {
                    receivingAbilities = false;
                    receivingItems = false;
                    receivingSpreads = false;
                    receivingMoves = false;
                    receivingTeammates = false;
                }
            }
        }
        return true;
    }
    
    private Specie getSpecieFromName(String name)
    {
        //Cleaning the line
        name = name.replaceAll("  ", "").replaceAll(" | ", "").replaceAll("| ", "").replaceAll("| ", "");
        
        //Finding the specie object
        ArrayList<Specie> specieDB = engine.getSpecies();
        for(int i=0; i<specieDB.size(); i++)
        {
            if(specieDB.get(i).getName().toLowerCase().equals(name.toLowerCase()))
                return specieDB.get(i);
        }
        
        System.err.println("Error specie not found for: " + name);
        System.exit(0);
        
        return null;
    }
}
