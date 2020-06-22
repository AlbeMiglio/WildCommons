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
package wild.core.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import wild.api.WildCommons;
import wild.api.WildConstants;
import wild.api.command.CommandFramework.Permission;
import wild.api.command.SubCommandFramework;
import wild.api.item.BookTutorial;
import wild.api.item.CustomSkullAdapter;
import wild.api.item.ItemBuilder;
import wild.core.WildCommonsPermissions;
import wild.core.WildCommonsPlugin;
import wild.core.utils.GenericUtils;
import wild.core.utils.ReflectionUtils;

@Permission(WildCommonsPermissions.USE_COMMAND)
public class WildCommonsCommand extends SubCommandFramework {
	

	public WildCommonsCommand(JavaPlugin plugin, String label) {
		super(plugin, label);
	}

	@Override
	public void noArgs(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_GREEN + "Comandi WildCommons:");
		for (SubCommandDetails subCommand : getAccessibleSubCommands(sender)) {
			sender.sendMessage(ChatColor.GREEN + "/wildcommons " + subCommand.getName() + (subCommand.getUsage() != null ? " " + subCommand.getUsage() : ""));
		}
	}
	

	@SubCommand("setslots")
	@SubCommandUsage("<numero>")
	@SubCommandMinArgs(1)
	public void setslotsSub(CommandSender sender, String label, String[] args) {
		int newSlots = CommandValidate.getPositiveInteger(args[0]);
		
		try {
			Object playerList = ReflectionUtils.getPrivateField(Bukkit.getServer(), "playerList");
			ReflectionUtils.setPrivateField(playerList.getClass().getSuperclass(), playerList, "maxPlayers", newSlots);
			sender.sendMessage(ChatColor.GREEN + "Hai impostato gli slot a " + newSlots + " fino al prossimo riavvio.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecuteException("Impossibile impostare gli slot. Controlla la console.");
		}
	}
	
	@SubCommand("head")
	@SubCommandUsage("<uuid|texture URL>")
	@SubCommandMinArgs(1)
	public void headSub(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		
		CustomSkullAdapter skullAdapter;
		
		if (args[0].length() == 36 || args[0].length() == 32) {
			String uuid = args[0].replace("-", "");
			
			try {
				JsonObject json = GenericUtils.readJsonElementFromURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid).getAsJsonObject();
				if (json.get("errorMessage") != null) {
					throw new ExecuteException("Errore: " + json.get("errorMessage"));
				}
				
				JsonArray properties = json.get("properties").getAsJsonArray();
				String base64Texture = properties.iterator().next().getAsJsonObject().get("value").getAsString();

				skullAdapter = CustomSkullAdapter.fromEncodedTexture(base64Texture);
				
			} catch (IOException e) {
				e.printStackTrace();
				throw new ExecuteException("Errore: " + e.getClass().getName());
			}
		} else {
			String textureUrl = "http://textures.minecraft.net/texture/" + args[0];
			skullAdapter = CustomSkullAdapter.fromURL(textureUrl);
		}

		ItemStack head = ItemBuilder.of(Material.SKULL_ITEM).durability(3).build();
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		skullAdapter.apply(headMeta);
		head.setItemMeta(headMeta);
		player.getInventory().addItem(head);
	}
	
	@SubCommand("cleanup")
	public void cleanupSub(CommandSender sender, String label, String[] args) {
		File pluginsFolder = new File("plugins");
		CommandValidate.isTrue(pluginsFolder.isDirectory(), "La cartella plugins non esiste!");
		
		List<File> removeList = Lists.newArrayList();
		List<String> whitelist = Arrays.asList("update", "Updater", "PluginMetrics");
		
		for (File file : pluginsFolder.listFiles()) {
			if (file.isDirectory()) {
				if (whitelist.contains(file.getName())) {
					continue;
				}
				removeList.add(file);
			}
		}
		for (File file : pluginsFolder.listFiles()) {
			if (file.isFile()) {
				
				if (file.length() < 10) {
					removeList.add(file);
					continue;
				}
				
				try {
					String pluginName = GenericUtils.getPluginYmlName(file);
					
					for (Iterator<File> iter = removeList.iterator(); iter.hasNext();) {
						if (iter.next().getName().equals(pluginName)) {
							iter.remove(); // Non togliere se appartiene a un plugin nella cartella plugins
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					sender.sendMessage(ChatColor.RED + "Impossibile leggere il file " + file.getName());
					return;
				}
			}
		}
		
		CommandValidate.isTrue(!removeList.isEmpty(), "Non ci sono file da rimuovere.");
		if (args.length > 0 && args[0].equalsIgnoreCase("confirm")) {
			int removed = 0;
			for (File toRemove : removeList) {
				try {
					FileUtils.forceDelete(toRemove);
					removed++;
				} catch (IOException e) {
					e.printStackTrace();
					sender.sendMessage(ChatColor.RED + "Impossibile cancellare " + toRemove.getName());
				}
			}
			sender.sendMessage(ChatColor.YELLOW + "Rimossi " + removed + " file.");
			
		} else {
			StringBuilder filesMessage = new StringBuilder();
			for (File toRemove : removeList) {
				if (filesMessage.length() != 0) {
					filesMessage.append(ChatColor.GRAY + ", ");
				}
				filesMessage.append(ChatColor.YELLOW + toRemove.getName());
			}
			sender.sendMessage(ChatColor.RED + "Verranno rimossi i seguenti " + removeList.size() + " file: ");
			sender.sendMessage(filesMessage.toString());
			sender.sendMessage(ChatColor.GRAY + "Usa \"/" + label + " cleanup confirm\" per continuare.");
		}
	}
	
	@SubCommand("banner")
	public void bannerSub(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		ItemStack item = new ItemStack(Material.BANNER);
		BannerMeta bannerMeta = (BannerMeta) item.getItemMeta();
		bannerMeta.setBaseColor(DyeColor.BLACK);
		bannerMeta.addPattern(new Pattern(DyeColor.LIME, PatternType.TRIANGLE_BOTTOM));
		bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_BOTTOM));
		bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CROSS));
		bannerMeta.addPattern(new Pattern(DyeColor.LIME, PatternType.STRIPE_LEFT));
		bannerMeta.addPattern(new Pattern(DyeColor.LIME, PatternType.STRIPE_RIGHT));
		bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
		bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP));
		item.setItemMeta(bannerMeta);
		player.getInventory().addItem(item);
		player.sendMessage(ChatColor.GREEN + "Hai ricevuto il banner!");
	}
	
	
	@SubCommand("setline")
	@SubCommandUsage("<numero> <testo>")
	@SubCommandMinArgs(2)
	public void setlineSub(CommandSender sender, String label, String[] args) {
		Block block = CommandValidate.getPlayerSender(sender).getTargetBlock((Set<Material>) null, 20);
		CommandValidate.notNull(block, "Il blocco è troppo lontano.");
		
		BlockState blockstate = block.getState();
		CommandValidate.isTrue(blockstate != null && blockstate instanceof Sign, "Non stai guardando un cartello.");
		
		int line = CommandValidate.getInteger(args[0]);
		CommandValidate.isTrue(1 <= line && line <= 4, "Inserisci un numero fra 1 e 4.");
		
		String newText = WildCommons.color(StringUtils.join(args, " ", 1, args.length));
		if (newText.equals("\"\"")) {
			newText = null;
		}
		
		Sign sign = (Sign) blockstate;
		sign.setLine(line - 1, newText);
		sign.update(false, false);
		
		sender.sendMessage(ChatColor.GREEN + "Hai impostato la " + line + "° linea.");
	}
	
	
	@SubCommand("replacecolor")
	@SubCommandUsage("<linea> <colore>")
	@SubCommandMinArgs(2)
	public void replacecolorSub(CommandSender sender, String label, String[] args) {
		Block block = CommandValidate.getPlayerSender(sender).getTargetBlock((Set<Material>) null, 20);
		CommandValidate.notNull(block, "Il blocco è troppo lontano.");
		
		BlockState blockstate = block.getState();
		CommandValidate.isTrue(blockstate != null && blockstate instanceof Sign, "Non stai guardando un cartello.");
		
		int line;
		
		if (args[0].equalsIgnoreCase("*")) {
			line = 0;
		} else {
			line = CommandValidate.getInteger(args[0]);
			CommandValidate.isTrue(1 <= line && line <= 4, "Inserisci un numero fra 1 e 4.");
		}
		
		int line0 = line - 1;
		Sign sign = (Sign) blockstate;

		String newColor = WildCommons.color(args[1]);
		CommandValidate.isTrue(ChatColor.stripColor(newColor).isEmpty(), "Hai inserito dei non-colori.");
		
		if (line0 == -1) {
			String[] lines = sign.getLines();
			for (int i = 0; i < lines.length; i++) {
				sign.setLine(i, lines[i].replaceAll(("(?i)(§[1-9A-FK-O])+"), newColor));
			}
		} else {
			sign.setLine(line0, sign.getLine(line0).replaceAll(("(?i)(§[1-9A-FK-O])+"), newColor));
		}
		
		sign.update(false, false);
		
		sender.sendMessage(ChatColor.GREEN + "Hai cambiato colore nel cartello.");
	}
	
	
	@SubCommand("book")
	@SubCommandUsage("<file>")
	@SubCommandMinArgs(1)
	public void bookSub(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		
		String path = args[0];
		File bookFile = new File(path);
		
		CommandValidate.isTrue(bookFile.isFile(), "File non trovato.");
		
		new BookTutorial(WildCommonsPlugin.instance, bookFile, "BookCommand", "WildCommons").giveTo(player);
		player.sendMessage(ChatColor.GREEN + "Hai ricevuto il libro!");
	}
	
	
	
	@SubCommand("translation")
	public void translationSub(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.DARK_GREEN + "Comandi traduzioni:");
			sender.sendMessage(ChatColor.GREEN + "/" + label + " translation todo");
			sender.sendMessage(ChatColor.GREEN + "/" + label + " translation reload");
			return;
		}
		
		if (args[0].equalsIgnoreCase("todo")) {
			List<String> missingMaterials = Lists.newArrayList();
			List<String> missingEnchants = Lists.newArrayList();
			List<String> missingPotionEffects = Lists.newArrayList();
			List<String> missingEntityTypes = Lists.newArrayList();
			
			for (Entry<Material, String> entry : WildCommonsPlugin.materialTranslationsMap.entrySet()) {
				if (entry.getValue().equals(entry.getKey().toString())) {
					// Se è uguale al toString(), non è tradotto.
					missingMaterials.add(entry.getKey().toString());
				}
			}
			
			for (Entry<Enchantment, String> entry : WildCommonsPlugin.enchantmentTranslationsMap.entrySet()) {
				if (entry.getValue().equals(entry.getKey().getName())) {
					// Se è uguale al getName(), non è tradotto.
					missingEnchants.add(entry.getKey().getName());
				}
			}
			
			for (Entry<PotionEffectType, String> entry : WildCommonsPlugin.potionEffectsTranslationsMap.entrySet()) {
				if (entry.getValue().equals(entry.getKey().getName())) {
					// Se è uguale al getName(), non è tradotto.
					missingPotionEffects.add(entry.getKey().getName());
				}
			}
			
			for (Entry<EntityType, String> entry : WildCommonsPlugin.entityTypesTranslationsMap.entrySet()) {
				if (entry.getValue().equals(entry.getKey().toString())) {
					// Se è uguale al toString(), non è tradotto.
					missingEntityTypes.add(entry.getKey().toString());
				}
			}

			sender.sendMessage(ChatColor.DARK_GREEN + "Materiali non tradotti (" + missingMaterials.size() + "):");
			sender.sendMessage(ChatColor.GREEN + Joiner.on(", ").join(missingMaterials));
			sender.sendMessage(ChatColor.DARK_GREEN + "Incantesimi non tradotti (" + missingEnchants.size() + "):");
			sender.sendMessage(ChatColor.GREEN + Joiner.on(", ").join(missingEnchants));
			sender.sendMessage(ChatColor.DARK_GREEN + "Effetti non tradotti (" + missingPotionEffects.size() + "):");
			sender.sendMessage(ChatColor.GREEN + Joiner.on(", ").join(missingPotionEffects));
			sender.sendMessage(ChatColor.DARK_GREEN + "Entità non tradotte (" + missingEntityTypes.size() + "):");
			sender.sendMessage(ChatColor.GREEN + Joiner.on(", ").join(missingEntityTypes));
			return;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			try {
				WildCommonsPlugin.instance.loadTranslations();
				sender.sendMessage(ChatColor.GREEN + "Traduzioni ricaricate.");
			} catch (Exception ex) {
				sender.sendMessage(ChatColor.RED + "Impossibile leggere le traduzioni, guarda la console.");
				WildCommonsPlugin.instance.getLogger().log(Level.WARNING, "Impossibile leggere le traduzioni", ex);
			}
			return;
		}
	}
	
	@SubCommand("lagtest")
	@SubCommandUsage("[-bar]")
	public void lagtestSub(CommandSender sender, String label, String[] args) {
		LagtestRunnable previousTask = LagtestRunnable.tasks.get(sender);
		if (previousTask == null) {
			LagtestRunnable newTask = new LagtestRunnable(sender, args.length > 0 && args[0].equalsIgnoreCase("-bar"));
			LagtestRunnable.tasks.put(sender, newTask);
			newTask.runTaskTimer(WildCommonsPlugin.instance, 1, 1);
			sender.sendMessage(ChatColor.GREEN + "Lagtest avviato.");
			
		} else {
			previousTask.cancel();
			LagtestRunnable.tasks.remove(sender);
			sender.sendMessage(ChatColor.GREEN + "Lagtest stoppato.");
		}
	}

	@SuppressWarnings("deprecation")
	@SubCommand("debug")
	public void debugSub(CommandSender sender, String label, String[] args) {
		final Player player = CommandValidate.getPlayerSender(sender);

		WildCommons.sendActionBar(player, ChatColor.GREEN + "Test" + ChatColor.AQUA + "Test");
		WildConstants.Titles.sendCountdownStarted(player, 10);
	}

}
