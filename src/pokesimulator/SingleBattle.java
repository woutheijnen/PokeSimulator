/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokesimulator;

import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Wout
 */
public class SingleBattle {
    Engine engine;
    Trainer trainer1 = null;
    Trainer trainer2 = null;
    ArrayList<String> mayorStatus = new ArrayList<>();
    
    public SingleBattle(Engine e) {
        engine = e;
        mayorStatus.add("paralysis");
        mayorStatus.add("sleep");
        mayorStatus.add("freeze");
        mayorStatus.add("burn");
        mayorStatus.add("poison");
    }
    
    public boolean startBattle(Trainer t1, Trainer t2, boolean ai) {
        trainer1 = t1;
        trainer2 = t2;
        
        String t1str = "Enemy";
        if(t1.isPlayer())
            t1str = "Player";
        String t2str = "Enemy";
        if(t2.isPlayer())
            t2str = "Player";
        t1str += " " + t1.getName();
        t2str += " " + t2.getName();
        
        System.out.println("Starting a Single Battle between " + t1str + " and " + t2str + "!");
        
        //Before battle
        Random r = new Random();
        ArrayList<Integer> possiblePlayerActions = new ArrayList<>();
        ArrayList<Integer> possibleOpponentActions = new ArrayList<>();
        
        do{
            //For each move each trainer has, if it is a move and has enough pp, and has an effect on the opponent
            boolean playerStruggle = true;
            boolean opponentStruggle = true;
            for(int i=0; i<4; i++)
            {
                if(t1.getActivePokemon().getLearnedMoves()[i] != null)
                {
                    if(t1.getActivePokemon().getPP(i) > 0)
                    {
                        possiblePlayerActions.add(i);
                        playerStruggle = false;
                    }
                }
                if(t2.getActivePokemon().getLearnedMoves()[i] != null)
                {
                    if(t2.getActivePokemon().getPP(i) > 0)
                    {
                        possibleOpponentActions.add(i);
                        opponentStruggle = false;
                    }
                }
            }
            
            //If struggle is possible, add it
            if(playerStruggle)
                possiblePlayerActions.add(5);
            if(opponentStruggle)
                possibleOpponentActions.add(5);
            
            //If the trainers can switch, add the possibilty to the list
            if(t1.canSwitch(false))
                possiblePlayerActions.add(4);
            if(t2.canSwitch(false))
                possibleOpponentActions.add(4);
            
            int playerAction = possiblePlayerActions.get(r.nextInt(possiblePlayerActions.size()));
            int opponentAction = possibleOpponentActions.get(r.nextInt(possibleOpponentActions.size()));
            possiblePlayerActions.clear();
            possibleOpponentActions.clear();
            
            if(t1.getActivePokemon().getCharging() > -1)
                playerAction = 6;
            if(t2.getActivePokemon().getCharging() > -1)
                opponentAction = 6;
            
            //If the pokemon has to recharge, don't chose a move
            if(t1.getActivePokemon().mustRecharge())
                playerAction = 7;
            if(t2.getActivePokemon().mustRecharge())
                opponentAction = 7;

            //Inform in the console what action has been chosen
            String playerActionName = "switch pokémon";
            String opponentActionName = "switch pokémon";
            if(playerAction < 4 && t1.getActivePokemon().getLearnedMoves()[playerAction] != null)
                playerActionName = "use " + t1.getActivePokemon().getLearnedMoves()[playerAction].getName();
            if(opponentAction < 4 && t2.getActivePokemon().getLearnedMoves()[opponentAction] != null)
                opponentActionName = "use " + t2.getActivePokemon().getLearnedMoves()[opponentAction].getName();
            if(playerAction < 6)
                System.out.println("Player " + t1.getName() + " chose to " + playerActionName + ".");
            if(opponentAction < 6)
                System.out.println("Enemy " + t2.getName() + " chose to " + opponentActionName + ".");

            //Battle mechanics for single battle
            //4 = switch, 0-3 = moves
            int playerPriority = 0;
            int opponentPriority = 0;

            //See if a switch is chosen
            if(playerAction == 4)
            {
                playerPriority = 6;
                if(opponentAction < 4)
                {
                    if(t2.getActivePokemon().getLearnedMoves()[opponentAction] != null)
                    {
                        if(t2.getActivePokemon().getLearnedMoves()[opponentAction].getEffect() == 129)
                            opponentPriority = 7; //Pursuit
                    }
                }
            }
            if(opponentAction == 4)
            {
                opponentPriority = 6;
                if(playerAction < 4)
                {
                    if(t1.getActivePokemon().getLearnedMoves()[playerAction] != null)
                    {
                        if(t1.getActivePokemon().getLearnedMoves()[playerAction].getEffect() == 129)
                            playerPriority = 7;
                    }
                }
            }
            
            //Get the priority of the move if it has a priority
            if(playerAction < 4 && playerPriority < 6) //Not pursuit related
                playerPriority = t1.getActivePokemon().getLearnedMoves()[playerAction].getPriority();
            if(opponentAction < 4 && opponentPriority < 6)
                opponentPriority = t2.getActivePokemon().getLearnedMoves()[opponentAction].getPriority();

            //See who moves first
            boolean playerGoesFirst = playerPriority > opponentPriority;         
            if(playerPriority == opponentPriority)
            {
                //Speed modifier for trainers   
                playerGoesFirst = t1.getActivePokemon().getStat(5, true) > t2.getActivePokemon().getStat(5, true);
                if(t1.getActivePokemon().getStat(5, true) == t2.getActivePokemon().getStat(5, true))
                    playerGoesFirst = r.nextBoolean();
            }
            System.out.println("Player Priority: " + playerPriority + ". Player Speed: " + t1.getActivePokemon().getStat(5, true) + ".");
            System.out.println("Enemy Priority: " + opponentPriority + ". Enemy Speed: " + t2.getActivePokemon().getStat(5, true) + ".");

            //Execute the turn based attack logic
            //First trainer attacks first semi turn, then other trainer strikes back
            for(int i=0; i<2; i++)
            {
                if((i==0 && playerGoesFirst) || (i==1 && !playerGoesFirst))
                {
                    //Player chose to switch, so let's switch
                    if(playerAction == 4)
                        doSwitch(t1, t1.decideSwitch(), false); //Find available pokemon to switch with decideSwitch() and do the switch
                    else //Use chosen move or struggle
                        useMove(t1.getActivePokemon(), playerAction, t2.getActivePokemon());
                }
                else
                {
                    //Opponent chose to switch, so let's switch
                    if(opponentAction == 4)
                        doSwitch(t2, t2.decideSwitch(), false); //Find available pokemon to switch with decideSwitch() and do the switch
                    else //Use chosen move or struggle
                        useMove(t2.getActivePokemon(), opponentAction, t1.getActivePokemon());
                }
            }
            //See if any pokemon fainted and make them switch
            deathCheck();
            
            System.out.println();
            System.out.println(t1.getName() + " is using: " + t1.getActivePokemon().getSpecie().getName() + ". Health: " + t1.getActivePokemon().getCurrentHP() + "/" + t1.getActivePokemon().getStat(0, false));
            System.out.println(t2.getName() + " is using: " + t2.getActivePokemon().getSpecie().getName() + ". Health: " + t2.getActivePokemon().getCurrentHP() + "/" + t2.getActivePokemon().getStat(0, false));
            System.out.println();
        }while(!t1.hasLost() && !t2.hasLost());
        
        //Show the battle result
        if(t1.hasLost() && t2.hasLost())
            System.out.println("It's a DRAW!");
        else
        {
            if(t1.hasLost())
                System.out.println("Player " + t1.getName() + " LOST the battle!");
            else
                System.out.println("Enemy " + t2.getName() + " LOST the battle!");
        }
        
        return true;
    }
    
