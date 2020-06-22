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
package wild.core.nms.v1_9_R2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftFish;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_9_R2.AxisAlignedBB;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.DamageSource;
import net.minecraft.server.v1_9_R2.DataConverterManager;
import net.minecraft.server.v1_9_R2.EntityFishingHook;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.EnumParticle;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R2.Item;
import net.minecraft.server.v1_9_R2.ItemArmor;
import net.minecraft.server.v1_9_R2.ItemTool;
import net.minecraft.server.v1_9_R2.MovingObjectPosition;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.PacketDataSerializer;
import net.minecraft.server.v1_9_R2.PacketPlayOutChat;
import net.minecraft.server.v1_9_R2.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_9_R2.PacketPlayOutExperience;
import net.minecraft.server.v1_9_R2.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_9_R2.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_9_R2.PlayerChunk;
import net.minecraft.server.v1_9_R2.PlayerConnection;
import net.minecraft.server.v1_9_R2.Vec3D;
import net.minecraft.server.v1_9_R2.World;
import net.minecraft.server.v1_9_R2.WorldNBTStorage;
import net.minecraft.server.v1_9_R2.WorldServer;
import wild.api.WildCommons;
import wild.api.world.SightInfo;
import wild.core.WildCommonsPlugin;
import wild.core.nms.interfaces.FancyMessage;
import wild.core.nms.interfaces.NmsManager;
import wild.core.utils.MathUtils;
import wild.core.utils.ReflectionUtils;

public class NmsManagerImpl implements NmsManager {
	

	@Override
	public ItemStack removeAttributes(ItemStack item) {
        if(item == null) {
            return item;
        }
        
        ItemMeta meta = item.getItemMeta();
		if (isNullOrEmpty(meta.getItemFlags())) {
			// Add them only if necessary
			meta.addItemFlags(ItemFlag.values());
			item.setItemMeta(meta);
		}
		return item;
    }
	
