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
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ParserUtils {
	
	protected static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static <T> T castJson(JsonElement jsonElement, Class<T> type, String errorMessage) throws ParserException {
		if (jsonElement != null) {
			if (type == String.class) {
				return (T) castPrimitive(jsonElement, errorMessage).getAsString();
			} else if (type == Integer.class) {
				return (T) (Integer) castPrimitive(jsonElement, errorMessage).getAsInt();
			} else if (type.isInstance(jsonElement)) {
				return type.cast(jsonElement);
			} else {
				throw new ParserException(errorMessage);
			}
		} else {
			return null;
		}
	}
	
	private static JsonPrimitive castPrimitive(JsonElement jsonElement, String errorMessage) throws ParserException {
		if (jsonElement instanceof JsonPrimitive) {
			return (JsonPrimitive) jsonElement;
		} else {
			throw new ParserException(errorMessage);
		}
	}
	
	protected static int parseInteger(String input, int minimumValue, String errorMessage) throws ParserException {
		return parseInteger(input, minimumValue, Integer.MAX_VALUE, errorMessage);
	}
	
	protected static int parseInteger(String input, int minimumValue, int maximumValue, String errorMessage) throws ParserException {
		try {
			int value = Integer.parseInt(input);
			if (value < minimumValue || value > maximumValue) {
				throw new ParserException(errorMessage);
			}
			return value;
		} catch (NumberFormatException e) {
			throw new ParserException(errorMessage);
		}
	}
	
	protected static <E> E match(String input, E[] possibleValues, Map<String, E> customAliases, NameGetter<E> nameGetter, String errorMessage) throws ParserException {
		input = input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		if (customAliases != null) {
			for (Entry<String, E> entry : customAliases.entrySet()) {
				if (entry.getKey().toLowerCase().replace("_", "").equals(input)) {
					return entry.getValue();
				}
			}
		}
		for (E possibleValue : possibleValues) {
			if (possibleValue != null && nameGetter.getName(possibleValue).toLowerCase().replace("_", "").equals(input)) {
				return possibleValue;
			}
		}
		
		throw new ParserException(errorMessage);
	}


}
