/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokesimulator;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Wout
 */
public class Trainer {
    private ArrayList<Pokemon> party = new ArrayList<>();
    private String name = "";
    private int activePokemon = 0;
    private boolean player = true;
    private boolean defeated = false;
    
    public int decideSwitch()
    {
        Random r = new Random();
        ArrayList<Integer> availablePokemon = new ArrayList<>();
        for(int i=0; i<this.amountOfPokemon(); i++)
        {
            if(this.getParty().get(i).getCurrentHP() > 0)
                availablePokemon.add(i);
        }
        return availablePokemon.get(r.nextInt(availablePokemon.size()));
    }
    
    public boolean canSwitch(boolean forced) {
        int count = 0;
        if(forced) //If the switch is forced, only 1 switchable pokemon is necessary
            count ++;
        
        for(int i=0; i<party.size(); i++)
        {
            if(party.get(i).getCurrentHP() > 0)
                count ++;
            if(count > 1)
                return true;
        }
        return false;
    }
    
    public boolean hasLost() {
        return defeated;
    }
    
    public void lose() {
        defeated = true;
    }

    public boolean isPlayer() {
        return player;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    public Pokemon getActivePokemon() {
        return party.get(activePokemon);
    }

    public void setActivePokemon(int activePokemon) {
        this.activePokemon = activePokemon;
    }
    
    public boolean setActivePokemon(Pokemon pokemon) {
        this.activePokemon = party.indexOf((Pokemon) pokemon);
        return activePokemon > -1;
    }

    public ArrayList<Pokemon> getParty() {
        return party;
    }
    
    public int amountOfPokemon() {
        return party.size();
    }

    public void addToParty(Pokemon pokemon) {
        if(party.size() < 6)
            party.add(pokemon);
    }
    
    public void removeParty() {
        party.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void printParty() {
        String playerString = "Enemy";
        if(player)
            playerString = "Player";
        
        System.out.println(playerString + " " + name + " has a team consisting of " + this.amountOfPokemon() + " Pokémons:");
        for(int i=0; i<party.size(); i++)
        {
            party.get(i).printPokemon();
        }
        System.out.println("Active Pokémon: #" + (activePokemon+1) + " " + party.get(activePokemon).getSpecie().getName());
        System.out.println();
    }
}
