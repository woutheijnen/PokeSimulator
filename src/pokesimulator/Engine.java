/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokesimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.sql.*;
import java.util.Random;

/**
 *
 * @author Wout
 */
public class Engine {
    //Allowed movesets from which games?
    private String VERSIONGROUPID = "version_group_id >= 15";
    //Not allowed move methods (Stadium surfing pikachu is unavailable for example)
    private String ILLEGALMOVEMETHOD = "pokemon_move_method_id <> 5";
    
    //Smogon statistics file name to be used for team generation
    private String TEAM_GENERATOR_FILE_NAME = "2016-09-battlespotsingles-1760.txt";
    
    private ArrayList<Specie> species = new ArrayList<>();
    private ArrayList<Type> types = new ArrayList<>();
    private ArrayList<Move> moves = new ArrayList<>();
    private ArrayList<Nature> natures = new ArrayList<>();
    private ArrayList<String> ailments = new ArrayList<>();
    private ArrayList<Specie> evolved = new ArrayList<>();
    //private TeamGenerator teamgen = new TeamGenerator();
    
    private ArrayList<Specie> goodspecies = new ArrayList<>();
    
    private Move struggle = null;
    
    private Connection conn = null;
    private Statement stat = null;
    
    //Only for count
    int qtItems = 0;
    int qtSpreads = 0;

    public boolean loadData()
    {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:veekun-pokedex.sqlite");
            stat = conn.createStatement();

            //Load the species in the list
            ResultSet rs = stat.executeQuery("SELECT pokemon.id, pokemon.species_id, pokemon.identifier, pokemon.height, pokemon.weight, pokemon_forms.is_default, pokemon_forms.is_battle_only, pokemon_forms.is_mega, pokemon_forms.form_identifier, pokemon_forms.form_order FROM pokemon INNER JOIN pokemon_forms ON pokemon.id = pokemon_forms.pokemon_id;");
            while (rs.next()) {
                Specie s = new Specie();
                s.setId(rs.getInt("id"));
                s.setSpecieId(rs.getInt("species_id"));
                s.setForm_is_default(rs.getInt("is_default"));
                s.setForm_is_battle_only(rs.getInt("is_battle_only"));
                s.setForm_is_mega(rs.getInt("is_mega"));
                String name = rs.getString("identifier").substring(0, 1).toUpperCase() + rs.getString("identifier").substring(1);
                if(rs.getInt("form_order") == 1)
                {
                    String formname = rs.getString("form_identifier");
                    if(!rs.wasNull())
                        name = name.replaceAll("-"+formname, "");
                }
                s.setName(name);
                s.setHeight(rs.getInt("height"));
                s.setWeight(rs.getInt("weight"));
                
                if(s.getForm_is_default() == 1)
                    species.add(s);
            }

            //Add abilities
            rs = stat.executeQuery("SELECT pokemon_abilities.pokemon_id, abilities.identifier FROM pokemon_abilities INNER JOIN abilities ON abilities.id=pokemon_abilities.ability_id;");
            while (rs.next()) {
                int search = rs.getInt("pokemon_id");
                for(int i=0; i<species.size(); i++)
                {
                    if(species.get(i).getId() == search)
                    {
                        if(!species.get(i).addAbility(rs.getString("identifier").substring(0, 1).toUpperCase() + rs.getString("identifier").substring(1)))
                        {
                            System.err.println("Error during adding ability data for " + species.get(i).getName() + "! Aborting.");
                            System.exit(0);
                        }
                    }
                }
            }

            //Add data for final evolution
            rs = stat.executeQuery("SELECT evolves_from_species_id FROM pokemon_species WHERE evolves_from_species_id IS NOT NULL;");
            while (rs.next()) {
                int search = rs.getInt("evolves_from_species_id");
                for(int i=0; i<species.size(); i++)
                {
                    if(species.get(i).getSpecieId() == search)
                        species.get(i).setIsFullyEvolved(false);
                }
            }

            //Add stat data
            rs = stat.executeQuery("SELECT pokemon_id, stat_id, base_stat FROM pokemon_stats;");
            while (rs.next()) {
                int search = rs.getInt("pokemon_id");
                for(int i=0; i<species.size(); i++)
                {
                    if(species.get(i).getId() == search)
                        species.get(i).setBaseStat(rs.getInt("stat_id")-1, rs.getInt("base_stat"));
                }
            }
            
            //Create Types database
            rs = stat.executeQuery("SELECT id, identifier FROM types;");
            while (rs.next()) {
                Type t = new Type();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("identifier").substring(0, 1).toUpperCase() + rs.getString("identifier").substring(1));
                types.add(t);
            }
            