    private void doSwitch(Trainer trainer, int chosenID, boolean forced) {
        String ancient = "";
        if(!forced)
            ancient = "Great job " + trainer.getActivePokemon().getSpecie().getName() + " come back! ";
        //Reset stat changes, pseudostatuses, etc
        trainer.getActivePokemon().reset();
        //Set active pokemon for the trainer
        trainer.setActivePokemon(chosenID);
        System.out.println(ancient + "Go " + trainer.getActivePokemon().getSpecie().getName() + "!");
    }
    
    private void deathCheck() {
        if(trainer1.getActivePokemon().getCurrentHP() < 1)
        {
            if(trainer1.canSwitch(true))
                doSwitch(trainer1, trainer1.decideSwitch(), true);
            else
                trainer1.lose();
        }
        if(trainer2.getActivePokemon().getCurrentHP() < 1)
        {
            if(trainer2.canSwitch(true))
                doSwitch(trainer2, trainer2.decideSwitch(), true);
            else
                trainer2.lose();
        }
    }
    
    private void useMove(Pokemon attacker, int moveID, Pokemon defender) {
        //Verify if pokemon hasn't fainted
        if(attacker.getCurrentHP() > 0)
        {
            boolean useMove = true;
            boolean specialCase = false;
            
            //Get the move data
            Move move = engine.getStruggleData();
            if(moveID < 4)
                move = attacker.getLearnedMoves()[moveID];
            
            //If it is a charging move (2nd turn)
            if(moveID == 6)
            {
                move = attacker.getLearnedMoves()[attacker.getCharging()];
                moveID = attacker.getCharging();
            }

            //If the move has to charge before being used
            if(move.isHasChargingTurn())
            {
                specialCase = true;
                if(attacker.getCharging() == -1)
                {
                    attacker.setCharging(moveID);
                    System.out.println(attacker.getSpecie().getName() + " started to charge for a move!");
                    useMove = false;
                }
                else
                {
                    attacker.deducePP(attacker.getCharging());
                    attacker.setCharging(-1);
                }
            }
            
            //If the move needs a recharge afterwards...
            if(attacker.mustRecharge())
            {
                specialCase = true;
                useMove = false;
                attacker.setRecharge(false);
                System.out.println(attacker.getSpecie().getName() + " has to recharge!");
            }
            
            //If the move is a normal kind of move and doesn't have addicional logic, deduct PP
            if(!specialCase && moveID < 4)
                attacker.deducePP(moveID);
            
            //As last, before using the actual move but after deducing pp: Accuracy check
            if(useMove)
            {
                System.out.println(attacker.getSpecie().getName() + " attacks " + defender.getSpecie().getName() + " with " + move.getName() + "! (PP: " + attacker.getPP(moveID) + "/" + move.getPp() + ")");
                useMove = accuracyCheck(attacker, move, defender);
                if(!useMove)
                    System.out.println(attacker.getSpecie().getName() + "'s attack missed!");
            }
            
            if(useMove)
            {
                if(move.getDamageClass() > 1)
                {
                    //Physical or Special move
                    int damage = calculateDamage(attacker, move, defender);
                    defender.doDamage(damage);
                    System.out.println("It does " + damage + " damage! " + defender.getSpecie().getName() + "'s health: " + defender.getCurrentHP() + "/" + defender.getStat(0, false) + ".");
                    
                    //Attacker has to recharge for some specific moves
                    if(move.isMustRecharge())
                        attacker.setRecharge(true);
                    
                    //Check if the pokemons fainted
                    if(defender.getCurrentHP() < 1)
                    {
                        defender.setStatus("fainted");
                        System.out.println(defender.getSpecie().getName() + " fainted!");
                    }
                    if(attacker.getCurrentHP() < 1)
                    {
                        attacker.setStatus("fainted");
                        System.out.println(attacker.getSpecie().getName() + " fainted!");
                    }
                }
                else
                {
                    //Status move
                    if(move.getMetaCategory() == 1)
                    {
                        String ailment = move.getMetaAilment();
                        if(mayorStatus.contains(ailment))
                        {
                            if(defender.setStatus(ailment))
                                System.out.println(defender.getSpecie().getName() + " got " + ailment + " [mayor]!");
                            else
                                System.out.println("But it failed!");
                        }
                        else
                            if(defender.addPseudoStatus(ailment))
                                System.out.println(defender.getSpecie().getName() + " got " + ailment + " [pseudo]!");
                            else
                                System.out.println("But it failed!");
                    }
                }
            }
        }
    }
    
