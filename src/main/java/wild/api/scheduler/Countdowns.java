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
package wild.api.scheduler;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import wild.api.WildCommons;
import wild.api.util.UnitFormatter;
import wild.api.WildConstants;

public class Countdowns {
	
	/**
	 * Stabilisce se un messaggio di countdown deve essere mandato o meno.
	 */
	public static boolean shouldAnnounceCountdown(int seconds) {
		if (seconds >= 60) {
			return seconds % 15 == 0;
			
		} else if (seconds >= 10) {
			return seconds % 10 == 0;
			
		} else {
			return seconds > 0 && seconds <= 5;
		}
	}
	
	
	
	/**
	 * Ritorna la stringa di tempo formattata. Ignora se deve essere mandato o meno in base ai secondi!
	 */
	public static String announceStartingCountdown(String gameChatPrefix, Player player, int seconds) {
		String formattedTime = UnitFormatter.formatMinutesOrSeconds(seconds);
		player.sendMessage(gameChatPrefix + ChatColor.YELLOW + "La partita inizia fra " + formattedTime + ".");
		
		if (seconds <= 5) {
			WildConstants.Sounds.COUNTDOWN_TIMER.playTo(player);
			WildCommons.sendTitle(player, 0, 40, 5, ChatColor.RED + "" + seconds, ChatColor.YELLOW  + "La partita sta per cominciare!");
		}
			
		return formattedTime;
	}
	
	public static String announceStartingCountdown(String gameChatPrefix, Collection<Player> players, int seconds) {
		String formattedTime = UnitFormatter.formatMinutesOrSeconds(seconds);
		
		for (Player player : players) {
			player.sendMessage(gameChatPrefix + ChatColor.YELLOW + "La partita inizia fra " + formattedTime + ".");
			
			if (seconds <= 5) {
				WildConstants.Sounds.COUNTDOWN_TIMER.playTo(player);
				WildCommons.sendTitle(player, 0, 40, 5, ChatColor.RED + "" + seconds, ChatColor.YELLOW  + "La partita sta per cominciare!");
			}
		}
			
		return formattedTime;
	}
	
	
	

	/**
	 * Annuncia la fine del countdown.
	 */
	public static void announceEndedCountdown(String gameChatPrefix, Player player) {
		player.sendMessage(gameChatPrefix + ChatColor.RED + "La partita è iniziata!");
		
		WildConstants.Sounds.COUNTDOWN_FINISH.playTo(player);
		WildCommons.sendTitle(player, 5, 30, 10, ChatColor.RED + "Via!", ChatColor.YELLOW  + "La partita è iniziata!");
	}
	
	public static void announceEndedCountdown(String gameChatPrefix, Collection<Player> players) {
		for (Player player : players) {
			player.sendMessage(gameChatPrefix + ChatColor.RED + "La partita è iniziata!");
			
			WildConstants.Sounds.COUNTDOWN_FINISH.playTo(player);
			WildCommons.sendTitle(player, 5, 30, 10, ChatColor.RED + "Via!", ChatColor.YELLOW  + "La partita è iniziata!");
		}
	}

}
