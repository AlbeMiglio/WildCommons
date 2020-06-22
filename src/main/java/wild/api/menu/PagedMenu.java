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
package wild.api.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import lombok.NonNull;
import wild.api.sound.EasySound;

public class PagedMenu {
	
	private static final Sound PAGE_CHANGE_SOUND = Sound.UI_BUTTON_CLICK;
	
	private final String name;
	private final int slotsPerPage;
	private final int totalRows;
	
	private List<Icon> icons;
	private List<IconMenu> pagesMenus;
	
	public PagedMenu(@NonNull String name, int rows) {
		if (rows < 1 || rows > 5) {
			throw new IllegalArgumentException("Rows must be between 1 and 5");
		}
		this.name = name;
		this.slotsPerPage = rows * 9;
		this.totalRows = rows + 1;
		this.icons = Lists.newArrayList();
		this.pagesMenus = Lists.newArrayList();
	}

	
	public void addIcon(@NonNull Icon icon) {
		this.icons.add(icon);
	}
	
	public void clearIcons() {
		this.icons.clear();
	}
	
	public void refresh(@NonNull Icon icon) {
		for (IconMenu menu : pagesMenus) {
			menu.refresh(icon);
		}
	}
	
	public void update() {
		// Approssima per eccesso, minimo 1 pagina
		int pages = icons.size() % slotsPerPage == 0 ? (icons.size() / slotsPerPage) : (icons.size() / slotsPerPage) + 1;
		if (pages < 1) {
			pages = 1;
		}
		
		// Aggiunge o toglie pagine in base al numero giusto
		while (pagesMenus.size() < pages) {
			pagesMenus.add(new IconMenu(name, totalRows));
		}
		while (pagesMenus.size() > pages) {
			pagesMenus.remove(pagesMenus.size() - 1);
		}
		
		// Pulisce tutti i menu, per riutilizzarli
		for (IconMenu menu : pagesMenus) {
			menu.clearIcons();
		}
		
		int index = 0;
		for (Icon icon : icons) {
			int page = index / slotsPerPage;
			int slot = index % slotsPerPage;

			pagesMenus.get(page).setIconRaw(slot, icon);
			
			index++;
		}
		
		
		if (pages > 1) {
			for (int page = 0; page < pagesMenus.size(); page++) {
				IconMenu currentPageMenu = pagesMenus.get(page);
				
				if (page > 0) {
					// Se non è il primo, visualizza la pagina precedente
					IconMenu previousPageMenu = pagesMenus.get(page - 1);
					
					currentPageMenu.setIcon(4, totalRows, new IconBuilder(Material.ARROW).name(ChatColor.WHITE + "Pagina precedente").clickHandler(player -> {
						EasySound.quickPlay(player, PAGE_CHANGE_SOUND, 1.6f, 0.5f);
						previousPageMenu.open(player);
					}).build());
				}
				
				if (page < pagesMenus.size() - 1) {
					// Se non è l'ultimo, visualizza la pagina successiva
					IconMenu nextPageMenu = pagesMenus.get(page + 1);
					
					currentPageMenu.setIcon(6, totalRows, new IconBuilder(Material.ARROW).name(ChatColor.WHITE + "Pagina successiva").clickHandler(player -> {
						EasySound.quickPlay(player, PAGE_CHANGE_SOUND, 1.6f, 0.5f);
						nextPageMenu.open(player);
					}).build());
				}
				
				currentPageMenu.setIcon(5, totalRows, new IconBuilder(Material.PAPER).name(ChatColor.WHITE + "Pagina " + (page + 1)).build());
			}
		}

		// Refresh di tutti i menu
		for (IconMenu menu : pagesMenus) {
			menu.refresh();
		}
	}

	public void open(Player player) {
		if (!pagesMenus.isEmpty()) {
			pagesMenus.get(0).open(player);
		}
	}
}
