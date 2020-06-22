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
import java.util.Random;
import java.util.WeakHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import wild.api.command.CommandFramework;
import wild.api.world.SpectatorAPI;

public class FireworkCommand extends CommandFramework {

	
	private static Map<Player, Long> cooldown = new WeakHashMap<>();
	private static Random random = new Random();
	private static Color[] niceColors = {
		Color.fromRGB(255, 0, 0),
		Color.fromRGB(255, 128, 0),
		Color.fromRGB(255, 255, 0),
		Color.fromRGB(128, 255, 0),
		Color.fromRGB(0, 255, 0),
		Color.fromRGB(0, 255, 128),
		Color.fromRGB(0, 255, 255),
		Color.fromRGB(0, 128, 255),
		Color.fromRGB(0, 0, 255),
		Color.fromRGB(128, 0, 255),
		Color.fromRGB(255, 0, 255),
		Color.fromRGB(255, 0, 128),
		Color.fromBGR(255, 255, 255),
		Color.fromBGR(128, 128, 128),
		Color.fromBGR(0, 0, 0)
	};
	
	
	public FireworkCommand(JavaPlugin plugin, String label, String... aliases) {
		super(plugin, label, aliases);
	}


	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender && args.length > 0 && args[0].equalsIgnoreCase("debug")) {
			sender.sendMessage("Map size: " + cooldown.size());
			return;
		}
		
		Player player = CommandValidate.getPlayerSender(sender);
		tryUse(player);
	}
	
	private static <T> T randomInArray(T[] array) {
		return array[random.nextInt(array.length)];
	}
	
	public static void tryUse(Player player) {
		if (!player.hasPermission("cosmetics.firework")) {
			player.sendMessage(ChatColor.RED + "Devi essere VIP per usarlo.");
			return;
		}
		
		if (SpectatorAPI.isSpectator(player)) {
			player.sendMessage(ChatColor.RED + "Non puoi usarlo ora.");
			return;
		}
		
		if (cooldown.containsKey(player)) {
			long lastTimeUsed = cooldown.get(player);
			long now = System.currentTimeMillis();
			
			if (now - lastTimeUsed < 10000) {
				int nextUseIn = (int) Math.ceil((10000 - (now - lastTimeUsed)) / 1000.0);
				player.sendMessage(ChatColor.RED + "Potrai usarlo nuovamente tra " + nextUseIn + (nextUseIn == 1 ? " secondo." : " secondi."));
				return;
			}
		}
		
		cooldown.put(player, System.currentTimeMillis());
		
		FireworkEffect effect = FireworkEffect.builder()
			.flicker(random.nextBoolean())
			.trail(random.nextBoolean())
			.with(randomInArray(Type.values()))
			.withColor(randomInArray(niceColors))
			.withFade(randomInArray(niceColors))
		.build();
			
		Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		meta.setPower(1);
		firework.setFireworkMeta(meta);
		player.sendMessage(ChatColor.GREEN + "Hai lanciato un fuoco d'artificio!");
	}

}
