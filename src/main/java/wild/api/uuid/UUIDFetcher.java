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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import wild.api.util.CaseInsensitiveMap;
import wild.core.WildCommonsPlugin;

public class UUIDFetcher {
	
	private static final Map<String, UUIDData> UUID_CACHE = new CaseInsensitiveMap<>();
	private static final int UUID_CACHE_MAX_SIZE = 500;
	
	private static final Map<UUID, NameData> NAME_CACHE = new HashMap<>();
	private static final int NAME_CACHE_MAX_SIZE = 200;
	
	private static final long MAX_DATA_AGE = TimeUnit.MINUTES.toMillis(5);
	private static final long API_LIMIT_REACHED_COOLDOWN = TimeUnit.MINUTES.toMillis(1);
	
	private static long lastAPILimitReached;
	
	public static void fetchUUIDAsync(Plugin plugin, String playerName, Consumer<UUID> uuidConsumer, CommandSender exceptionHandler) {
		fetchUUIDAsync(plugin, playerName, uuidConsumer, error -> {
			if (error instanceof APILimitException) {
				exceptionHandler.sendMessage(ChatColor.RED + "Impossibile ottenere l'ID di " + playerName + ": limite richieste raggiunto, riprova più tardi.");
			} else if (error instanceof ProfileNotFoundException) {
				exceptionHandler.sendMessage(ChatColor.RED + playerName + " non è un nickname Premium attualmente esistente.");
			} else {
				error.printStackTrace();
				exceptionHandler.sendMessage(ChatColor.RED + "Errore interno durante la richiesta dell'ID di " + playerName + ".");
			}
		});	
	}
	
	public static void fetchUUIDAsync(Plugin plugin, String playerName, Consumer<UUID> uuidConsumer, UUIDExceptionHandler exceptionHandler) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				UUID uuid = fetchUUID(playerName);
				if (uuidConsumer != null) {
					Bukkit.getScheduler().runTask(plugin, () -> {
						uuidConsumer.accept(uuid);
					});
				}
			} catch (Throwable t) {
				if (exceptionHandler != null) {
					Bukkit.getScheduler().runTask(plugin, () -> {
						exceptionHandler.handle(t);
					});
				}
			}
		});
	}
	
	public static UUID fetchUUID(String playerName) throws APILimitException, ProfileNotFoundException, Throwable {
		if (WildCommonsPlugin.serverInitialized && Bukkit.isPrimaryThread()) {
			throw new IllegalStateException("Running from Bukkit primary thread");
		}
		
		long now = System.currentTimeMillis();
		
		if (now - lastAPILimitReached < API_LIMIT_REACHED_COOLDOWN) {
			throw new APILimitException();
		}
		
		UUIDData cachedUUIDData = getUUIDFromCache(playerName);
		
		if (cachedUUIDData != null && cachedUUIDData.isFresh(now, MAX_DATA_AGE)) {
			if (cachedUUIDData.isValidProfile()) {
				return cachedUUIDData.getUuid();
			} else {
				throw new ProfileNotFoundException();
			}
		}
		
		try {
			UUID uuid = getFreshUUID(playerName);
			putUUIDInCache(playerName, new UUIDData(uuid, now), now);
			return uuid;
			
		} catch (APILimitException e) {
			lastAPILimitReached = now;
			throw e;
			
		} catch (ProfileNotFoundException e) {
			putUUIDInCache(playerName, new UUIDData(null, now), now);
			throw e;
		}
	}
	
	public static String fetchName(UUID uuid) throws APILimitException, ProfileNotFoundException, Throwable {
		if (WildCommonsPlugin.serverInitialized && Bukkit.isPrimaryThread()) {
			throw new IllegalStateException("Running from Bukkit primary thread");
		}
		
		long now = System.currentTimeMillis();
		
		if (now - lastAPILimitReached < API_LIMIT_REACHED_COOLDOWN) {
			throw new APILimitException();
		}
		
		NameData cachedNameData = getNameFromCache(uuid);
		
		if (cachedNameData != null && cachedNameData.isFresh(now, MAX_DATA_AGE)) {
			if (cachedNameData.isValidProfile()) {
				return cachedNameData.getName();
			} else {
				throw new ProfileNotFoundException();
			}
		}
		
		try {
			String name = getFreshName(uuid);
			putNameInCache(uuid, new NameData(name, now), now);
			return name;
			
		} catch (APILimitException e) {
			lastAPILimitReached = now;
			throw e;
			
		} catch (ProfileNotFoundException e) {
			putNameInCache(uuid, new NameData(null, now), now);
			throw e;
		}
	}
	
	private static UUIDData getUUIDFromCache(String playerName) {
		synchronized (UUID_CACHE) {
			return UUID_CACHE.get(playerName);
		}
	}
	
	private static void putUUIDInCache(String playerName, UUIDData uuidData, long now) {
		synchronized (UUID_CACHE) {
			UUID_CACHE.put(playerName, uuidData);
			if (UUID_CACHE.size() > UUID_CACHE_MAX_SIZE) {
				UUID_CACHE.values().removeIf(u -> !u.isFresh(now, MAX_DATA_AGE));
			}
		}
	}
	
	private static NameData getNameFromCache(UUID uuid) {
		synchronized (NAME_CACHE) {
			return NAME_CACHE.get(uuid);
		}
	}
	
	private static void putNameInCache(UUID uuid, NameData nameData, long now) {
		synchronized (NAME_CACHE) {
			NAME_CACHE.put(uuid, nameData);
			if (NAME_CACHE.size() > NAME_CACHE_MAX_SIZE) {
				NAME_CACHE.values().removeIf(n -> !n.isFresh(now, MAX_DATA_AGE));
			}
		}
	}
	
	private static UUID getFreshUUID(String playerName) throws APILimitException, ProfileNotFoundException, Throwable {
		JsonObject response = (JsonObject) apiRequest("https://api.mojang.com/users/profiles/minecraft/" + playerName);
		return UUID.fromString(addDashes(response.get("id").getAsString()));
	}
	
	private static String getFreshName(UUID uuid) throws APILimitException, ProfileNotFoundException, Throwable {
		JsonArray response = (JsonArray) apiRequest("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
		JsonObject lastName = (JsonObject) response.get(response.size() - 1);
		return lastName.get("name").getAsString();
	}
	
	private static JsonElement apiRequest(String url) throws APILimitException, ProfileNotFoundException, Throwable {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setReadTimeout(3000);
		conn.setConnectTimeout(2000);
		conn.setRequestMethod("GET");

		int statusCode = conn.getResponseCode();
		
		if (statusCode == 200) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				return new JsonParser().parse(in);
			}
		} else if (statusCode == 204) {
			throw new ProfileNotFoundException();
			
		} else if (statusCode == 429) {
			throw new APILimitException();
		} else {
			throw new RuntimeException("Unknown HTTP status code: " + statusCode);
		}
	}

	private static String addDashes(String uuid) {
		StringBuilder sb = new StringBuilder(uuid);
	    sb.insert(8, "-");
	    sb = new StringBuilder(sb.toString());
	    sb.insert(13, "-");
	    sb = new StringBuilder(sb.toString());
	    sb.insert(18, "-");
	    sb = new StringBuilder(sb.toString());
	    sb.insert(23, "-");

	    return sb.toString();
	}
}