            //Create Types efficacy system
            rs = stat.executeQuery("SELECT * FROM type_efficacy;");
            while (rs.next()) {
                int search = rs.getInt("damage_type_id");
                for(int i=0; i<types.size(); i++)
                {
                    if(types.get(i).getId() == search)
                        types.get(i).setMatchup(rs.getInt("target_type_id"), rs.getInt("damage_factor"));
                }
            }
            
            //Add types to species
            rs = stat.executeQuery("SELECT * FROM pokemon_types;");
            while (rs.next()) {
                int search = rs.getInt("pokemon_id");
                for(int i=0; i<species.size(); i++)
                {
                    if(species.get(i).getId() == search)
                    {
                        Type temp = null;
                        for(int j=0; j<types.size(); j++)
                        {
                            if(types.get(j).getId() == rs.getInt("type_id"))
                            {
                                temp = types.get(j);
                                break;
                            }
                        }
                        if(rs.getInt("slot") == 1)
                            species.get(i).setType1(temp);
                        else
                            species.get(i).setType2(temp);
                        break;
                    }
                }
            }
            
            //Create Moves database
            rs = stat.executeQuery("SELECT id, identifier, type_id, power, pp, accuracy, priority, target_id, damage_class_id, effect_id, effect_chance FROM moves;");
            while (rs.next()) {
                Move m = new Move();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("identifier").substring(0, 1).toUpperCase() + rs.getString("identifier").substring(1));
                
                Type temp = null;
                for(int i=0; i<types.size(); i++)
                {
                    if(types.get(i).getId() == rs.getInt("type_id"))
                    {
                        temp = types.get(i);
                        break;
                    }
                }
                m.setType(temp);
                
