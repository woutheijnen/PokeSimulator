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
public class Nature {
    private int id = 0;
    private String name = "";
    private int increasedStat = 0;
    private int decreasedStat = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIncreasedStat() {
        return increasedStat;
    }

    public void setIncreasedStat(int increasedStat) {
        this.increasedStat = increasedStat;
    }

    public int getDecreasedStat() {
        return decreasedStat;
    }

    public void setDecreasedStat(int decreasedStat) {
        this.decreasedStat = decreasedStat;
    }
}
