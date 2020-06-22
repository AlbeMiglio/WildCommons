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
package wild.api.bridges;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import wild.api.WildCommons;
import wild.api.item.ItemBuilder;
import wild.api.sound.SoundEnum;

/**
 * Supporto a ProCosmetics temporaneamente RIMOSSO
 *
 */
public class CosmeticsBridge {

	
	/* * * * * * * * * * * * * * * * *
	 *                               *
	 *            A  P  I            *
	 *                               *
	 * * * * * * * * * * * * * * * * */
	
	static final List<String> OPEN_COMMAND_ALIASES = Arrays.asList("/cosmetics", "/c", "/trail", "/trails");
	
	/**
	 * Dà gli item a un giocatore.
	 */
	public static void giveCosmeticsItems(Inventory inventory) {
		inventory.setItem(7, cosmeticsMenuItem);
		inventory.setItem(8, FIREWORK_ITEM);
	}
	
	public static enum Status {
		LOBBY, 		// Tutto abilitato (default)
		GAME, 		// Solo particelle e cose che non interagiscono con i giocatori
		SPECTATOR 	// Nulla, neanche le particelle
	}
	
	
	
	/* * * * * * * * * * * * * * * * *
	 *                               *
	 *     I  N  T  E  R  N  A  L    *
	 *                               *
	 * * * * * * * * * * * * * * * * */
	static final Set<String> PRO_COSMETICS_ALIASES = Sets.newHashSet("/procosmetics", "/cosmetics", "/proc", "/pc");
	
	private static final Set<String> ALL_MENU_FILES = Sets.newHashSet("Arrow-Effects.yml", "Balloons.yml", "Death-Effects.yml", "Emotes.yml", "Gadgets.yml",
																 "Miniatures.yml", "Morphs.yml", "Mounts.yml", "Music.yml", "Particle-Effects.yml", "Pets.yml");
	private static final Set<String> ALLOWED_GAME_MENU_FILES = Sets.newHashSet("Arrow-Effects.yml", "Death-Effects.yml", "Particle-Effects.yml");
	
	static final String FILTER_WITHOUT_MENU_OPEN_CODE = "{'bold':true,'italic':true,'color':'green','text':' '}".replace("'", "\"");
	
	static final Sound DENIED_COSMETIC_USE_SOUND = SoundEnum.get("NOTE_BASS");
	
	protected static final ItemStack FIREWORK_ITEM = ItemBuilder.of(Material.FIREWORK)
			.name(ChatColor.GOLD + "Fuoco d'artificio " + ChatColor.GRAY + "(Click destro)")
			.lore(
					ChatColor.GRAY + "Lancia un fuoco d'artificio casuale.",
					ChatColor.GRAY + "Solo per " + ChatColor.GOLD + "VIP" + ChatColor.GRAY + ".",
					"",
					ChatColor.GRAY + "Accessibile anche con il comando " + ChatColor.LIGHT_PURPLE + "/firework").build();
			
	private static boolean enabled;
	private static boolean useProtocolLib;
	private static ItemStack cosmeticsMenuItem;
	
	private static String mainMenuTitle;
	static List<Pair<Integer, ItemStack>> mainMenuExtraItems = Lists.newArrayList();
	static Map<String, Map<Integer, String>> permissionsBySlotByMenu = Maps.newHashMap();
	static Set<String> allowedGameMenuTitles = Sets.newHashSet();

	static Map<Player, Status> playerStatuses = new PlayerStatusesMap();
	static Map<Player, Long> playerMenuCloseTime = Maps.newConcurrentMap();
	