	private static boolean isNullOrEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}

	@Override
	public void sendExperiencePacket(Player player, float exp, int level) {
		if (player.isDead()) return;
		
		PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
		if (conn != null) {
			conn.sendPacket(new PacketPlayOutExperience(exp, 0, level));
		}
	}

	@Override
	public Location getBlockInSight(Player player) {
		Location start = player.getEyeLocation();
		Location end = start.clone().add(start.getDirection().multiply(MathUtils.SIGHT_LENGTH));
		World nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
		
		Vec3D startVec = new Vec3D(start.getX(), start.getY(), start.getZ());
	    Vec3D endVec = new Vec3D(end.getX(), end.getY(), end.getZ());
	    MovingObjectPosition line = nmsWorld.rayTrace(startVec, endVec, false, true, false);
	      
	    if (line == null) {
	    	return end;
	    } else {
	    	return new Location(start.getWorld(), line.pos.x, line.pos.y, line.pos.z);
	    }
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isInsideBlock(Location loc) {
		World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
		BlockPosition blockPos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
		IBlockData type = nmsWorld.getType(blockPos);
		AxisAlignedBB blockAABB = type.getBlock().a(type, nmsWorld, blockPos);
		if (blockAABB == null) {
			return false;
		}
		
		return blockAABB.a(new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
	}
	
	
	@Override
	public Location getBlockInSight(Location start, Location end) {
		World nmsWorld = ((CraftWorld) start.getWorld()).getHandle();
		
		Vec3D startVec = new Vec3D(start.getX(), start.getY(), start.getZ());
	    Vec3D endVec = new Vec3D(end.getX(), end.getY(), end.getZ());
	    MovingObjectPosition line = nmsWorld.rayTrace(startVec, endVec, false, true, false);
	      
	    if (line == null) {
	    	return null;
	    } else {
	    	return new Location(start.getWorld(), line.pos.x, line.pos.y, line.pos.z);
	    }
	}
	
	
	@Override
	public SightInfo getPlayerInSight(Player player, List<Player> possibleTargets, double boundingBoxIncrement) {
		
		final Location start = player.getEyeLocation();
		
		World nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		
		final double startX = start.getX();
		final double startY = start.getY();
		final double startZ = start.getZ();
		
		Vector direction = player.getLocation().getDirection().multiply(MathUtils.SIGHT_LENGTH);
		
		double endX = startX + direction.getX();
		double endY = startY + direction.getY();
		double endZ = startZ + direction.getZ();
		
		Vec3D startVec = new Vec3D(startX, startY, startZ);
	    Vec3D endVec = new Vec3D(endX, endY, endZ);
	    MovingObjectPosition line = nmsWorld.rayTrace(startVec, endVec, false, true, false);
	    
	    if (line != null) {
	    	endVec = line.pos;
	    }
	    
		startVec = new Vec3D(startX, startY, startZ);
	    
	    EntityPlayer nmsTarget = null;
	    double lowestDistance = 0.0;

	    EntityPlayer nmsPossibleTarget = null;
	    for (Player possibleTarget : possibleTargets) {
	    	  
	    	nmsPossibleTarget = ((CraftPlayer) possibleTarget).getHandle();
	    	  
	        if (nmsPlayer != nmsPossibleTarget && nmsPossibleTarget.isAlive()) {
	        	
	        	AxisAlignedBB collisionBox = nmsPossibleTarget.getBoundingBox().grow(boundingBoxIncrement, boundingBoxIncrement, boundingBoxIncrement);
	        	MovingObjectPosition entityLine = collisionBox.a(startVec, endVec);

	        	if (entityLine != null) {
	        		
	        		double distanceFromPlayer = startVec.distanceSquared(entityLine.pos);
	        		endVec = entityLine.pos;

	        		// Find the nearest player
	        		if ((distanceFromPlayer < lowestDistance) || (lowestDistance == 0.0)) {
	        			nmsTarget = nmsPossibleTarget;
	            		lowestDistance = distanceFromPlayer;
	        		}
	        	}
	        }
	    }
	    
	    return new SightInfo(nmsTarget != null ? nmsTarget.getBukkitEntity() : null, new Location(player.getWorld(), endVec.x, endVec.y, endVec.z));
	}
	
	@Override
	public void spawnSilentFirework(Location location, FireworkEffect... effects) {
		WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
		SilentEntityFirework firework = new SilentEntityFirework(nmsWorld);
		FireworkMeta meta = ((Firework) firework.getBukkitEntity()).getFireworkMeta();
		meta.addEffects(effects);
		((Firework) firework.getBukkitEntity()).setFireworkMeta(meta);
		firework.setPosition(location.getX(), location.getY(), location.getZ());
		nmsWorld.addEntity(firework, SpawnReason.CUSTOM);
	}

	@Override
	public FancyMessage fancyMessage(String firstText) {
		return new FancyMessageImpl(firstText);
	}

	@Override
	public void makeNormalParticle(String particle, org.bukkit.World bukkitWorld, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		EnumParticle enumParticle = null;
	    for (EnumParticle ep : EnumParticle.values()) {
	    	if (ep.b().equals(particle)) {
	    		enumParticle = ep;
	    		break;
	    	}
	    }
	    
	    if (enumParticle == null) {
	    	return;
	    }
		
		PacketPlayOutWorldParticles nmsPacket = new PacketPlayOutWorldParticles(enumParticle, false, x, y, z, dx, dy, dz, speed, amount);

		WorldServer nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
		net.minecraft.server.v1_9_R2.EntityPlayer nmsPlayer;
		for (Object playerObject : nmsWorld.players) {
			nmsPlayer = (net.minecraft.server.v1_9_R2.EntityPlayer) playerObject;
			if (MathUtils.square(x - nmsPlayer.locX) + MathUtils.square(z - nmsPlayer.locZ) < 4096) { // Radius = 64 * 64
				nmsPlayer.playerConnection.sendPacket(nmsPacket);
			}
		}
	}
	
	@Override
	public void makeNormalParticle(Player player, String particle, org.bukkit.World bukkitWorld, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		EnumParticle enumParticle = null;
	    for (EnumParticle ep : EnumParticle.values()) {
	    	if (ep.b().equals(particle)) {
	    		enumParticle = ep;
	    		break;
	    	}
	    }
	    
	    if (enumParticle != null) {
	    	((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(enumParticle, false, x, y, z, dx, dy, dz, speed, amount));
	    }
	}

	@Override
	public void sendTeamPrefixChangePacket(Player player, String teamName, String teamDisplayName, String prefix, String suffix) throws Exception {
		PacketPlayOutScoreboardTeam teamUpdatePacket = new PacketPlayOutScoreboardTeam();
		ReflectionUtils.setPrivateField(teamUpdatePacket, "a", teamName);
		ReflectionUtils.setPrivateField(teamUpdatePacket, "b", teamDisplayName);
		ReflectionUtils.setPrivateField(teamUpdatePacket, "c", prefix);
		ReflectionUtils.setPrivateField(teamUpdatePacket, "d", suffix);
		
		ReflectionUtils.setPrivateField(teamUpdatePacket, "i", Integer.valueOf(2));
		ReflectionUtils.setPrivateField(teamUpdatePacket, "j", Integer.valueOf(3));
		
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(teamUpdatePacket);
	}

	@Override
	public void sendPluginMessage(Player player, String channel, byte[] data) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutCustomPayload(channel, new PacketDataSerializer(Unpooled.wrappedBuffer(data))));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void makeBlockCrackParticle(Material block, int data, org.bukkit.World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		PacketPlayOutWorldParticles nmsPacket = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_CRACK, false, x, y, z, dx, dy, dz, speed, amount, new int[]{ data << 12 | block.getId() & 0xFFF });
		
		WorldServer nmsWorld = ((CraftWorld)world).getHandle();

	    for (Iterator<EntityHuman> localIterator = nmsWorld.players.iterator(); localIterator.hasNext(); ) {
	    	EntityPlayer nmsPlayer = (EntityPlayer) localIterator.next();
	    	if (MathUtils.square(x - nmsPlayer.locX) + MathUtils.square(z - nmsPlayer.locZ) < 4096.0D) {
	    		nmsPlayer.playerConnection.sendPacket(nmsPacket);
	    	}
	    }
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void makeBlockDustParticle(Material block, int data, org.bukkit.World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		PacketPlayOutWorldParticles nmsPacket = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_DUST, false, x, y, z, dx, dy, dz, speed, amount, new int[]{ data << 12 | block.getId() & 0xFFF });
		
		WorldServer nmsWorld = ((CraftWorld)world).getHandle();

	    for (Iterator<EntityHuman> localIterator = nmsWorld.players.iterator(); localIterator.hasNext(); ) {
	    	EntityPlayer nmsPlayer = (EntityPlayer) localIterator.next();
	    	if (MathUtils.square(x - nmsPlayer.locX) + MathUtils.square(z - nmsPlayer.locZ) < 4096.0D) {
	    		nmsPlayer.playerConnection.sendPacket(nmsPacket);
	    	}
	    }
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void makeIconCrackParticle(Material item, org.bukkit.World world, float x, float y, float z, float dx, float dy, float dz, float speed, int amount) {
		PacketPlayOutWorldParticles nmsPacket = new PacketPlayOutWorldParticles(EnumParticle.ITEM_CRACK, false, x, y, z, dx, dy, dz, speed, amount, new int[] { item.getId(), 0 });
		
		WorldServer nmsWorld = ((CraftWorld)world).getHandle();

	    for (Iterator<EntityHuman> localIterator = nmsWorld.players.iterator(); localIterator.hasNext(); ) {
	    	EntityPlayer nmsPlayer = (EntityPlayer) localIterator.next();
	    	if (MathUtils.square(x - nmsPlayer.locX) + MathUtils.square(z - nmsPlayer.locZ) < 4096.0D) {
	    		nmsPlayer.playerConnection.sendPacket(nmsPacket);
	    	}
	    }
	}

	@Override
	public boolean sendActionBar(Player player, String message) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"" + message + "\"}"), (byte) 2));
		return true;
	}

	@Override
	public boolean sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		if (title == null) {
			title = "";
		}
		title = WildCommons.color(title);

		if (subtitle == null) {
			subtitle = "";
		}
		subtitle = WildCommons.color(subtitle);

		IChatBaseComponent serializedTitle = ChatSerializer.a("{\"text\":" + quote(title) + "}");
		IChatBaseComponent serializedSubTitle = ChatSerializer.a("{\"text\":" + quote(subtitle) + "}");

		nmsPlayer.playerConnection.sendPacket(new PacketPlayOutTitle(fadeIn, stay, fadeOut));
		nmsPlayer.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, serializedTitle));
		nmsPlayer.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, serializedSubTitle));
		return true;
	}
	
	private static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char c = 0;
		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String t;

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			c = string.charAt(i);
			switch (c) {
				case '\\':
				case '"':
					sb.append('\\');
					sb.append(c);
					break;
				case '/':
					sb.append('\\');
					sb.append(c);
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\r':
					sb.append("\\r");
					break;
				default:
					if (c < ' ') {
						t = "000" + Integer.toHexString(c);
						sb.append("\\u" + t.substring(t.length() - 4));
					} else {
						sb.append(c);
					}
			}
		}
		sb.append('"');
		return sb.toString();
	}
	
	@Override
	public int getPortalCooldown(Entity entity) {
		return ((CraftEntity) entity).getHandle().portalCooldown;
	}
	
	@Override
	public int refreshChunk(Chunk chunk) {
		int viewDistance = Bukkit.getViewDistance();
		WorldServer nmsWorld = ((CraftWorld) chunk.getWorld()).getHandle();
		PlayerChunk playerChunk = new PlayerChunk(nmsWorld.getPlayerChunkMap(), chunk.getX(), chunk.getZ());
		int count = 0;
		
		for (EntityHuman nmsHuman : nmsWorld.players) {
			if (nmsHuman instanceof EntityPlayer) {
				EntityPlayer nmsPlayer = (EntityPlayer) nmsHuman;
				
				int playerChunkX = NumberConversions.floor(nmsPlayer.locX) >> 4;
				int playerChunkZ = NumberConversions.floor(nmsPlayer.locZ) >> 4;
			
				if (Math.abs(playerChunkX - chunk.getX()) <= viewDistance && Math.abs(playerChunkZ - chunk.getZ()) <= viewDistance) {
					playerChunk.sendChunk(nmsPlayer);
					count++;
				}
			}
		}
		
		return count;
	}

	@Override
	public void setInvulnerable(Entity entity, boolean invulnerable) {
		net.minecraft.server.v1_9_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.setInvulnerable(invulnerable);
	}

	@Override
	public boolean isSuffocatingInsideBlock(Player player) {
		return ((CraftPlayer) player).getHandle().inBlock();
	}
	
	@Override
	public FishHook getFishingHook(Player player) {
		EntityFishingHook fishingHook = ((CraftPlayer) player).getHandle().hookedFish;
		return fishingHook != null ? (FishHook) fishingHook.getBukkitEntity() : null;
	}

	@Override
	public void removeFishingHook(FishHook fishHook) {
		((CraftEntity) fishHook).getHandle().die();
	}

	@Override
	public void disablePlayerSave(org.bukkit.World world) throws Exception {
		WorldServer nmsWorld = ((CraftWorld) world).getHandle();
		WorldNBTStorage oldNBTStorage = (WorldNBTStorage) ReflectionUtils.getPrivateField(World.class, nmsWorld, "dataManager");
		Object oldDataConverterManager = ReflectionUtils.getPrivateField(WorldNBTStorage.class, oldNBTStorage, "a");
		Object oldDefinedStructureManager = ReflectionUtils.getPrivateField(WorldNBTStorage.class, oldNBTStorage, "h");
		
		ServerNBTManagerNoSave newNBTStorage = new ServerNBTManagerNoSave(oldNBTStorage.getDirectory().getParentFile(), oldNBTStorage.getDirectory().getName(), true, (DataConverterManager) oldDataConverterManager);
		
		ReflectionUtils.setPrivateField(WorldNBTStorage.class, newNBTStorage, "h", oldDefinedStructureManager);
		ReflectionUtils.setPrivateField(World.class, nmsWorld, "dataManager", newNBTStorage);
	}

	@Override
	public int getEnchantmentSeed(Player player) {
		try {
			return (int) ReflectionUtils.getPrivateField(EntityHuman.class, ((CraftPlayer) player).getHandle(), "h");
		} catch (Exception e) {
			e.printStackTrace();
			return player.getName().toLowerCase().hashCode(); // Seed arbitrario
		}
	}

	@Override
	public void setEnchantmentSeed(Player player, int seed) {
		try {
			ReflectionUtils.setPrivateField(EntityHuman.class, ((CraftPlayer) player).getHandle(), "h", seed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void restoreFishingRodDamage() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
		
			@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
			public void onProjectileHit(ProjectileHitEvent event) {
				if (event.getEntityType() == EntityType.FISHING_HOOK) {
					EntityFishingHook nmsFishingHook = ((CraftFish) event.getEntity()).getHandle();
					Bukkit.getScheduler().runTask(WildCommonsPlugin.instance, () -> {
						if (nmsFishingHook.hooked != null) {
							nmsFishingHook.hooked.damageEntity(DamageSource.projectile(nmsFishingHook, nmsFishingHook.owner), 0.0F);
						}
					});
				}
			}
			
		}, WildCommonsPlugin.instance);
	}

	@Override
	public void setToolBaseDamage(Material tool, float baseDamage) throws Exception {
		@SuppressWarnings("deprecation")
		Item item = Item.getById(tool.getId());
		if (item instanceof ItemTool) {
			ReflectionUtils.setPrivateField(ItemTool.class, item, "b", baseDamage);
		} else {
			throw new IllegalArgumentException(tool + " is not a tool");
		}
	}

	@Override
	public void setArmorToughness(String armor, double toughness) throws Exception {
		ReflectionUtils.setPrivateField(ItemArmor.EnumArmorMaterial.valueOf(armor), "k", (float) toughness);
	}

	@Override
	public void setPreventEntitySpawning(Entity entity, boolean preventEntitySpawning) {
		((CraftEntity) entity).getHandle().i = preventEntitySpawning;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack setEggType(ItemStack item, EntityType type) {
		if (!type.isAlive()) {
			throw new IllegalArgumentException("Entity type " + type + " is not alive");
		}

		net.minecraft.server.v1_9_R2.ItemStack stack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new NBTTagCompound();
		}
		NBTTagCompound id = new NBTTagCompound();
		id.setString("id", type.getName());
		tagCompound.set("EntityTag", id);
		stack.setTag(tagCompound);
		return CraftItemStack.asBukkitCopy(stack);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EntityType getEggType(ItemStack item) {
		if (item == null) {
			throw new IllegalArgumentException("item cannot be null");
		}
		if (item.getType() != Material.MONSTER_EGG) {
			throw new IllegalArgumentException("item is not a monster egg");
		}

		net.minecraft.server.v1_9_R2.ItemStack stack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagCompound = stack.getTag();
		if (tagCompound != null) {
			return EntityType.fromName(tagCompound.getCompound("EntityTag").getString("id"));
		} else {
			return null;
		}
	}
	
}
