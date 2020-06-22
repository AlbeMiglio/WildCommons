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

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldInitEvent;

import wild.api.menu.Icon;
import wild.api.menu.IconMenu;
import wild.api.menu.MenuInventoryHolder;
import wild.api.world.SpectatorAPI;

public class WildCommonsListener implements Listener {
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onWorldLoad(WorldInitEvent event) {
		if (WildCommonsPlugin.disableWorldPlayerSave) {
			try {
				WildCommonsPlugin.nmsManager.disablePlayerSave(event.getWorld());
			} catch (Throwable t) {
				WildCommonsPlugin.instance.getLogger().log(Level.SEVERE, "Impossibile disabilitare il salvataggio dei giocatori nel mondo " + event.getWorld().getName(), t);
			}
		}
	}

	@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getHolder() instanceof MenuInventoryHolder) {
			
			event.setCancelled(true); // First thing to do, if an exception is thrown at least the player doesn't take the item.
			
			IconMenu menu = ((MenuInventoryHolder) event.getInventory().getHolder()).getIconMenu();
			int slot = event.getRawSlot();
			
			if (slot >= 0 && slot < menu.getSize()) {
				Icon icon = menu.getIconAt(slot);
				
				if (icon != null) {
					icon.onClick((Player) event.getWhoClicked());
					
					if (icon.isCloseOnClick()) {
						Bukkit.getScheduler().runTaskLater(WildCommonsPlugin.instance, () -> event.getWhoClicked().closeInventory(), 1);
					}
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof MenuInventoryHolder) {
			((MenuInventoryHolder) event.getInventory().getHolder()).getIconMenu().onClose((Player) event.getPlayer());
		}
	}
	
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void playerLogin(PlayerLoginEvent event) {
		if (event.getResult() == Result.KICK_FULL && event.getPlayer().hasPermission("wildcommons.joinfull")) {
			event.allow();
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		SpectatorAPI.removeSpectatorSilent(event.getPlayer());
		event.getPlayer().leaveVehicle(); // TODO: Hotfix for teleport on join
	}
	
}