	public static void setup() {
		if (enabled) return;
		enabled = Bukkit.getPluginManager().isPluginEnabled("ProCosmetics");
		
		if (enabled) {
			try {
				YamlConfiguration pcConfig = YamlConfiguration.loadConfiguration(new File("plugins/ProCosmetics/Config.yml"));
				mainMenuTitle = pcConfig.getString("General.Cosmetic-Menu.Title");
				
				Material material = Material.valueOf(pcConfig.getString("General.Cosmetic-Menu.Material"));
				int data = pcConfig.getInt("General.Cosmetic-Menu.Data");
				int amount = pcConfig.getInt("General.Cosmetic-Menu.Amount");
				String displayName = pcConfig.getString("General.Cosmetic-Menu.Displayname");
				List<String> lore = pcConfig.getStringList("General.Cosmetic-Menu.Lore");
				for (int i = 0; i < lore.size(); i++) {
					lore.set(i, WildCommons.color(lore.get(i)));
				}
				
				cosmeticsMenuItem = createItem(material, amount, data, WildCommons.color(displayName), lore);
				
			} catch (Exception e) {
				System.out.println("Cannot read main menu and create \"fake\" menu opener");
				e.printStackTrace();
			}
			
			try {
				for (String menuFile : ALL_MENU_FILES) {
					String menuId = menuFile.replace(".yml", "");
					YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("plugins/ProCosmetics/" + menuFile));
					ConfigurationSection inventorySection = yaml.getConfigurationSection(menuId + ".Inventory");
					String menuTitle = inventorySection.getString("Title");
					
					if (!yaml.getBoolean(menuId + ".Enable") && yaml.getBoolean(menuId + ".Show-Disabled")) {
						Material material = Material.valueOf(yaml.getString(menuId + ".Material"));
						int data = yaml.getInt(menuId + ".Data");
						int amount = yaml.getInt(menuId + ".Amount");
						int slot = yaml.getInt(menuId + ".Slot");
						String displayName = yaml.getString(menuId + ".Displayname");
						
						ItemStack extraItem = createItem(material, amount, (short) data, WildCommons.color(displayName), Arrays.asList(
								"",
								ChatColor.RED + "Questa categoria è disabilitata",
								ChatColor.RED + "in questa modalità."));
						
						mainMenuExtraItems.add(new ImmutablePair<Integer, ItemStack>(slot, extraItem));
					}
					
					if (ALLOWED_GAME_MENU_FILES.contains(menuFile)) {
						allowedGameMenuTitles.add(menuTitle);
					}
					
					Map<Integer, String> permissionsBySlot = Maps.newHashMap();
					permissionsBySlotByMenu.put(menuTitle, permissionsBySlot);
					
					// Legge tutti gli slot dei cosmetici, e per ognuno "crea" un permesso
					for (String key : inventorySection.getKeys(false)) {
						if (inventorySection.isConfigurationSection(key)) {
							ConfigurationSection itemSection = inventorySection.getConfigurationSection(key);
							
							// Solo i cosmetici hanno la sezione Enable (go back non ce l'ha)
							if (itemSection.getBoolean("Enable", false)) {
								permissionsBySlot.put(itemSection.getInt("Slot"), "ProCosmetics." + menuId + "." + key);
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Cannot read menu titles, they will be all blocked");
				e.printStackTrace();
			}
			
			
			PC_BukkitListener.enable();
			if (useProtocolLib = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
				PC_PacketListener.enable();
			}
		}
		
		
	}
	
	protected static boolean isCosmeticsMainMenu(Inventory inventory) {
		return inventory != null && inventory.getType() == InventoryType.CHEST && inventory.getHolder() == null && mainMenuTitle.equals(inventory.getTitle());
	}
	
	protected static boolean isCosmeticsMenu(Inventory inventory) {
		return inventory != null && inventory.getType() == InventoryType.CHEST && inventory.getHolder() == null && (permissionsBySlotByMenu.containsKey(inventory.getTitle()) || mainMenuTitle.equals(inventory.getTitle()));
	}
	
	private static ItemStack createItem(Material material, int amount, int data, String displayName, List<String> lore) {
		ItemStack item = new ItemStack(material, amount, (short) data);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	
	static boolean checkOpenMenu(PlayerCommandPreprocessEvent event) {
		if (useProtocolLib) {
			// Ci pensa protocol lib a prevenire i click sugli item giusti
			return true;
		}
		
		Status status = playerStatuses.get(event.getPlayer());

		if (status == Status.GAME || status == Status.SPECTATOR) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Non puoi cambiare cosmetici ora.");
			return false;
		} else {
			return true;
		}
	}
	
	
	@SuppressWarnings("serial")
	private static class PlayerStatusesMap extends HashMap<Player, Status> {

		@Override
		public Status get(Object paramObject) {
			Status status = super.get(paramObject);
			return status != null ? status : Status.LOBBY;
		}

		@Override
		public Status put(Player paramK, Status paramV) {
			Status status = super.put(paramK, paramV);
			return status != null ? status : Status.LOBBY;
		}
		
	}

}
