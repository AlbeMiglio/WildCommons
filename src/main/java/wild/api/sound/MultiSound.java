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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import wild.core.utils.SoundDetails;

/**
 * Una classe che può memorizzare più suoni, con costruttore statico.
 */
public class MultiSound {

	private List<SoundDetails> sounds;
	
	public static MultiSound of() {
		return new MultiSound();
	}
	
	public static MultiSound of(Sound sound) {
		return new MultiSound().add(sound);
	}
	
	public static MultiSound of(Sound sound, float pitch) {
		return new MultiSound().add(sound, pitch);
	}
	
	public static MultiSound of(Sound sound, float pitch, float volume) {
		return new MultiSound().add(sound, pitch, volume);
	}
	
	private MultiSound() {
		sounds = new ArrayList<SoundDetails>();
	}
	
	public MultiSound add(Sound sound) {
		return add(sound, 1, 1);
	}
	
	public MultiSound add(Sound sound, float pitch) {
		return add(sound, pitch, 1);
	}
	
	public MultiSound add(Sound sound, float pitch, float volume) {
		sounds.add(new SoundDetails(sound, pitch, volume));
		return this;
	}
	
	public void playTo(Player player) {
		playTo(player, player.getLocation());
	}
	
	public void playTo(Player player, Location location) {
		for (SoundDetails details : sounds) {
			player.playSound(location, details.getSound(), details.getVolume(), details.getPitch());
		}
	}
	
	public void playToAll() {
		for (Player player: Bukkit.getOnlinePlayers()) {
			playTo(player);
		}
	}
	
	
	public void playToAll(Location location) {
		for (SoundDetails details : sounds) {
			location.getWorld().playSound(location, details.getSound(), details.getVolume(), details.getPitch());
		}
	}
	
}
