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
package wild.api.item.parsing;

import java.util.Map;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.ImmutableMap;

import lombok.NonNull;

public class PotionEffectParser {
	
	private static final Map<String, PotionEffectType> NAME_ALIASES = ImmutableMap.<String, PotionEffectType>builder()
			.put("strength", PotionEffectType.INCREASE_DAMAGE)
			.put("resistance", PotionEffectType.DAMAGE_RESISTANCE)
			.put("slowness", PotionEffectType.SLOW)
			.put("haste", PotionEffectType.FAST_DIGGING)
			.put("nausea", PotionEffectType.CONFUSION)
			.build();
	
	
	public static PotionEffect parse(String input) throws ParserException {
		return parse(input, true, true);
	}
	
	public static PotionEffect parse(String input, boolean ambient) throws ParserException {
		return parse(input, ambient, true);
	}
	
	public static PotionEffect parse(@NonNull String input, boolean ambient, boolean particles) throws ParserException {
		
		input = input.replace(" ", "");
		
		// Parse duration
		int duration = 0;
		
		String[] splitDuration = input.split(",");
		if (splitDuration.length < 2) {
			throw new ParserException("duration not set");
		}
			
		duration = ParserUtils.parseInteger(splitDuration[1], -1, "invalid duration (" + splitDuration[1] + ")");
		if (duration == 0) {
			throw new ParserException("duration cannot be 0");
		}
		input = splitDuration[0];
		
		// Parse level
		int level = 1;
		
		String[] splitLevel = input.split(":");
		if (splitLevel.length > 1) {
			level = ParserUtils.parseInteger(splitLevel[1], 1, "invalid level (" + splitLevel[1] + ")");
			input = splitLevel[0];
		}
		
		// Parse potion type
		PotionEffectType type = ParserUtils.match(input, PotionEffectType.values(), NAME_ALIASES, PotionEffectType::getName, "potion type not found (" + input + ")");

		int durationTicks;
		if (duration > 0) {
			durationTicks = duration * 20;
		} else {
			durationTicks = Integer.MAX_VALUE;
		}
		
		return new PotionEffect(type, durationTicks, level - 1, ambient, particles);
	}

}
