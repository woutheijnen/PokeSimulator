/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokesimulator;

import static java.lang.Math.floor;
import static java.lang.Math.round;
import java.util.ArrayList;

/**
 *
 * @author Wout
 */
public class Pokemon {
    private int level = 100;
    private Specie specie = null;
    private Nature nature = null;
    private Item holdItem = null;
    private String ability = "";
    private int currentHP = 0;
    private int[] iv = {31,31,31,31,31,31};
    private int[] ev = {0,0,0,0,0,0};
    private int[] modifiers = {0,0,0,0,0,0,0,0,0}; //0HP, 1ATT, 2DEF, 3SPA, 4SPD, 5SPE, 6ACC, 7EVA, 8CRI
    private Move[] learnedMoves = {null,null,null,null};
    private int[] currentPP = {0, 0, 0, 0};
    private String status = "";
    private ArrayList<String> pseudoStatus = new ArrayList<>();
    private int[] timers = {0,0}; //0: Sleep 1: Badly PSN
    private int charging = -1; //For moves that need charging like Razor Wind
    private boolean recharge = false; //For moves that needs to recharge like Hyper Beam
    
    public void reset() {
        for(int i=0; i<modifiers.length; i++)
            modifiers[i] = 0;
        pseudoStatus.clear();
        for(int i=0; i<timers.length; i++)
            timers[i] = 0;
        charging = -1;
        recharge = false;
    }
    
    public boolean hasPseudoStatus(String ps) {
        return pseudoStatus.contains(ps);
    }
    
    public boolean addPseudoStatus(String ps)
    {
        if(ps.equals("trap"))
        {
            if(specie.getType1().getName().equals("Ghost"))
                return false;
            if(specie.getType2() != null)
            {
                if(specie.getType2().getName().equals("Ghost"))
                    return false;
            }
        }
        
        if(ps.equals("nightmare"))
        {
            if(!status.equals("sleep"))
                return false;
        }
        
        if(ps.equals("leech-seed"))
        {
            if(specie.getType1().getName().equals("Grass"))
                return false;
            if(specie.getType2() != null)
            {
                if(specie.getType2().getName().equals("Grass"))
                    return false;
            }
        }
        
        if(!pseudoStatus.contains(ps))
        {
            pseudoStatus.add(ps);
            return true;
        }
        else
            return false;
    }
    
    public boolean mustRecharge() {
        return recharge;
    }
    
    public void setRecharge(boolean value) {
        recharge = value;
    }
    
    public void restorePP() {
        for(int i=0; i<currentPP.length; i++)
        {
            if(learnedMoves[i] != null)
                currentPP[i] = learnedMoves[i].getPp();
        }
    }
    
    public int getPP(int moveID) {
        return currentPP[moveID];
    }
    
    public void deducePP(int moveID) {
        currentPP[moveID] --;
    }
    
    //0HP, 1ATT, 2DEF, 3SPA, 4SPD, 5SPE
    public int getStat(int i, boolean applyModifiers)
    {
        int base = specie.getBaseStat(i);
        
        if(i == 0)
        {
            if(specie.getName().equals("Shedinja"))
                return 1;
            else
                return (int) floor((((iv[0] + 2 * base + (ev[0]/4)) + 100) * level)/100 + 10);
        }
        
        double natureMod = 1.0;
        if(nature.getIncreasedStat() == i)
            natureMod += 0.1;
        if(nature.getDecreasedStat() == i)
            natureMod -= 0.1;
            
        int stat = (int) floor(floor((((iv[i] + 2 * base + (ev[i]/4)) * level)/100) +5) * natureMod);
        if(i == 5 && status.equals("Paralyzed"))
            stat /= 4;
        
        double mod = 1.0;
        if(applyModifiers)
        {
            if(modifiers[i] >= 0)
            {
                mod = 2.0 + modifiers[i];
                mod /= 2.0;
            }
            else
            {
                mod = 2.0;
                mod /= 2.0 + (-1.0 * modifiers[i]);
            }
        }
        
        return (int) floor(stat * mod);
    }

