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
public class Move {
    private int id = 0;
    private String name = "";
    private Type type = null;
    private int power = 0;
    private int pp = 0;
    private int accuracy = 0;
    private int priority = 0;
    private int damageClass = 0; //0: Status, 1: Physical, 2: Special
    private int effect = 0;
    private int effectChance = 0;
    private boolean isStruggle = false;
    
    private int target = 0;
    /*
        Possible Target ID numbers:
        1. One specific move.  How this move is chosen depends upon on the move being used.
        2. One other Pokémon on the field, selected by the trainer.  Stolen moves reuse the same target.
        3. The user's ally (if any).
        4. The user's side of the field.  Affects the user and its ally (if any).
        5. Either the user or its ally, selected by the trainer.
        6. The opposing side of the field.  Affects opposing Pokémon.
        7. The user.
        8. One opposing Pokémon, selected at random.
        9. Every other Pokémon on the field.
        10. One other Pokémon on the field, selected by the trainer.
        11. All opposing Pokémon.
        12. The entire field.  Affects all Pokémon.
        13. The user and its allies.
        14. Every Pokémon on the field.
    */
    
    //Move flags
    //User touches the target.  This triggers some abilities (e.g., []{ability:static}) and items (e.g., []{item:sticky-barb}).
    private boolean makesContact                = false;
    //This move has a charging turn that can be skipped with a []{item:power-herb}.
    private boolean hasChargingTurn             = false;
    //The turn after this move is used, the Pokémon's action is skipped so it can recharge.
    private boolean mustRecharge                = false;
    //This move will not work if the target has used []{move:detect} or []{move:protect} this turn.
    private boolean blockedByDetectProtect      = false;
    //This move may be reflected back at the user with []{move:magic-coat} or []{ability:magic-bounce}.
    private boolean reflectable                 = false;
    //This move will be stolen if another Pokémon has used []{move:snatch} this turn.
    private boolean snatchable                  = false;
    //A Pokémon targeted by this move can use []{move:mirror-move} to copy it.
    private boolean copiedByMirrorMove          = false;
    //This move has 1.2× its usual power when used by a Pokémon with []{ability:iron-fist}.
    private boolean punch_based                 = false;
    //Pokémon with []{ability:soundproof} are immune to this move.
    private boolean sound_based                 = false;
    //This move cannot be used in high []{move:gravity}.
    private boolean unusableDuringGravity       = false;
    //This move can be used while frozen to force the Pokémon to defrost.
    private boolean defrostsWhenUsed            = false;
    //In triple battles, this move can be used on either side to target the farthest away opposing Pokémon.
    private boolean targetsOppositeInTriple     = false;
    //This move is blocked by []{move:heal-block}.
    private boolean heals                       = false;
    //This move ignores the target's []{move:substitute}.
    private boolean ignoresSubstitute           = false;
    //Pokémon with []{ability:overcoat} and []{type:grass}-type Pokémon are immune to this move.
    private boolean powder_based                = false;
    //This move has 1.5× its usual power when used by a Pokémon with []{ability:strong-jaw}.
    private boolean jaw_based                   = false;
    //This move has 1.5× its usual power when used by a Pokémon with []{ability:mega-launcher}.
    private boolean pulse_based                 = false;
    //This move is blocked by []{ability:bulletproof}.
    private boolean ballistics_based            = false;
    //This move is blocked by []{ability:aroma-veil} and cured by []{item:mental-herb}.
    private boolean mental_effects              = false;
    //This move is unusable during Sky Battles.
    private boolean unusableDuringSkyBattle     = false;
    
    
    //Meta data for moves
    private int metaCategory    = -2;
    private String metaAilment     = "";
    private int minHits         = -2;
    private int maxHits         = -2;
    private int minTurns        = -2;
    private int maxTurns        = -2;
    private int drain           = -2;
    private int healing         = -2;
    private int criticalRate    = -2;
    private int ailmentChance   = -2;
    private int flinchChance    = -2;
    private int statChance      = -2;
    private ArrayList<int[]> statChanges = new ArrayList<>();
    
