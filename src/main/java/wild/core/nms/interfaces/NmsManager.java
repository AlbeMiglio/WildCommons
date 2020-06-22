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
package wild.core.nms.interfaces;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wild.api.world.SightInfo;

public interface NmsManager {

	public ItemStack removeAttributes(ItemStack item);

	public void sendExperiencePacket(Player player, float exp, int level);
	
	public void makeNormalParticle(String particle, World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount);
	
	public void makeNormalParticle(Player player, String particle, World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount);
	
	public void makeBlockCrackParticle(Material block, int data, World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount);
	
	public void makeBlockDustParticle(Material block, int data, World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount);
	
	public void makeIconCrackParticle(Material block, World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount);
	
	public boolean isInsideBlock(Location loc);
	
	public Location getBlockInSight(Player player);
	
	public Location getBlockInSight(Location start, Location end);
	
	public SightInfo getPlayerInSight(Player player, List<Player> possibleTargets, double boundingBoxIncrement);
	
	public void spawnSilentFirework(Location location, FireworkEffect... effects);
	
	public FancyMessage fancyMessage(String firstText);
	
	public void sendTeamPrefixChangePacket(Player player, String teamName, String teamDisplayName, String prefix, String suffix) throws Exception;

	public void sendPluginMessage(Player player, String channel, byte[] data);
	
	public boolean sendActionBar(Player player, String message);
	
	public boolean sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle);

	public int getPortalCooldown(Entity entity);
	
	public int refreshChunk(Chunk chunk);

	public void setInvulnerable(Entity entity, boolean invulnerable);

	public boolean isSuffocatingInsideBlock(Player player);
	
	public FishHook getFishingHook(Player player);

	public void removeFishingHook(FishHook fishHook);
	
	public int getEnchantmentSeed(Player player);
	
	public void setEnchantmentSeed(Player player, int seed);

	public void disablePlayerSave(World world) throws Exception;

	public void restoreFishingRodDamage();

	public void setToolBaseDamage(Material tool, float baseDamage) throws Exception;

	public void setArmorToughness(String armor, double toughness) throws Exception;

	public void setPreventEntitySpawning(Entity entity, boolean preventEntitySpawning);
	
	public ItemStack setEggType(ItemStack item, EntityType type);
	
	public EntityType getEggType(ItemStack item);
	
}
