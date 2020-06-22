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

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import wild.api.bridges.protocollib.WrapperPlayClientWindowClick;
import wild.api.bridges.protocollib.WrapperPlayServerSetSlot;
import wild.api.bridges.protocollib.WrapperPlayServerTransaction;
import wild.api.bridges.protocollib.WrapperPlayServerWindowItems;
import wild.api.sound.EasySound;
import wild.core.WildCommonsPlugin;

class PC_PacketListener extends PacketAdapter {
	
	protected static void enable() {
		//WrappedChatComponent messageToFilter = WrappedChatComponent.fromJson("{'extra':['{null}'],'text':''}".replace("'", "\""));
		AdapterParameteters params = PacketAdapter.params()
				.plugin(WildCommonsPlugin.instance)
				.types(PacketType.Play.Server.CHAT, PacketType.Play.Client.WINDOW_CLICK)
				.listenerPriority(ListenerPriority.NORMAL);
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PC_PacketListener(params));
	}
	
	private PC_PacketListener(AdapterParameteters params) {
		super(params);
	}
		  
	@Override
	public void onPacketSending(PacketEvent event) {
		WrappedChatComponent message = event.getPacket().getChatComponents().read(0);
		if (message == null) {
			return;
		}
		
		//if(message.equals(messageToFilter)) {
		//	event.setCancelled(true);
		//}
		
		if (message.getJson().contains(CosmeticsBridge.FILTER_WITHOUT_MENU_OPEN_CODE)) {
			Inventory openInventory = event.getPlayer().getOpenInventory().getTopInventory();
			
			if (!CosmeticsBridge.isCosmeticsMenu(openInventory)) {
				Long lastMenuClose = CosmeticsBridge.playerMenuCloseTime.get(event.getPlayer());
				
				long timeSinceClose = System.currentTimeMillis() - (lastMenuClose != null ? lastMenuClose.longValue() : 0);
				if (timeSinceClose > 200) {
					// E' passato troppo tempo dall'ultima volta che è stato chiuso il menù, dipende da altri fattori allora
					event.setCancelled(true);
				}
			}
		}
	}
	
	@Override
	public void onPacketReceiving(PacketEvent event) {
		WrapperPlayClientWindowClick windowClick = new WrapperPlayClientWindowClick(event.getPacket());
		Inventory openInventory = event.getPlayer().getOpenInventory().getTopInventory();
		int slot = windowClick.getSlot();

		if (openInventory == null
			|| slot < 0
			|| slot >= openInventory.getSize()
			|| !CosmeticsBridge.isCosmeticsMenu(openInventory)) {
				// Si controlla anche che sia un menu di ProCosmetics
				return;
		}
		
		Map<Integer, String> permissionsBySlot = CosmeticsBridge.permissionsBySlotByMenu.get(openInventory.getTitle());
		if (permissionsBySlot == null) {
			return;
		}
		String permission = permissionsBySlot.get(slot);
		if (permission == null) {
			return;
		}
		
		Player player = event.getPlayer();
		CosmeticsBridge.Status status = CosmeticsBridge.playerStatuses.get(player);
		
		String errorMessage = null;
		
		if (status == CosmeticsBridge.Status.SPECTATOR) {
			errorMessage = "Non puoi attivare cosmetici da spettatore.";
		} else if (status == CosmeticsBridge.Status.GAME) {
			if (!CosmeticsBridge.allowedGameMenuTitles.contains(openInventory.getTitle())) {
				errorMessage = "Non puoi attivare questo tipo di cosmetico in combattimento o durante una partita.";
			}
		}
		
		if (errorMessage == null) {
			if (!player.hasPermission(permission)) {
				errorMessage = "Non hai il permesso per usare questo oggetto.";
			}
		}
		
		if (errorMessage == null) {
			return;
		}
		
		final String finalErrorMessage = errorMessage;
		event.setCancelled(true);
			
		WrapperPlayServerTransaction transaction = new WrapperPlayServerTransaction();
		transaction.setWindowId(windowClick.getWindowId());
		transaction.setActionNumber(windowClick.getActionNumber());
		transaction.setAccepted(true);
		
		WrapperPlayServerWindowItems windowItems = new WrapperPlayServerWindowItems();
		windowItems.setWindowId(windowClick.getWindowId());
		windowItems.setSlotData(openInventory.getContents());

		WrapperPlayServerSetSlot setSlot = new WrapperPlayServerSetSlot();
		setSlot.setWindowId(-1);
		setSlot.setSlot(-1);
		setSlot.setSlotData(null);
		
		transaction.sendPacket(player);
		windowItems.sendPacket(player);
		setSlot.sendPacket(player);
		
		Bukkit.getScheduler().runTask(WildCommonsPlugin.instance, () -> {
			player.sendMessage(ChatColor.RED + finalErrorMessage);
			EasySound.quickPlay(player, CosmeticsBridge.DENIED_COSMETIC_USE_SOUND);
		});
	}

}