                m.setPower(rs.getInt("power"));
                m.setPp(rs.getInt("pp"));
                m.setAccuracy(rs.getInt("accuracy"));
                m.setPriority(rs.getInt("priority"));
                m.setTarget(rs.getInt("target_id"));
                m.setDamageClass(rs.getInt("damage_class_id"));
                m.setEffect(rs.getInt("effect_id"));
                m.setEffectChance(rs.getInt("effect_chance"));
                moves.add(m);
            }
            
            //Add move flags
            rs = stat.executeQuery("SELECT * FROM move_flag_map;");
            while (rs.next()) {
                int search = rs.getInt("move_id");
                for(int i=0; i<moves.size(); i++)
                {
                    if(moves.get(i).getId() == search)
                    {
                        moves.get(i).setFlag(rs.getInt("move_flag_id"), true);
                        break;
                    }
                }
            }
            
            //Create Ailments database
            int idAilment=0;
            rs = stat.executeQuery("SELECT id, identifier FROM move_meta_ailments WHERE id >= 0;");
            while (rs.next()) {
                int databaseID = 0;
                do {
                    String a = "";
                    databaseID = rs.getInt("id");
                    //If the ailment ID is not valid, use an empty one
                    if(idAilment == databaseID)
                        a = rs.getString("identifier");
                    ailments.add(a);
                    idAilment ++;
                }while((idAilment -1) != databaseID);
            }
            
            //Add move meta data
            rs = stat.executeQuery("SELECT * FROM move_meta;");
            while (rs.next()) {
                int search = rs.getInt("move_id");
                for(int i=0; i<moves.size(); i++)
                {
                    if(moves.get(i).getId() == search)
                    {
                        moves.get(i).setMetaCategory(rs.getInt("meta_category_id"));
                        int ailID = rs.getInt("meta_ailment_id");
                        if(ailID >= 0)
                            moves.get(i).setMetaAilment(ailments.get(rs.getInt("meta_ailment_id")));
                        else
                            moves.get(i).setMetaAilment("unknown");
                        int temp = rs.getInt("min_hits");
                        if(!rs.wasNull())
                            moves.get(i).setMinHits(temp);
                        temp = rs.getInt("max_hits");
                        if(!rs.wasNull())
                            moves.get(i).setMaxHits(temp);
                        temp = rs.getInt("min_turns");
                        if(!rs.wasNull())
                            moves.get(i).setMinTurns(temp);
                        temp = rs.getInt("max_turns");
                        if(!rs.wasNull())
                            moves.get(i).setMaxTurns(temp);
                        moves.get(i).setDrain(rs.getInt("drain"));
                        moves.get(i).setHealing(rs.getInt("healing"));
                        moves.get(i).setCriticalRate(rs.getInt("crit_rate"));
                        moves.get(i).setAilmentChance(rs.getInt("ailment_chance"));
                        moves.get(i).setFlinchChance(rs.getInt("flinch_chance"));
                        moves.get(i).setStatChance(rs.getInt("stat_chance"));
                        break;
                    }
                }
            }
            
            //Add move meta stat changes
            rs = stat.executeQuery("SELECT * FROM move_meta_stat_changes;");
            while (rs.next()) {
                int search = rs.getInt("move_id");
                for(int i=0; i<moves.size(); i++)
                {
                    if(moves.get(i).getId() == search)
                    {
                        moves.get(i).addStatChange(rs.getInt("stat_id"), rs.getInt("change"));
                        break;
                    }
                }
            }
            
            //Add moves to each specie
            rs = stat.executeQuery("SELECT pokemon_id, move_id FROM pokemon_moves WHERE " + VERSIONGROUPID + " AND " + ILLEGALMOVEMETHOD + ";");
            while (rs.next()) {
                int search = rs.getInt("pokemon_id");                
                for(int i=0; i<species.size(); i++)
                {
                    if(species.get(i).getId() == search)
                    {
                        Move temp = null;
                        for(int j=0; j<moves.size(); j++)
                        {
                            if(moves.get(j).getId() == rs.getInt("move_id"))
                            {
                                temp = moves.get(j);
                                break;
                            }
                        }
                        
                        if(temp != null)
                            species.get(i).addPossibleMove(temp);
                        break;
                    }
                }
            }
            
            //Create Nature database
            rs = stat.executeQuery("SELECT id, identifier, decreased_stat_id, increased_stat_id FROM natures;");
            while (rs.next()) {
                Nature n = new Nature();
                n.setId(rs.getInt("id"));
                n.setName(rs.getString("identifier").substring(0, 1).toUpperCase() + rs.getString("identifier").substring(1));
                n.setIncreasedStat(rs.getInt("increased_stat_id")-1);
                n.setDecreasedStat(rs.getInt("decreased_stat_id")-1);
                
                natures.add(n);
            }
            
            //Finish the database connection as it is no longer needed (everything is in memory)
            rs.close();
            conn.close();
            
            //Struggle move data shortcut
            for(int i=0; i<moves.size(); i++)
            {
                if(moves.get(i).getName().equals("Struggle"))
                {
                    struggle = moves.get(i);
                    break;
                }
            }
            
            //Fully evolved pokemon data
            for(int i=0; i<species.size(); i++)
            {
                if(species.get(i).isIsFullyEvolved() && species.get(i).getForm_is_battle_only() == 0)
                    evolved.add(species.get(i));
            }
            
            //Generate species with good stats
            //First collect all good species, good abilities, good items, good moves and good evs
            //Then add them to each good specie if they can have (and should have) these values
            File file = new File(TEAM_GENERATOR_FILE_NAME);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            Boolean receivingPokemon = true;
            Boolean receivingAbilities = false;
            Boolean receivingItems = false;
            Boolean receivingSpreads = false;
            Boolean receivingMoves = false;
            Boolean receivingTeammates = false;
            ArrayList<String> temp_species = new ArrayList<>();
            ArrayList<String> temp_abilities = new ArrayList<>();
            ArrayList<String> temp_items = new ArrayList<>();
            ArrayList<String> temp_spreads = new ArrayList<>();
            ArrayList<String> temp_moves = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                //If a receiver is open, execute until stop condition is met
                if(receivingPokemon)
                {
                    if(!line.contains("+----------------------------------------+"))
                    {
                        line = line.replaceAll(" ", "");
                        if(!temp_species.contains(line))
                            temp_species.add(line);
                        receivingPokemon = false;
                    }
                }

                //TODO: Different spaces between ability/item and probability are possible, should check differently
                if(receivingAbilities || receivingItems || receivingMoves)
                {
                    //CAREFUL: If it naturally contains a number or dot, it will delete it!!
                    if(!line.contains("Other") && !line.contains("+----------------------------------------+"))
                    {
                        line = line.replaceAll(" ", "").replaceAll("0", "").replaceAll("1", "").replaceAll("2", "").replaceAll("3", "").replaceAll("4", "").replaceAll("5", "").replaceAll("6", "").replaceAll("7", "").replaceAll("8", "").replaceAll("9", "");
                        if(receivingMoves && !temp_moves.contains(line))
                            temp_moves.add(line);
                        else
                            if(receivingAbilities && !temp_abilities.contains(line))
                                temp_abilities.add(line);
                            else
                            {
                                if(receivingItems && !temp_items.contains(line))
                                temp_items.add(line);
                            }
                    }
                }
                
                if(receivingSpreads)
                {
                    if(!line.contains("Other") && !line.contains("+----------------------------------------+"))
                    {
                        String[] temp = line.split(" ");
                        temp_spreads.add(temp[2]);
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
                    receivingPokemon = true;

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
            
            //Now generate the optimized good specie objects from the strings
            for(int i=0; i<temp_species.size(); i++)
            {
                String s = temp_species.get(i).toLowerCase();
                for(int j=0; j<species.size(); j++)
                {
                    String pkname = species.get(j).getName().toLowerCase();
                    if(pkname.equals(s))
                    {
                        //Copy values from a specie to a new specie...
                        Specie to = new Specie();
                        Specie from = species.get(j);
                        for(int k=0; k<temp_abilities.size(); k++)
                        {
                            for(int l=0; l<from.getAbilities().size(); l++)
                            {
                                if(from.getAbilities().get(l).toLowerCase().replaceAll(" ", "").replaceAll("-", "").equals(temp_abilities.get(k).toLowerCase()))
                                    to.addAbility(from.getAbilities().get(l));
                            }
                        }
                        for(int k=0; k<temp_moves.size(); k++)
                        {
                            for(int l=0; l<from.getPossibleMoves().size(); l++)
                            {
                                if(from.getPossibleMoves().get(l).getName().toLowerCase().replaceAll(" ", "").replaceAll("-", "").equals(temp_moves.get(k).toLowerCase()))
                                    to.addPossibleMove(from.getPossibleMoves().get(l));
                            }
                        }
                        int[] stats = from.getBaseStats();
                        for(int k=0; k<stats.length; k++)
                            to.setBaseStat(k, stats[k]);
                        to.setForm_is_battle_only(from.getForm_is_battle_only());
                        to.setForm_is_default(from.getForm_is_default());
                        to.setForm_is_mega(from.getForm_is_mega());
                        to.setHeight(from.getHeight());
                        to.setId(from.getId());
                        to.setIsFullyEvolved(from.isIsFullyEvolved());
                        to.setName(from.getName());
                        to.setSpecieId(from.getSpecieId());
                        to.setType1(from.getType1());
                        to.setType2(from.getType2());
                        to.setWeight(from.getWeight());
                        goodspecies.add(to);
                    }
                }
            }
            qtItems = temp_items.size();
            qtSpreads = temp_spreads.size();
            
            //teamgen.load(this, TEAM_GENERATOR_FILE_NAME);
            
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean doSingleBattle()
    {
        Trainer ash = new Trainer();
        Trainer gary = new Trainer();
        ash.setName("Ash");
        gary.setName("Gary");
        ash.setPlayer(true);
        gary.setPlayer(false);
        generateTeam(ash, 3);
        generateTeam(gary, 3);
        
        ash.printParty();
        gary.printParty();
        
        SingleBattle sb = new SingleBattle(this);
        return sb.startBattle(ash, gary, false);
    }
    
    public Move getStruggleData() {
        return struggle;
    }
    
    private void generateTeam(Trainer forTrainer, int partySize) {
        forTrainer.removeParty();
        
        for(int i=0; forTrainer.amountOfPokemon()<partySize; i++)
        {
            Random r = new Random();
            Pokemon p = new Pokemon();
            boolean valid;
            do{
                p.setSpecie(evolved.get(r.nextInt(evolved.size())));
                valid = true;
                for(int j=0; j<forTrainer.amountOfPokemon(); j++)
                {
                    if(forTrainer.getParty().get(j).getSpecie().getSpecieId() == p.getSpecie().getSpecieId())
                        valid = false;
                }
            }while(!valid);
            p.setAbility(p.getSpecie().getAbilities().get(r.nextInt(p.getSpecie().getAbilities().size())));
            
            int[] ev = {0, 0, 0, 0, 0, 0};
            do{
                if(ev[0] + ev[1] + ev[2] + ev[3] + ev[4] + ev[5] < 504)
                    ev[r.nextInt(6)] = 252;
                if((ev[0] + ev[1] + ev[2] + ev[3] + ev[4] + ev[5]) >= 504 && (ev[0] + ev[1] + ev[2] + ev[3] + ev[4] + ev[5]) < 510)
                {
                    int stat = r.nextInt(6);
                    if(ev[stat] == 0)
                        ev[stat] += 6;
                }
            }while((ev[0] + ev[1] + ev[2] + ev[3] + ev[4] + ev[5]) != 510);
            p.setEv(ev);
            
            Move[] learnedMoves = {null, null, null, null};
            if(p.getSpecie().getPossibleMoves().size() >= 4)
            {
                for(int j=0; j<4; j++)
                {
                    do{
                        learnedMoves[j] = p.getSpecie().getPossibleMoves().get(r.nextInt(p.getSpecie().getPossibleMoves().size()));
                        valid = true;
                        for(int k=0; k<4; k++)
                        {
                            if(learnedMoves[k] != null)
                            {
                                if((learnedMoves[k].equals(learnedMoves[j]) && (k != j)))
                                    valid = false;
                            }
                        }
                    }while(!valid);
                }
            }
            else
            {
                for(int j=0; j<4; j++)
                {
                    if(j < p.getSpecie().getPossibleMoves().size())
                        learnedMoves[j] = p.getSpecie().getPossibleMoves().get(j);
                }
            }
            //learnedMoves[0] = moves.get(227);
            p.setLearnedMoves(learnedMoves);
            p.setNature(natures.get(r.nextInt(natures.size())));
            
            p.heal(100);
            p.restorePP();
            forTrainer.addToParty(p);
        }
    }

    public ArrayList<Specie> getSpecies() {
        return species;
    }

    public ArrayList<Move> getMoves() {
        return moves;
    }

    public ArrayList<Nature> getNatures() {
        return natures;
    }
    
    public void countTest() {
        qtItems = 7;
        qtSpreads = 6;
        System.out.println("qtItems: "+qtItems);
        System.out.println("qtSpreads: "+qtSpreads);        
        double c = 0;
        for(int i=0; i<goodspecies.size(); i++)
        {
            if(goodspecies.get(i).getPossibleMoves().size()>3)
            {
                double a = 11; //goodspecies.get(i).getPossibleMoves().size();
                for(double j=a-1; j>0; j--)
                    a *= j;
                double b1 = 4;
                for(double j=b1-1; j>0; j--)
                    b1 *= j;
                double b2 = 11; //goodspecies.get(i).getPossibleMoves().size() - 4;
                for(double j=b2-1; j>0; j--)
                    b2 *= j;
                double temp = a / (b1 * b2);
                temp *= goodspecies.get(i).getAbilities().size();
                temp *= qtItems;
                temp *= qtSpreads;
                c += temp;
            }
        }
        System.out.println(c);
    }
}
