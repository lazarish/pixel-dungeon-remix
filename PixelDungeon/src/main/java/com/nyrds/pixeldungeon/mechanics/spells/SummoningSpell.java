package com.nyrds.pixeldungeon.mechanics.spells;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.mobs.common.Deathling;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.actors.mobs.Rat;
import com.watabou.pixeldungeon.effects.Wound;
import com.watabou.pixeldungeon.plants.Sungrass;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;

import java.util.Collection;

/**
 * Created by DeadDie on 02.09.2017
 */
public class SummoningSpell extends Spell {

    private int summonLimit = 1;
    private static final String TXT_MAXIMUM_PETS  	   = Game.getVar(R.string.Spells_SummonLimitReached);


    @Override
    public void use(Hero hero){
        if(isSummoningLimitReached(hero)){
            GLog.w( getLimitWarning(getSummonLimit()) );
            return;
        }
        if(!hero.spendSoulPoints(spellCost())){
            GLog.w( notEnoughSouls(name) );
            return;
        }
        cast(hero);
    }

    @Override
    public void cast(Hero hero){
        super.cast(hero);

        int spawnPos = Dungeon.level.getEmptyCellNextTo(hero.getPos());

        Wound.hit(hero);
        Buff.detach(hero, Sungrass.Health.class);

        if (Dungeon.level.cellValid(spawnPos)) {
            Mob pet = Mob.makePet(getSummonMob(), hero);
            pet.setPos(spawnPos);
            Dungeon.level.spawnMob(pet);
        }

        hero.spend(1/hero.speed());
    }


    public boolean isSummoningLimitReached(Hero hero){
        if (getSummonLimit() <= getNumberOfSummons(hero)){
            return true;
        }
        return false;
    }

    public int getNumberOfSummons(Hero hero){
        Collection<Mob> pets = hero.getPets();

        int n = 0;
        for (Mob mob : pets) {
            if (mob.isAlive() && mob instanceof Deathling) {
                n++;
            }
        }

        return n;
    }

    public int getSummonLimit(){
        return summonLimit;
    }

    public Mob getSummonMob(){
        return new Rat();
    }

    public String getLimitWarning(int limit){
        return Utils.format(TXT_MAXIMUM_PETS, this.name(), limit);
    }
}
