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
package wild.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Maps;

import wild.api.WildCommons;
import wild.api.bridges.CosmeticsBridge;
import wild.api.bridges.EconomyBridge;
import wild.api.config.PluginConfig;
import wild.api.util.FileLogger;
import wild.api.uuid.PackageAccess;
import wild.core.commands.FireworkCommand;
import wild.core.commands.WildCommonsCommand;
import wild.core.nms.interfaces.NmsManager;
import wild.core.utils.ReflectionUtils;

public class WildCommonsPlugin extends JavaPlugin {

	public static WildCommonsPlugin instance;
	public static NmsManager nmsManager;
	public static FileLogger mysqlErrorLogger;
	public static boolean disableWorldPlayerSave;
	public static boolean serverInitialized;
	
	public static Map<Material, String> materialTranslationsMap = Maps.newHashMap();
	public static Map<Enchantment, String> enchantmentTranslationsMap = Maps.newHashMap();
	public static Map<PotionEffectType, String> potionEffectsTranslationsMap = Maps.newHashMap();
	public static Map<EntityType, String> entityTypesTranslationsMap = Maps.newHashMap();
	
	
	@Override
	public void onLoad() {
		instance = this;
		mysqlErrorLogger = new FileLogger(this, "mysql.error.log");
		
		String version = WildCommons.getBukkitVersion();

		if ("v1_9_R2".equals(version)) {
			nmsManager = new wild.core.nms.v1_9_R2.NmsManagerImpl();
		} else if ("v1_12_R1".equals(version)) {
			nmsManager = new wild.core.nms.v1_12_R1.NmsManagerImpl();
		} else {
			System.out.println(
				" \n "
			   + "\n  ######################## ATTENZIONE ########################"
			   + "\n  #                                                          #"
			   + "\n  #  WildCommons non e' aggiornato. Il server verra' spento. #"
			   + "\n  #                                                          #"
			   + "\n  ############################################################"
			   + "\n "
			   + "\n Versione: " + version
			   + "\n "
			);
			
			WildCommons.pauseThread(30000);
			Bukkit.shutdown();
			return;
		}

		// Rimuove i colori dai log
		try {
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			Configuration config = context.getConfiguration();
			for (Appender appender : config.getAppenders().values()) {
				if (appender instanceof RollingRandomAccessFileAppender) {
					// Modifica solo l'appender del file
					RollingRandomAccessFileAppender fileAppender = (RollingRandomAccessFileAppender) appender;
					Layout<? extends Serializable> layout = fileAppender.getLayout();
					if (layout instanceof PatternLayout) {
						PatternLayout patternLayout = (PatternLayout) layout;
						
						Object eventSerializer = ReflectionUtils.getPrivateField(PatternLayout.class, patternLayout, "eventSerializer");
						
						Field replaceField = eventSerializer.getClass().getDeclaredField("replace");
						ReflectionUtils.setPrivateFinalField(eventSerializer, replaceField, RegexReplacement.createRegexReplacement(String.valueOf(Pattern.compile("\\x1b\\[[0-9;]*m")), ""));
					}
				}
			}
		} catch (Throwable t) {
			getLogger().log(Level.WARNING, "Impossibile filtrare i colori nella console", t);
		}
		
		// Disabilita eventualmente il salvataggio dei giocatori
		try {
			PluginConfig config = new PluginConfig(this, "config_worlds.yml");
			String configKey = "disablePlayerSave";
			if (!config.isSet(configKey)) {
				config.set(configKey, false);
				config.save();
			}
			disableWorldPlayerSave = config.getBoolean(configKey);
			
		} catch (Exception ex) {
			getLogger().log(Level.WARNING, "Impossibile leggere o salvare il file di configurazione", ex);
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
			return;
		}

		try {
			getDataFolder().mkdirs();
			loadTranslations();
		} catch (Exception ex) {
			getLogger().log(Level.WARNING, "Impossibile leggere le traduzioni", ex);
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
			return;
		}
	}
	