    /*
        Possible Meta Category IDs:
        0. Inflicts damage
        1. No damage; inflicts status ailment
        2. No damage; lowers target's stats or raises user's stats
        3. No damage; heals the user
        4. Inflicts damage; inflicts status ailment
        5. No damage; inflicts status ailment; raises target's stats
        6. Inflicts damage; lowers target's stats
        7. Inflicts damage; raises user's stats
        8. Inflicts damage; absorbs damage done to heal the user
        9. One-hit KO
        10. Effect on the whole field
        11. Effect on one side of the field
        12. Forces target to switch out
        13. Unique effect
    
        Possible Meta Ailment IDs:
        -1. ????
        0. none
        1. Paralysis
        2. Sleep
        3. Freeze
        4. Burn
        5. Poison
        6. Confusion
        7. Infatuation
        8. Trap
        9. Nightmare
        12. Torment
        13. Disable
        14. Yawn
        15. Heal Block
        17. No type immunity
        18. Leech Seed
        19. Embargo
        20. Perish Song
        21. Ingrain
    */
    
    
    public void setFlag(int i, boolean value) {
        switch(i){
            case 1: makesContact                = value; break;
            case 2: hasChargingTurn             = value; break;
            case 3: mustRecharge                = value; break;
            case 4: blockedByDetectProtect      = value; break;
            case 5: reflectable                 = value; break;
            case 6: snatchable                  = value; break;
            case 7: copiedByMirrorMove          = value; break;
            case 8: punch_based                 = value; break;
            case 9: sound_based                 = value; break;
            case 10: unusableDuringGravity      = value; break;
            case 11: defrostsWhenUsed           = value; break;
            case 12: targetsOppositeInTriple    = value; break;
            case 13: heals                      = value; break;
            case 14: ignoresSubstitute          = value; break;
            case 15: powder_based               = value; break;
            case 16: jaw_based                  = value; break;
            case 17: pulse_based                = value; break;
            case 18: ballistics_based           = value; break;
            case 19: mental_effects             = value; break;
            case 20: unusableDuringSkyBattle    = value; break;
            default: System.err.println("Unsupported Move Flag: " + i + " for " + name + "! Aborting."); System.exit(0);
        }
    }
    
    public void addStatChange(int stat, int amount) {
        int[] sc = {stat, amount};
        statChanges.add(sc);
    }
    
    public int amountOfStatChanges() {
        return statChanges.size();
    }
    
    public int[] getStatChangesToDo(int i) {
        if(!statChanges.isEmpty())
            return statChanges.get(i);
        else
        {
            int[] invalid = {-1, 0};
            return invalid;
        }
    }

    public int getMetaCategory() {
        return metaCategory;
    }

    public void setMetaCategory(int metaCategory) {
        this.metaCategory = metaCategory;
    }

    public String getMetaAilment() {
        return metaAilment;
    }

    public void setMetaAilment(String metaAilment) {
        this.metaAilment = metaAilment;
    }

    public int getMinHits() {
        return minHits;
    }

    public void setMinHits(int minHits) {
        this.minHits = minHits;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
    }

    public int getMinTurns() {
        return minTurns;
    }

    public void setMinTurns(int minTurns) {
        this.minTurns = minTurns;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    public int getDrain() {
        return drain;
    }

    public void setDrain(int drain) {
        this.drain = drain;
    }

    public int getHealing() {
        return healing;
    }

    public void setHealing(int healing) {
        this.healing = healing;
    }

    public int getCriticalRate() {
        return criticalRate;
    }

    public void setCriticalRate(int criticalRate) {
        this.criticalRate = criticalRate;
    }

    public int getAilmentChance() {
        return ailmentChance;
    }

    public void setAilmentChance(int ailmentChance) {
        this.ailmentChance = ailmentChance;
    }

    public int getFlinchChance() {
        return flinchChance;
    }

    public void setFlinchChance(int flinchChance) {
        this.flinchChance = flinchChance;
    }

    public int getStatChance() {
        return statChance;
    }

    public void setStatChance(int statChance) {
        this.statChance = statChance;
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
        if(name.equals("Struggle"))
            isStruggle = true;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(int damageClass) {
        this.damageClass = damageClass;
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public int getEffectChance() {
        return effectChance;
    }

    public void setEffectChance(int effectChance) {
        this.effectChance = effectChance;
    }

    //Getters for move flags
    public boolean isMakesContact() {
        return makesContact;
    }

    public boolean isHasChargingTurn() {
        return hasChargingTurn;
    }

    public boolean isMustRecharge() {
        return mustRecharge;
    }

    public boolean isBlockedByDetectProtect() {
        return blockedByDetectProtect;
    }

    public boolean isReflectable() {
        return reflectable;
    }

    public boolean isSnatchable() {
        return snatchable;
    }

    public boolean isCopiedByMirrorMove() {
        return copiedByMirrorMove;
    }

    public boolean isPunch_based() {
        return punch_based;
    }

    public boolean isSound_based() {
        return sound_based;
    }

    public boolean isUnusableDuringGravity() {
        return unusableDuringGravity;
    }

    public boolean isDefrostsWhenUsed() {
        return defrostsWhenUsed;
    }

    public boolean isTargetsOppositeInTriple() {
        return targetsOppositeInTriple;
    }

    public boolean isHeals() {
        return heals;
    }

    public boolean isIgnoresSubstitute() {
        return ignoresSubstitute;
    }

    public boolean isPowder_based() {
        return powder_based;
    }

    public boolean isJaw_based() {
        return jaw_based;
    }

    public boolean isPulse_based() {
        return pulse_based;
    }

    public boolean isBallistics_based() {
        return ballistics_based;
    }

    public boolean isMental_effects() {
        return mental_effects;
    }

    public boolean isUnusableDuringSkyBattle() {
        return unusableDuringSkyBattle;
    }
    
    
}