    public int getCharging() {
        return charging;
    }

    public void setCharging(int charging) {
        this.charging = charging;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Specie getSpecie() {
        return specie;
    }

    public void setSpecie(Specie specie) {
        this.specie = specie;
    }

    public Nature getNature() {
        return nature;
    }

    public void setNature(Nature nature) {
        this.nature = nature;
    }

    public Item getHoldItem() {
        return holdItem;
    }

    public void setHoldItem(Item holdItem) {
        this.holdItem = holdItem;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void heal(double percentage) {
        int maxHP = getStat(0, false);
        currentHP += (int) round(maxHP * percentage / 100.0);
        if(currentHP > maxHP)
            currentHP = maxHP;
    }
    
    public void doDamage(int amount) {
        currentHP -= amount;
        if(currentHP < 0)
            currentHP = 0;
    }
    
    public void doDamagePercentage(double percentage) {
        int maxHP = getStat(0, false);
        currentHP -= (int) round(maxHP * percentage / 100.0);
        if(currentHP < 0)
            currentHP = 0;
    }

    public int[] getIv() {
        return iv;
    }

    public void setIv(int[] iv) {
        this.iv = iv;
    }

    public int[] getEv() {
        return ev;
    }

    public void setEv(int[] ev) {
        this.ev = ev;
    }

    public int[] getModifiers() {
        return modifiers;
    }

    public void setModifiers(int[] modifiers) {
        this.modifiers = modifiers;
    }

    public Move[] getLearnedMoves() {
        return learnedMoves;
    }

    public void setLearnedMoves(Move[] learnedMoves) {
        this.learnedMoves = learnedMoves;
    }

    public String getStatus() {
        return status;
    }

    public boolean setStatus(String status) {
        if(status.equals("paralysis"))
        {
            if(specie.getType1().getName().equals("Electric"))
                return false;
            if(specie.getType2() != null)
            {
                if(specie.getType2().getName().equals("Electric"))
                    return false;
            }
        }
        
        if(status.equals("freeze"))
        {
            if(specie.getType1().getName().equals("Ice"))
                return false;
            if(specie.getType2() != null)
            {
                if(specie.getType2().getName().equals("Ice"))
                    return false;
            }
        }
        
        if(status.equals("burn"))
        {
            if(specie.getType1().getName().equals("Fire"))
                return false;
            if(specie.getType2() != null)
            {
                if(specie.getType2().getName().equals("Fire"))
                    return false;
            }
        }
        
        if(status.equals("poison"))
        {
            if(specie.getType1().getName().equals("Poison") || specie.getType1().getName().equals("Steel"))
                return false;
            if(specie.getType2() != null)
            {
                if(specie.getType2().getName().equals("Poison") || specie.getType2().getName().equals("Steel"))
                    return false;
            }
        }

        if(this.status.isEmpty())
        {
            this.status = status;
            return true;
        }
        else
            return false;
    }

    public int[] getTimers() {
        return timers;
    }

    public void setTimers(int[] timers) {
        this.timers = timers;
    }
    
    public void printPokemon() {
        String string = "#" + specie.getSpecieId() + " " + this.nature.getName() + " " + specie.getName() + " with " + this.ability + " Health: " + this.currentHP + "/" + this.getStat(0, false) + "  -  Moves: ";
        String[] stats = {"HP", "Atk", "Def", "SpA", "SpD", "Spe"};
        
        for(int i=0; i<4; i++)
        {
            if(learnedMoves[i] != null)
                string += learnedMoves[i].getName();
            else
                string += "none";
            if(i < 3)
                string += ", ";
            else
                string += "  -  Stats: ";
        }
        
        for(int i=0; i<6; i++)
        {
            string += stats[i] + ": " + this.getStat(i, true);
            if(i < 5)
                string += ", ";
        }
        
        System.out.println(string);
    }
}