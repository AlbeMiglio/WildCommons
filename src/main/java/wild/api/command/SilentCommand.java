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
package wild.api.command;

import java.util.Map;

import lombok.NonNull;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Maps;

/**
 * Un comando usato per le API, senza passare da Bukkit o usando Player.chat(...)
 */
public class SilentCommand {
	
	private static Map<String, SilentCommandHandler> map = Maps.newHashMap();
	
	public static void execute(String pluginName, Player player, String command) {
		execute(pluginName, player, command, new String[0]);
	}

	public static void execute(@NonNull String pluginName, @NonNull Player player, @NonNull String command, @NonNull String[] args) {
		execute(pluginName + ":" + command, player, args);
	}
	
	@Deprecated
	public static void execute(@NonNull String pluginNameAndCommand, @NonNull Player player, @NonNull String[] args) {
		SilentCommandHandler cmd = map.get(pluginNameAndCommand.toLowerCase());
		if (cmd != null) {
			try {
				cmd.execute(player, args);
			} catch (Exception ex) {
				ex.printStackTrace();
				player.sendMessage(ChatColor.RED + "Errore interno nell'esecuzione del comando.");
			}
		}
	}
	
	public static void register(@NonNull Plugin plugin, @NonNull String command, @NonNull SilentCommandHandler handler) {
		map.put(plugin.getName().toLowerCase() + ":" + command.toLowerCase(), handler);
	}
}
