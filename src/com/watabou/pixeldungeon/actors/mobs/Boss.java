package com.watabou.pixeldungeon.actors.mobs;

import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.watabou.pixeldungeon.items.weapon.enchantments.Death;

abstract public class Boss extends Mob {
	
	public Boss() {
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
}
