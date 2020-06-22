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

import org.bukkit.enchantments.Enchantment;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;

public class EnchantmentParser {
	
	
	private static final Map<String, Enchantment> NAME_ALIASES = ImmutableMap.<String, Enchantment>builder()
		.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL)
		.put("sharpness", Enchantment.DAMAGE_ALL)
		.put("power", Enchantment.ARROW_DAMAGE)
		.put("infinity", Enchantment.ARROW_INFINITE)
		.build();

	public static EnchantmentData parse(@NonNull String input) throws ParserException {
		
		input = input.replace(" ", "");
		
		// Parse level
		int level = 1;
		
		String[] splitLevel = input.split(":");
		if (splitLevel.length > 1) {
			level = ParserUtils.parseInteger(splitLevel[1], 1, "invalid enchantment level (" + splitLevel[1] + ")");
			input = splitLevel[0];
		}
		
		// Parse enchantment
		Enchantment enchantment = ParserUtils.match(input, Enchantment.values(), NAME_ALIASES, Enchantment::getName, "enchantment not found (" + input + ")");

		return new EnchantmentData(enchantment, level);
	}
	
	
	public static EnchantmentData parse(String enchantmentName, Integer optionalLevel) throws ParserException {

		// Parse level
		int level = 1;

		if (optionalLevel != null) {
			if (optionalLevel < 1) {
				throw new ParserException("invalid enchantment level (" + optionalLevel + ")");
			}
			level = optionalLevel;
		}
		
		// Parse enchantment
		Enchantment enchantment = ParserUtils.match(enchantmentName, Enchantment.values(), NAME_ALIASES, Enchantment::getName, "enchantment not found (" + enchantmentName + ")");

		return new EnchantmentData(enchantment, level);
	}

}