    private boolean accuracyCheck(Pokemon attacker, Move move, Pokemon defender) {
        if(move.getAccuracy() > 0)
        {
            Random r = new Random();
            double accuracyBase = move.getAccuracy();

            double acc;
            int modifier = attacker.getModifiers()[6] - defender.getModifiers()[7];
            
            //Apply minimum, maximum cap for the modifier
            if(modifier < -6)
                modifier = -6;
            if(modifier > 6)
                modifier = 6;
            
            //Accuracy modifier for the attacker and evasion for the defender are combined
            if(modifier >= 0)
            {
                acc = 3.0 + attacker.getModifiers()[6];
                acc /= 3.0;
            }
            else
            {
                acc = 3.0;
                acc /= 3.0 + (-1.0 * attacker.getModifiers()[6]);
            }
            
            acc *= accuracyBase;
            return r.nextInt(100)+1 <= acc;
        }
        else
            return true;
    }
    
    private int calculateDamage(Pokemon attacker, Move move, Pokemon defender) {
        //Find out if the move uses attack or sp.attack and if it targets defense or sp. defense
        int attackID = 1;
        int defenseID = 2;
        if(move.getDamageClass() == 3)
        {
            attackID = 3;
            defenseID = 4;
        }
        
        //STAB
        double modifier = 1.0;
        if(move.getType() == attacker.getSpecie().getType1())
            modifier = 1.5;
        if(attacker.getSpecie().getType2() != null)
        {
            if(move.getType() == attacker.getSpecie().getType2())
                modifier = 1.5;
        }
        
        //Amount of effect against opponent
        double tm = typeModifier(move, defender);
        modifier *= tm;
        if(tm >= 1.99 && tm <= 2.01)
            System.out.println("It's super effective!");
        if(tm >= 3.99 && tm <= 4.01)
            System.out.println("It's ultra effective!");
        if(tm >= 0.49 && tm <= 0.51)
            System.out.println("It's not very effective...");
        if(tm >= 0.24 && tm <= 0.26)
            System.out.println("It's not effective at all...");
        if(tm <= 0.01)
            System.out.println("It doesn't have any effect!");
        
        //Critical hit
        //TODO: Ignore Reflect/Light screen
        //Critical hit moves, abilities, ...
        int chance = 1;
        int criticalModifier = attacker.getModifiers()[8] + move.getCriticalRate();
        
        if(criticalModifier > 6)
            criticalModifier = 6;
        
        switch(criticalModifier)
        {
            case 0: chance = 16;
                    break;
            case 1: chance = 8;
                    break;
            case 2: chance = 2;
                    break;
        }
        Random r = new Random();
        boolean isCritical = false;
        if(r.nextInt(chance) == 0)
        {
            System.out.println("Critical hit!");
            modifier *= 1.5;
            isCritical = true;
        }
        
        //Other effects to add like thunder
        
        //Random effect
        modifier *= (r.nextInt(16)+85.0) * 1.0;
        modifier /= 100.0;
        
        //Damage calculation
        double temp = (2.0 * attacker.getLevel() + 10.0);
        temp /= 250.0;
        double temp2, temp3;
        
        //If the hit is critical, ignore negatif attack stages and positif defense stages
        if(!isCritical)
        {
            temp2 = attacker.getStat(attackID, true);
            temp3 = defender.getStat(defenseID, true);
        }
        else
        {
            if(attacker.getModifiers()[attackID] < 0)
                temp2 = attacker.getStat(attackID, false);
            else
                temp2 = attacker.getStat(attackID, true);
            if(defender.getModifiers()[defenseID] > 0)
                temp3 = defender.getStat(defenseID, false);
            else
                temp3 = defender.getStat(defenseID, true);
        }
        
        temp2 /= temp3;
        temp *= temp2 * move.getPower() + 2.0;
        temp *= modifier;
        int damage = (int) floor(temp);
        return damage;
    }
    
    private double typeModifier(Move move, Pokemon defender)
    {
        double tm = 1.0;
        tm *= move.getType().getMatchup(defender.getSpecie().getType1().getId()) / 100.0;
        if(defender.getSpecie().getType2() != null)
            tm *= move.getType().getMatchup(defender.getSpecie().getType2().getId()) / 100.0;
        return tm;
    }
}