	@Override
	public void onEnable() {
		try {
			PackageAccess.UUIDRegistry_init();
		} catch (Exception ex) {
			getLogger().log(Level.WARNING, "Impossibile leggere il registro degli UUID", ex);
			WildCommons.pauseThread(10000);
			this.setEnabled(false);
			Bukkit.shutdown();
			return;
		}
		
		// Registra il comando
		new WildCommonsCommand(this, "wildcommons");
		new FireworkCommand(this, "firework", "fw");
		
		// Registra il listener
		Bukkit.getPluginManager().registerEvents(new WildCommonsListener(), this);
		
		Bukkit.getScheduler().runTaskLater(this, () -> {
			serverInitialized = true;
			
			try {
				if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
					EconomyBridge.setup();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			try {
				if (Bukkit.getPluginManager().isPluginEnabled("ProCosmetics")) {
					CosmeticsBridge.setup();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}, 1L);
	}
	
	@Override
	public void onDisable() {
		PackageAccess.UUIDRegistry_save();
	}
	
	public void loadTranslations() throws Exception {
		materialTranslationsMap.clear();
		PluginConfig config;
		boolean configChanged;
		
		config = new PluginConfig(this, "translations_materials.yml");
		configChanged = false;

		for (Material material : Material.values()) {
			
			// Nome della chiave.
			String key = material.toString();
			String translation;
			
			if (config.isString(key)) {
				
				// Ottieni la traduzione dal config.
				translation = config.getString(key);
				
			} else {
			
				// Usa la traduzione di default.
				translation = material.toString();
				
				// Aggiunge il materiale alle traduzioni.
				config.set(key, translation);
				configChanged = true;
				
			}
			
			// Mette nella mappa delle traduzioni.
			materialTranslationsMap.put(material, translation);
		}
		
		if (configChanged) {
			config.save();
		}
		
		enchantmentTranslationsMap.clear();
		config = new PluginConfig(this, "translations_enchants.yml");
		configChanged = false;

		for (Enchantment enchant : Enchantment.values()) {
			
			if (enchant == null) continue;
			
			// Nome della chiave.
			String key = enchant.getName();
			String translation;
			
			if (config.isString(key)) {
				
				// Ottieni la traduzione dal config.
				translation = config.getString(key);
				
			} else {
			
				// Usa la traduzione di default.
				translation = enchant.getName();
				
				// Aggiunge il materiale alle traduzioni.
				config.set(key, translation);
				configChanged = true;
				
			}
			
			// Mette nella mappa delle traduzioni.
			enchantmentTranslationsMap.put(enchant, translation);
		}
		
		if (configChanged) {
			config.save();
		}
		
		potionEffectsTranslationsMap.clear();
		config = new PluginConfig(this, "translations_potions.yml");
		configChanged = false;

		for (PotionEffectType potionType : PotionEffectType.values()) {
			
			if (potionType == null) continue;
			
			// Nome della chiave.
			String key = potionType.getName();
			String translation;
			
			if (config.isString(key)) {
				
				// Ottieni la traduzione dal config.
				translation = config.getString(key);
				
			} else {
			
				// Usa la traduzione di default.
				translation = potionType.getName();
				
				// Aggiunge il materiale alle traduzioni.
				config.set(key, translation);
				configChanged = true;
				
			}
			
			// Mette nella mappa delle traduzioni.
			potionEffectsTranslationsMap.put(potionType, translation);
		}
		
		if (configChanged) {
			config.save();
		}
		
		entityTypesTranslationsMap.clear();
		config = new PluginConfig(this, "translations_entities.yml");
		configChanged = false;

		for (EntityType entityType : EntityType.values()) {
			
			// Nome della chiave.
			String key = entityType.toString();
			String translation;
			
			if (config.isString(key)) {
				
				// Ottieni la traduzione dal config.
				translation = config.getString(key);
				
			} else {
			
				// Usa la traduzione di default.
				translation = entityType.toString();
				
				// Aggiunge il materiale alle traduzioni.
				config.set(key, translation);
				configChanged = true;
				
			}
			
			// Mette nella mappa delle traduzioni.
			entityTypesTranslationsMap.put(entityType, translation);
		}
		
		if (configChanged) {
			config.save();
		}
	}
}
