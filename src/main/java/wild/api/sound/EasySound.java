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
package wild.api.sound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import wild.core.utils.SoundDetails;

/**
 * Una classe che può memorizzare un suono in modo semplice.
 */
public class EasySound {

	private SoundDetails details;
	
	public EasySound(Sound sound) {
		this(sound, 1f, 1f);
	}
	
	public EasySound(Sound sound, float pitch) {
		this(sound, pitch, 1f);
	}
	
	public EasySound(Sound sound, float pitch, float volume) {
		details = new SoundDetails(sound, pitch, volume);
	}
	
	public void playTo(Player player) {
		playTo(player, player.getLocation());
	}
	
	public void playTo(Player player, Location location) {
		player.playSound(location, details.getSound(), details.getVolume(), details.getPitch());
	}
	
	public void playToAll() {
		for (Player player: Bukkit.getOnlinePlayers()) {
			playTo(player);
		}
	}
	
	public void playToAll(Location location) {
		location.getWorld().playSound(location, details.getSound(), details.getVolume(), details.getPitch());
	}
	
	public static void quickPlay(Player player, Sound sound) {
		player.playSound(player.getLocation(), sound, 1f, 1f);
	}
	
	public static void quickPlay(Player player, Sound sound, float pitch) {
		player.playSound(player.getLocation(), sound, 1f, pitch);
	}
	
	public static void quickPlay(Player player, Sound sound, float pitch, float volume) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
	
	public static void quickPlayAll(Sound sound) {
		quickPlayAll(sound, 1f, 1f);
	}
	
	public static void quickPlayAll(Sound sound, float pitch) {
		quickPlayAll(sound, pitch, 1f);
	}
	
	public static void quickPlayAll(Sound sound, float pitch, float volume) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), sound, volume, pitch);
		}
	}
}
