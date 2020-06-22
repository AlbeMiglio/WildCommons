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

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class ItemParser {
	
	private static final JsonParser GOOGLE_GSON_PARSER = new JsonParser();
	
	
	public static ItemStack parse(String input) throws ParserException {
		return parse(input, null);
	}
	
	
	@SuppressWarnings("deprecation")
	public static ItemStack parse(String input, JsonReturner jsonReturner) throws ParserException {
		
		// Parse json
		JsonObject json = null;
		
		int jsonStart = input.indexOf('{');
		int jsonEnd = input.lastIndexOf('}');
		
		if (jsonStart > 0 && jsonEnd > 0 && jsonEnd > jsonStart) {
			String jsonString = input.substring(jsonStart, jsonEnd + 1);
			
			JsonElement jsonElement;
			try {
				jsonElement = GOOGLE_GSON_PARSER.parse(jsonString);
			} catch (JsonSyntaxException e) {
				throw new ParserException("invalid json (" + e.getMessage() + ")");
			}
			
			if (jsonElement instanceof JsonObject) {
				json = (JsonObject) jsonElement;
			} else {
				throw new ParserException("json root must be an object");
			}
			
			input = input.substring(0, jsonStart) + input.substring(jsonEnd + 1, input.length());
		}
		input = input.replace(" ", "");
		

		// Parse amount
		int amount = 1;
		
		String[] splitAmount = input.split(",");
		if (splitAmount.length > 1) {
			amount = ParserUtils.parseInteger(splitAmount[1], 1, "invalid amount (" + splitAmount[1] + ")");
			input = splitAmount[0];
		}
		
		
		// Parse data value
		short dataValue = 0;
		
		String[] splitDataValue = input.split(":");
		if (splitDataValue.length > 1) {
			dataValue = (short) ParserUtils.parseInteger(splitDataValue[1], 1, "invalid data value (" + splitDataValue[1] + ")");
			input = splitDataValue[0];
		}
		
		
		// Parse material
		Material material;
		if (ParserUtils.isInteger(input)) {
			material = Material.getMaterial(Integer.parseInt(input));
		} else {
			material = ParserUtils.match(input, Material.values(), null, Material::name, "invalid material (" + input + ")");
		}
		if (material == Material.AIR) {
			throw new ParserException("material cannot be air");
		}
		
		
		// Build itemstack
		ItemStack itemStack = new ItemStack(material, amount, dataValue);
		
		
		// Apply optional extras
		if (json != null) {
			JsonObject extraJson = ParserUtils.castJson(json.remove("extra"), JsonObject.class, "extra must be an object");
			
			JsonElement enchantsJson = json.remove("enchants");
			String colorJson = ParserUtils.castJson(json.remove("color"), String.class, "color must be a string");
			String nameJson = ParserUtils.castJson(json.remove("name"), String.class, "name must be a string");
			JsonArray loreJson = ParserUtils.castJson(json.remove("lore"), JsonArray.class, "lore must be an array");
			
			if (!json.entrySet().isEmpty()) {
				itemStack = Bukkit.getUnsafe().modifyItemStack(itemStack, CustomJsonWriter.toString(json));
			}
			
			if (enchantsJson != null) {
				if (enchantsJson instanceof JsonArray) {
					for (JsonElement singleEnchantJson : (JsonArray) enchantsJson) {
						EnchantmentData enchantmentData = EnchantmentParser.parse(ParserUtils.castJson(singleEnchantJson, String.class, "single enchantments must be strings"));
						itemStack.addUnsafeEnchantment(enchantmentData.getEnchant(), enchantmentData.getLevel());
					}
				} else if (enchantsJson instanceof JsonObject) {
					for (Entry<String, JsonElement> entry : ((JsonObject) enchantsJson).entrySet()) {
						EnchantmentData enchantmentData = EnchantmentParser.parse(entry.getKey(), ParserUtils.castJson(entry.getValue(), Integer.class, "enchant level must be an integer"));
						itemStack.addUnsafeEnchantment(enchantmentData.getEnchant(), enchantmentData.getLevel());
					}
				} else {
					throw new ParserException("enchants must be a map or an array");
				}
			}
			
			ItemMeta itemMeta = itemStack.getItemMeta();
			
			if (colorJson != null && itemMeta instanceof LeatherArmorMeta) {
				((LeatherArmorMeta) itemMeta).setColor(ColorParser.parse(colorJson));
			}
			if (nameJson != null) {
				itemMeta.setDisplayName(nameJson);
			}
			if (loreJson != null) {
				List<String> lore = Lists.newArrayList();
				for (JsonElement loreLineJson : loreJson) {
					lore.add(ParserUtils.castJson(loreLineJson, String.class, "lore lines must be strings"));
				}
				itemMeta.setLore(lore);
			}
			
			itemStack.setItemMeta(itemMeta);
			
			// Also return json if requested
			if (jsonReturner != null) {
				jsonReturner.setJson(extraJson);
			}
		}

		return itemStack;
	}
	
	@NoArgsConstructor
	@Setter(AccessLevel.PROTECTED)
	@Getter
	public static class JsonReturner {
		
		private JsonObject json;
		
	}


}
