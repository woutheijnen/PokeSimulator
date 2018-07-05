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
public class Type {
    private int id = 0;
    private String name = "";
    private ArrayList<int[]> matchup = new ArrayList<>();

    public int getMatchup(int against) {
        return matchup.get(against-1)[1];
    }

    public void setMatchup(int against, int modifier) {
        int[] m = {against, modifier};
        matchup.add(m);
    }

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
}
