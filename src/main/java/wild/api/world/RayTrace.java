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
package wild.api.world;

import static wild.core.WildCommonsPlugin.nmsManager;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RayTrace {

	/**
	 * Blocco in vista, anche se lontano. Posizione precisa.
	 * Se non ci sono blocchi, semplicemente un punto abbastanza distante nella direzione in cui punta.
	 */
	public static Location getSight(Player player) {
		return nmsManager.getBlockInSight(player);
	}
	
	public static boolean isInsideBlock(Location loc) {
		return nmsManager.isInsideBlock(loc);
	}
	
	public static boolean isSuffocatingInsideBlock(Player player) {
		return nmsManager.isSuffocatingInsideBlock(player);
	}
	
	/**
	 * Primo blocco che si incontra andando da start a end. Posizione precisa.
	 * ATTENZIONE: null se non trovato.
	 */
	public static Location getSight(Location start, Location end) {
		return nmsManager.getBlockInSight(start, end);
	}
	
	/**
	 * Controllo su tutti i giocatori nello stesso mondo.
	 */
	public static SightInfo getSightIncludePlayers(Player player) {
		return getSightIncludePlayers(player, player.getWorld().getPlayers());
	}
	
	/**
	 * Controllo su una lista ristretta di giocatori.
	 */
	public static SightInfo getSightIncludePlayers(Player player, List<Player> possibleTargets) {
		return getSightIncludePlayers(player, possibleTargets, 0.3);
	}
	
	/**
	 * Controllo su una lista ristretta di giocatori e con un bounding box incrementato.
	 */
	public static SightInfo getSightIncludePlayers(Player player, List<Player> possibleTargets, double boundingBoxIncrement) {
		return nmsManager.getPlayerInSight(player, possibleTargets, boundingBoxIncrement);
	}
}
