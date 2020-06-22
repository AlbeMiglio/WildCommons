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
package wild.api.item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import wild.api.WildConstants;

/**
 * Questa classe legge il file 'book.yml' dalla cartella del plugin, e lo crea se non presente.
 */
public class BookTutorial {

	private final ItemStack book;
	
	public void giveTo(Player player) {
		player.getInventory().addItem(book);
	}
	
	public void giveTo(Inventory inventory) {
		inventory.addItem(book);
	}
	
	public ItemStack getItemStack() {
		return book;
	}
	
	public BookTutorial(Plugin plugin, String mode) {
		this(plugin, WildConstants.Messages.getTutorialBookTitle(mode), WildConstants.Messages.getTutorialBookAuthor(mode));
	}
	
	@Deprecated
	public BookTutorial(Plugin plugin, String title, String author) {
		this(plugin, new File(plugin.getDataFolder(), "book.yml"), title, author);
	}
	
	@Deprecated
	public BookTutorial(Plugin plugin, File file, String title, String author) {

		// Evita NullPointerException, alla peggio rimane vuoto.
		book = new ItemStack(Material.WRITTEN_BOOK);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				plugin.getLogger().log(Level.WARNING, "Impossibile creare 'book.yml'", ex);
				return;
			}
		}
		
		YamlConfiguration config = new YamlConfiguration();
	    try {
			config.load(file);
		} catch (FileNotFoundException ex) {
			plugin.getLogger().log(Level.WARNING, "Impossibile leggere 'book.yml': file non trovato", ex);
			return;
		} catch (IOException ex) {
			plugin.getLogger().log(Level.WARNING, "Impossibile leggere 'book.yml': errore I/O", ex);
			return;
		} catch (InvalidConfigurationException ex) {
			plugin.getLogger().log(Level.WARNING, "Impossibile leggere 'book.yml': configurazione non valida", ex);
			return;
		}

		if (!config.isList("content")) {
			config.set("content", Arrays.asList("1", "2","3"));
			plugin.getLogger().info("Aggiunti valori di default a 'book.yml'.");
			try {
				config.save(file);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.WARNING, "Impossibile salvare 'book.yml'", ex);
			}
		}
		
		List<String> content = config.getStringList("content");
		
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setTitle(title);
		meta.setAuthor(author);
		
		List<String> pages = new ArrayList<String>();
		pages.add("");
		
		for(String line : content)  {
			if (line.equals("<newpage>")) {
				pages.add("");
			} else {
				line = ChatColor.translateAlternateColorCodes('&', line).replace("->", "➡");
				int index = pages.size() - 1;
				pages.set(index, pages.get(index) + line + "\n");
			}
		}
		
		meta.setPages(pages);
		book.setItemMeta(meta);
	}

}
