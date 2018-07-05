/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokesimulator;

import java.util.ArrayList;

/**
 *
 * @author Wout
 */
public class GeneratorPart {
    private Specie specie = null;
    private int probability = 0;
    private ArrayList<Object[]> abilities = new ArrayList<>();
    private ArrayList<Object[]> items = new ArrayList<>();

    public Specie getSpecie() {
        return specie;
    }

    public void setSpecie(Specie specie) {
        this.specie = specie;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }
    
    public boolean addAbility(String ability, int probability)
    {
        for(int i=0; i<specie.getAbilities().size(); i++)
        {
            if(specie.getAbilities().get(i).toLowerCase().equals(ability.toLowerCase()))
            {
                Object[] toAdd = {specie.getAbilities().get(i), probability};
                abilities.add(toAdd);
                return true;
            }
        }
        return false;
    }
    
    public void addItem(String item, int probability)
    {
        if(!item.equals("Other"))
        {
            Object[] toAdd = {item, probability};
            abilities.add(toAdd);
        }
    }
}
