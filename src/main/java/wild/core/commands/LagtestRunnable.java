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

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

import wild.api.WildCommons;

public class LagtestRunnable extends BukkitRunnable {
	
	private static final String[] ANIMATION = {
			"§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌§a▌§8▌▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌§a▌§8▌▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌§a▌§8▌▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌§a▌§8▌▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌▌§a▌§8▌▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌▌▌§a▌§8▌▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌§a▌§8▌▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§a▌§8▌▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§a▌§8▌▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§a▌§8▌▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§a▌§8▌",
			"§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§a▌"
	};
	
	public static Map<CommandSender, LagtestRunnable> tasks = Maps.newHashMap();
	
	private CommandSender sender;
	private boolean alwaysShowBar;
	private int index;
	private long lastTimestamp;
	
	
	public LagtestRunnable(CommandSender sender, boolean alwaysShowBar) {
		this.sender = sender;
		this.alwaysShowBar = alwaysShowBar;
	}


	@Override
	public void run() {
		Player player = sender instanceof Player ? (Player) sender : null;
		
		if (player != null && !player.isOnline()) {
			tasks.remove(sender);
			cancel();
			return;
		}
		
		long now = System.currentTimeMillis();
		if (lastTimestamp != 0) {
			if (now - lastTimestamp > 100) {
				sender.sendMessage(ChatColor.RED + "Picco di lag: sono passati "  + (now - lastTimestamp) + " ms invece del normale tick (< 50 ms).");
			}
		}
		lastTimestamp = now;
		
		if (player != null) {
			WildCommons.sendActionBar(player, ANIMATION[index]);
		} else if (alwaysShowBar) {
			sender.sendMessage(ANIMATION[index].replace("▌", "#"));
		}
		
		if (++index >= ANIMATION.length) {
			index = 0;
		}
	}

}
