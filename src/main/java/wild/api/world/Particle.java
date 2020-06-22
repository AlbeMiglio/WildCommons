/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package wild.api.world;

import lombok.NonNull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static wild.core.WildCommonsPlugin.nmsManager;

public enum Particle {
	
	HUGE_EXPLOSION("hugeexplosion"),
	LARGE_EXPLODE("largeexplode"),
	FIREWORKS_SPARK("fireworksSpark"),
	BUBBLE("bubble"),
	SUSPENDED("suspended"),
	DEPTH_SUSPENDED("depthsuspend"),
	TOWNAURA("townaura"),
	CRITICAL("crit"),
	MAGIC_CRITICAL("magicCrit"),
	SMOKE("smoke"),
	MOB_SPELL("mobSpell"),
	MOB_SPEEL_AMBIENT("mobSpellAmbient"),
	SPELL("spell"),
	INSTANT_SPELL("instantSpell"),
	WITCH_MAGIC("witchMagic"),
	NOTE("note"),
	PORTAL("portal"),
	ENCHANTMENT_TABLE("enchantmenttable"),
	EXPLODE("explode"),
	FLAME("flame"),
	LAVA("lava"),
	FOOTSTEP("footstep"),
	SPLASH("splash"),
	LARGE_SMOKE("largesmoke"),
	CLOUD("cloud"),
	RED_DUST("reddust"),
	SNOBALL("snowballpoof"),
	DRIP_WATER("dripWater"),
	DRIP_LAVA("dripLava"),
	SNOW_SHOVEL("snowshovel"),
	SLIMEBALL("slime"),
	HEART("heart"),
	ANGRY_VILLAGER("angryVillager"),
	HAPPY_VILLAGER("happyVillager"),
	// 1.9+
	END_ROD("endRod"),
	DRAGON_BREATH("dragonbreath");
	
	private String stringID;
	
	private Particle(String stringID) {
		this.stringID = stringID;
	}
	
	public String getStringID() {
		return stringID;
	}
	
	public void displaySingle(@NonNull Location loc) {
		displaySingle(loc.getWorld(), (float) loc.getX(), (float) loc.getY(), (float) loc.getZ());
	}
	
	public void displaySingle(@NonNull World world, float x, float y, float z) {
		display(world, x, y, z, 0, 0, 0, 0, 1);
	}
	
	public void display(@NonNull Location loc, float dx, float dy, float dz, float speed, int amount) {
		display(loc.getWorld(), (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), dx, dy, dz, speed, amount);
	}
	
	public void display(@NonNull World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		nmsManager.makeNormalParticle(getStringID(), world, x, y, z, dx, dy, dz, speed, amount);
	}
	
	public void displayPlayer(@NonNull Player player, World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		nmsManager.makeNormalParticle(player, getStringID(), world, x, y, z, dx, dy, dz, speed, amount);
	}
	
	/**
	 * DEVE essere un blocco, o non funziona!
	 * Le particelle NON si muovono con speed = 0.
	 */
	public static void blockDust(@NonNull Material material, int data, @NonNull World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		nmsManager.makeBlockDustParticle(material, data, world, x, y, z, dx, dy, dz, speed, amount);
	}
	
	/**
	 * DEVE essere un blocco, o non funziona!
	 * Le particelle SI MUOVONO e NON sono influenzate dalla velocità.
	 */
	public static void blockCrack(@NonNull Material material, int data, @NonNull World world, float x, float y, float z, float dx, float dy, float dz, int amount) {
		nmsManager.makeBlockCrackParticle(material, data, world, x, y, z, dx, dy, dz, 0, amount);
	}

	/**
	 * Valido anche per gli item.
	 * Le particelle NON si muovono con speed = 0.
	 */
	public static void iconCrack(@NonNull Material material, @NonNull World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		nmsManager.makeIconCrackParticle(material, world, x, y, z, dx, dy, dz, speed, amount);
	}
}
