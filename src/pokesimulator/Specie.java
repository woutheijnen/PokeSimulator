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
public class Specie {
    private int id = 0;
    private int specieId = 0;
    private String name = "";
    private int height = 0;
    private int weight = 0;
    private ArrayList<String> abilities = new ArrayList<>();
    private boolean isFullyEvolved = true;
    private int[] baseStat = {0, 0, 0, 0, 0, 0};
    private Type type1 = null;
    private Type type2 = null;
    private ArrayList<Move> possibleMoves = new ArrayList<>();
    private int form_is_default = 1;
    private int form_is_battle_only = 0;
    private int form_is_mega = 0;

    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public void addPossibleMove(Move move) {
        if(!possibleMoves.contains(move))
            possibleMoves.add(move);
    }

    public Type getType1() {
        return type1;
    }

    public void setType1(Type type1) {
        this.type1 = type1;
    }

    public Type getType2() {
        return type2;
    }

    public void setType2(Type type2) {
        this.type2 = type2;
    }

    public int[] getBaseStats() {
        return baseStat;
    }
    
    public int getBaseStat(int i) {
        return baseStat[i];
    }

    public void setBaseStat(int i, int amount) {
        this.baseStat[i] = amount;
    }

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public boolean isIsFullyEvolved() {
        return isFullyEvolved;
    }

    public void setIsFullyEvolved(boolean isFullyEvolved) {
        this.isFullyEvolved = isFullyEvolved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpecieId() {
        return specieId;
    }

    public void setSpecieId(int specieId) {
        this.specieId = specieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public boolean addAbility(String toAdd)
    {
        return abilities.add(toAdd);
    }

    public int getForm_is_default() {
        return form_is_default;
    }

    public void setForm_is_default(int form_is_default) {
        this.form_is_default = form_is_default;
    }

    public int getForm_is_battle_only() {
        return form_is_battle_only;
    }

    public void setForm_is_battle_only(int form_is_battle_only) {
        this.form_is_battle_only = form_is_battle_only;
    }

    public int getForm_is_mega() {
        return form_is_mega;
    }

    public void setForm_is_mega(int form_is_mega) {
        this.form_is_mega = form_is_mega;
    }
}
