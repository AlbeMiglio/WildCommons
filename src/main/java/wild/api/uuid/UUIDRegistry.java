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
package wild.api.uuid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import wild.api.util.CIString;
import wild.core.WildCommonsPlugin;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDRegistry implements Listener {

	private static File saveFile;
	private static BiMap<UUID, CIString> uuidToNames;
	private static Logger logger;
	
	private static boolean needSave;
	
	
	public static void syncLoadUUIDs(Collection<UUID> uuids) {
		for (UUID uuid : uuids) {
			if (!containsUUID(uuid)) {
				logger.warning("Fetching online the name for UUID " + uuid + " because it was not found in the UUID map");
				updateAssociation(uuid);
				needSave = true;
			}
		}
	}
	
	
	public static boolean containsUUID(UUID uuid) {
		synchronized (uuidToNames) {
			return uuidToNames.containsKey(uuid);
		}
	}
	
	
	public static String getName(UUID uuid) {
		Player onlinePlayer = Bukkit.getPlayer(uuid);
		if (onlinePlayer != null) {
			return onlinePlayer.getName();
		}
		
		CIString ciName;
		
		synchronized (uuidToNames) {
			ciName = uuidToNames.get(uuid);
		}
		
		if (ciName != null) {
			return ciName.toString();
		} else {
			return null;
		}
	}
	
	public static String getNameFallback(UUID uuid) {
		String name = getName(uuid);
		if (name != null) {
			return name;
		} else {
			return uuid.toString();
		}
	}
	
	public static UUID getUUID(String name) {
		Player onlinePlayer = Bukkit.getPlayerExact(name);
		if (onlinePlayer != null) {
			return onlinePlayer.getUniqueId();
		}
		
		CIString ciName = new CIString(name);
		synchronized (uuidToNames) {
			return uuidToNames.inverse().get(ciName);
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent event) {
		registerAssociation(event.getPlayer().getUniqueId(), event.getPlayer().getName(), true, true);
	}
	

	private static void registerAssociation(UUID uuid, String name, boolean isGuaranteedFresh, boolean resolveConflictsAsync) {
		CIString ciName = new CIString(name);
		boolean resolveConflicts = false;
		UUID previousAssociatedUUID;
		
		synchronized (uuidToNames) {
			/*
			 * Note: if the UUID already exists with a different name, it's either:
			 * - being updated because a player with that UUID joined
			 * - duplicate inside the save file because of a manual edit
			 * 
			 * In both cases it shouldn't be relevant and it shouldn't trigger an update.
			 * We only check if a name exists with a different UUID, in that case we should update both UUIDs.
			 */
			previousAssociatedUUID = uuidToNames.inverse().get(ciName);
			
			if (previousAssociatedUUID == null) {
				// Ok, new association
				uuidToNames.put(uuid, ciName);
				needSave = true;
				
			} else if (previousAssociatedUUID.equals(uuid)) {
				// Ok, association already present
				
			} else {
				// Conflict, this name is associated with a different UUID
				uuidToNames.inverse().remove(ciName);
				if (isGuaranteedFresh) {
					// If this association is guaranteed to be fresh, not need to check with Mojang API
					uuidToNames.put(uuid, ciName);
				}
				resolveConflicts = true;
				needSave = true;
			}
		}
		
		if (resolveConflicts) {
			// Schedule update for both UUIDs
			logger.info("Resolving UUID conflict on name " + name + ": " + uuid + " vs " + previousAssociatedUUID);
			
			Runnable task = () -> {
				if (!isGuaranteedFresh) {
					// Also update the current one if it's not guaranteed to be fresh, as it could be wrong
					updateAssociation(uuid);
				}
				updateAssociation(previousAssociatedUUID);
			};
			
			if (resolveConflictsAsync) {
				runTaskAsync(task);
			} else {
				task.run();
			}
		}
	}
	
	private static void updateAssociation(UUID uuid) {
		updateAssociation(uuid, 1);
	}

	private static void updateAssociation(UUID uuid, int attemptNumber) {
		try {
			String name = UUIDFetcher.fetchName(uuid);
			logger.info("Fetched online name for UUID " + uuid + ": " + name);
			registerAssociation(uuid, name, true, true); // Resolve new eventual conflicts async
			
		} catch (ProfileNotFoundException e) {
			logger.log(Level.WARNING, "Found invalid UUID: " + uuid);
			
		} catch (APILimitException e) {
			logger.log(Level.WARNING, "Failed to resolve name for UUID " + uuid + ", API limit exceeded");
			runTaskLaterAsync(() -> {
				updateAssociation(uuid, attemptNumber + 1);
			}, 60 * 20L); // Wait at least a minute for API limits
			
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Failed to resolve name for UUID " + uuid + ", retrying later (attempt #" + attemptNumber + ")", t);
			runTaskLaterAsync(() -> {
				updateAssociation(uuid, attemptNumber + 1);
			}, Math.min(attemptNumber * 5, 60) * 20L); // Wait 5, 10, 15, ... up to 60 seconds for each retry
		}
	}
	
	protected static void init() throws IOException {
		if (saveFile != null) {
			throw new IllegalArgumentException("Already initialized");
		}
		saveFile = new File(WildCommonsPlugin.instance.getDataFolder(), "uuid-registry.csv");
		uuidToNames = HashBiMap.create();
		logger = WildCommonsPlugin.instance.getLogger();
		
		synchronized (saveFile) {
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}

			try (final BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
				String line;
				while ((line = reader.readLine()) != null) {
					final String[] values = line.split(",");
					if (values.length != 2) {
						logger.warning("Found bad line in the UUID registry save file: " + line);
						continue;
					}
					final UUID uuid = UUID.fromString(values[0]);
					final String name = values[1];
					registerAssociation(uuid, name, false, false);
				}
			}
		}
		
		Bukkit.getPluginManager().registerEvents(new UUIDRegistry(), WildCommonsPlugin.instance);
		Bukkit.getScheduler().runTaskTimerAsynchronously(WildCommonsPlugin.instance, () -> {
			if (needSave) {
				save();
			}
		}, 5 * 60 * 20L, 5 * 60 * 20L);
	}

	protected static void save() {
		Map<UUID, CIString> uuidToNamesCopy;
		
		synchronized (uuidToNames) {
			uuidToNamesCopy = new HashMap<>(uuidToNames);
		}
		
		try {
			synchronized (saveFile) {
				if (!saveFile.exists()) {
					saveFile.createNewFile();
				}
	
				try (final BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
					for (Entry<UUID, CIString> entry : uuidToNamesCopy.entrySet()) {
						UUID uuid = entry.getKey();
						CIString ciName = entry.getValue();
						writer.append(uuid.toString() + "," + ciName.toString());
						writer.newLine();
					}
				}
				needSave = false;
			}				
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Couldn't save UUID registry to file", ex);
		}
	}
	
	private static void runTaskAsync(Runnable task) {
		Bukkit.getScheduler().runTaskAsynchronously(WildCommonsPlugin.instance, task);
	}
	
	private static void runTaskLaterAsync(Runnable task, long ticks) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(WildCommonsPlugin.instance, task, ticks);
	}
	
}
