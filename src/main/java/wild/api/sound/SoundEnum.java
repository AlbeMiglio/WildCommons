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

import java.util.Map;

import org.bukkit.Sound;

import com.google.common.collect.Maps;

import wild.core.WildCommonsPlugin;

public class SoundEnum {
	
	private static Map<String, Sound> soundsEnumMap = Maps.newHashMap();
	private static Sound missingSound;
	
	static {
		for (Sound sound : Sound.values()) {
			soundsEnumMap.put(sound.name(), sound);
		}
		
		addOrSwap("NOTE_BASS", "BLOCK_NOTE_BASS");
		addOrSwap("NOTE_PLING", "BLOCK_NOTE_PLING");
		addOrSwap("CLICK", "UI_BUTTON_CLICK");
		addOrSwap("ANVIL_LAND", "BLOCK_ANVIL_LAND");
		addOrSwap("NOTE_PIANO", "BLOCK_NOTE_HARP");
		addOrSwap("NOTE_STICKS", "BLOCK_NOTE_HAT");
		addOrSwap("LEVEL_UP", "ENTITY_PLAYER_LEVELUP");
		
		missingSound = get("NOTE_STICKS");
	}
	
	private static void addOrSwap(String sound1, String sound2) {
		try {
			soundsEnumMap.put(sound1, Sound.valueOf(sound2));
			//WildCommonsPlugin.instance.getLogger().info("Added " + sound1 + " as compatibility for " + sound2);
		} catch (IllegalArgumentException e) {
			// The other mapping will work
		}
		
		try {
			soundsEnumMap.put(sound2, Sound.valueOf(sound1));
			//WildCommonsPlugin.instance.getLogger().info("Added " + sound2 + " as compatibility for " + sound1);
		} catch (IllegalArgumentException e) {
			// The other mapping will work
		}
	}
	
	public static Sound get(String enumName) {
		Sound sound = soundsEnumMap.get(enumName);
		if (sound != null) {
			return sound;
		} else {
			WildCommonsPlugin.instance.getLogger().warning("Unknown or unmapped sound: " + enumName);
			return missingSound;
		}
	}
	

}
