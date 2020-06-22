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
package wild.api;

import static wild.core.WildCommonsPlugin.*;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Sets;

import lombok.NonNull;
import wild.core.nms.interfaces.FancyMessage;

/**
 * E' conveniente importare tutti i metodi in modo statico con import wild.api.WildCommons.*;
 */
public class WildCommons {

	public static String getBukkitVersion() {
		Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
		if (matcher.find()) {
			return matcher.group();
		} else {
			return null;
		}
	}
	
	
	private static Set<PotionEffectType> badPotionEffects = Sets.newHashSet(
		PotionEffectType.BLINDNESS,
		PotionEffectType.CONFUSION,
		PotionEffectType.HARM,
		PotionEffectType.HUNGER,
		PotionEffectType.POISON,
		PotionEffectType.SLOW,
		PotionEffectType.SLOW_DIGGING,
		PotionEffectType.WEAKNESS,
		PotionEffectType.WITHER
	);
	
	public static boolean isBadPotionEffect(PotionEffectType type) {
		return badPotionEffects.contains(type);
	}
	
	public static ItemStack removeAttributes(@NonNull ItemStack item) {
		return nmsManager.removeAttributes(item);
	}
	
	public static FishHook getFishingHook(@NonNull Player player) {
		return nmsManager.getFishingHook(player);
	}
	
	public static void removeFishingHook(@NonNull FishHook fishHook) {
		nmsManager.removeFishingHook(fishHook);
	}
	
	public static void sendExperiencePacket(@NonNull Player player, float exp) {
		sendExperiencePacket(player, exp, 0);
	}
	
	public static void sendExperiencePacket(@NonNull Player player, float exp, int level) {
		nmsManager.sendExperiencePacket(player, exp, level);
	}
	
	public static void sendPluginMessagePacket(@NonNull Player player, String channel, byte[] data) {
		nmsManager.sendPluginMessage(player, channel, data);
	}
	
	public static void clearInventoryFully(@NonNull Player player) {
		player.setItemOnCursor(null);
		PlayerInventory playerInventory = player.getInventory();
		playerInventory.clear();
		playerInventory.setArmorContents(null);
		
		Inventory topOpenInventory = player.getOpenInventory().getTopInventory();
		if (topOpenInventory != null && topOpenInventory.getType() == InventoryType.CRAFTING) {
			topOpenInventory.clear();
		}
	}
	
	public static void removePotionEffects(@NonNull Player player) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}
	
	public static FancyMessage fancyMessage(@NonNull String firstText) {
		return nmsManager.fancyMessage(firstText);
	}
	
	public static void pauseThread(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// Ignora
		}
	}
	
	public static String color(String input) {
		if (input == null) return null;
		
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	
	public static void heal(@NonNull Damageable damageable, double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("heal amount must be positive");
		}
		
		double newHealth = damageable.getHealth() + amount;
		double maxHealth = damageable.getMaxHealth();
		if (newHealth > maxHealth) {
			damageable.setHealth(maxHealth);
		} else {
			damageable.setHealth(newHealth);
		}
	}
	
	public static void respawn(@NonNull Player player) {
		player.spigot().respawn();
	}
	
	public static int getPortalCooldown(@NonNull Entity entity) {
		return nmsManager.getPortalCooldown(entity);
	}
	
	public static boolean sendActionBar(@NonNull Player player, String message) {
		return nmsManager.sendActionBar(player, message);
	}
	
	public static boolean sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
		return nmsManager.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
	}
	
	public static int refreshChunk(Chunk chunk) {
		return nmsManager.refreshChunk(chunk);
	}
	
	public static int getEnchantmentSeed(Player player) {
		return nmsManager.getEnchantmentSeed(player);
	}
	
	public static void setEnchantmentSeed(Player player, int seed) {
		nmsManager.setEnchantmentSeed(player, seed);
	}
	
	public static EntityType getEggType(ItemStack item) {
		return nmsManager.getEggType(item);
	}
	
	public static ItemStack setEggType(ItemStack item, EntityType type) {
		return nmsManager.setEggType(item, type);
	}

	public static class Unsafe {
		
		private static boolean restoredFishingRodDamage;
		
		public static void restoreFishingRodDamage() {
			if (restoredFishingRodDamage) {
				return;
			} else {
				restoredFishingRodDamage = true;
			}
			
			nmsManager.restoreFishingRodDamage();
		}
		
		/**
		 * Definizione del sorgente decompilato MCP:
	     * Blocks entities from spawning when they do their AABB check to make sure the spot is clear of entities that can
	     * prevent spawning.
	     * 
	     * Nota: ha l'effetto collaterale di consentire il posizionamento di blocchi nella posizione dell'entità
	     */
		public static void setPreventEntitySpawning(Entity entity, boolean preventEntitySpawning) {
			nmsManager.setPreventEntitySpawning(entity, preventEntitySpawning);
		}
		
		public static void setToolBaseDamage(Material tool, float baseDamage) throws Exception {
			nmsManager.setToolBaseDamage(tool, baseDamage);
		}
		
		public static void setDefaultArmorToughness(String armor, double toughness) throws Exception {
			nmsManager.setArmorToughness(armor, toughness);
		}
		
		public static void sendTeamPrefixSuffixChangePacket(@NonNull Player player, @NonNull Team team, @NonNull String prefix,  @NonNull String suffix) throws Exception {
			nmsManager.sendTeamPrefixChangePacket(player, team.getName(), team.getDisplayName(), prefix, suffix);
		}
	
	}

}
