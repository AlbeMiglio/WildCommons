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
package wild.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import wild.api.item.ItemBuilder;
import wild.api.sound.EasySound;
import wild.api.sound.SoundEnum;

public class WildConstants {
	
	
	public static class Titles {
		
		@Deprecated
		public static boolean sendCountdownStarted(Player player, int seconds) {
			WildCommons.sendTitle(player, 0, 40, 5, ChatColor.RED + "" + seconds, ChatColor.YELLOW  + "La partita sta per cominciare!");
			return true;
		}
		
		@Deprecated
		public static boolean sendCountdownEnded(Player player) {
			WildCommons.sendTitle(player, 5, 30, 10, ChatColor.RED + "Via!", ChatColor.YELLOW  + "La partita è iniziata!");
			return true;
		}
		
	}
	
	
	public static class Messages {
		
		public static String getTutorialBookTitle(String mode) {
			return ChatColor.GREEN + "Tutorial " + mode;
		}
		
		public static String getTutorialBookAuthor(String mode) {
			return "Wild Adventure";
		}
		
		public static String getSidebarIP() {
			return "   " + ChatColor.GRAY + "mc.WildAdventure.it" + "   ";
		}

		@Deprecated
		public static void displayIP(Scoreboard scoreboard, Objective objective, int score) {
			
			String text = "   " + ChatColor.GRAY + "mc.WildAdventure.it" + "   ";
			
			String[] parts = {"", "§r§r§r", ""}; // Il nome non deve essere mai vuoto!
			parts[0] = text.substring(0, Math.min(text.length(), 16));
			if (text.length() > 16) {
				parts[1] = text.substring(16, Math.min(text.length(), 32));
				
				if (text.length() > 32) {
					parts[2] = text.substring(32, Math.min(text.length(), 48));
				}
			}
			
			if (scoreboard.getTeam("ipLine-Team") == null) {
				Team team = scoreboard.registerNewTeam("ipLine-Team");
				team.setPrefix(parts[0]);
				team.setSuffix(parts[2]);
				team.addEntry(parts[1]);
			}
			
			Score scoreObject = objective.getScore(parts[1]);
			if (score == 0) {
				scoreObject.setScore(1);
			}
			scoreObject.setScore(score);
		}
		
	}
	
	
	public static class Sounds {
		
		public static final EasySound
		
			COUNTDOWN_TIMER = new EasySound(SoundEnum.get("CLICK")),
			COUNTDOWN_FINISH = new EasySound(SoundEnum.get("ANVIL_LAND"), 1.2f);
		
		
	}
	
	
	public static class Spectator {
		
		
		public static final ItemStack BACK_TO_HUB = ItemBuilder.of(Material.BED)
				.name(ChatColor.RED + "Torna allo hub " + ChatColor.GRAY + "(Click destro)")
				.lore(
						ChatColor.GRAY + "Per tornare all'hub fai click con mouse",
						ChatColor.GRAY + "destro mentre tieni l'oggetto in mano.").build();
		
		public static final ItemStack QUIT_SPECTATING = ItemBuilder.of(Material.BED)
				.name(ChatColor.RED + "Esci dalla modalità spettatore " + ChatColor.GRAY + "(Click destro)")
				.lore(
						ChatColor.GRAY + "Per uscire dalla modalità spettatore fai click",
						ChatColor.GRAY + "con mouse destro mentre tieni l'oggetto in mano.").build();
		
		
		public static final ItemStack TELEPORTER = ItemBuilder.of(Material.COMPASS)
				.name(ChatColor.GREEN + "Teletrasporto " + ChatColor.GRAY + "(Click destro)")
				.lore(
						ChatColor.GRAY + "Per aprire fai click con mouse destro",
						ChatColor.GRAY + "mentre tieni l'oggetto in mano.").build();
		
	}
	
}
