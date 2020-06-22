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

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import wild.core.WildCommonsPlugin;
import wild.core.commands.FireworkCommand;

class PC_BukkitListener implements Listener {
	
	protected static void enable() {
		Bukkit.getPluginManager().registerEvents(new PC_BukkitListener(), WildCommonsPlugin.instance);
	}
	
	private PC_BukkitListener() { }
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String[] command = event.getMessage().toLowerCase().split(" ");
		String commandRoot = command[0];
		
		if (CosmeticsBridge.OPEN_COMMAND_ALIASES.contains(commandRoot)) {
			if (CosmeticsBridge.checkOpenMenu(event)) {
				event.setMessage("/procosmetics open main");
			}
		} else if (CosmeticsBridge.PRO_COSMETICS_ALIASES.contains(commandRoot)) {
			if (command.length > 1 && command[1].equals("open")) {
				CosmeticsBridge.checkOpenMenu(event);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onInteract(PlayerInteractEvent event) {
		if (event.hasItem()
			&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			&& event.getItem().isSimilar(CosmeticsBridge.FIREWORK_ITEM)) {
				event.setCancelled(true);
				FireworkCommand.tryUse(event.getPlayer());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInvClick(InventoryClickEvent event) {
		// Fix per oggetti custom inseriti da qui e eventuali bug di ProCosmetics
		if (CosmeticsBridge.isCosmeticsMenu(event.getInventory())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInvOpen(InventoryOpenEvent event) {
		Inventory openInventory = event.getInventory();
		if (CosmeticsBridge.isCosmeticsMainMenu(openInventory)) {
			for (Pair<Integer, ItemStack> extraItem : CosmeticsBridge.mainMenuExtraItems) {
				openInventory.setItem(extraItem.getLeft(), extraItem.getRight());
			}
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent event) {
		if (CosmeticsBridge.isCosmeticsMenu(event.getInventory())) {
			CosmeticsBridge.playerMenuCloseTime.put((Player) event.getPlayer(), System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		CosmeticsBridge.playerStatuses.remove(event.getPlayer());
		CosmeticsBridge.playerMenuCloseTime.remove(event.getPlayer());
	}

}
