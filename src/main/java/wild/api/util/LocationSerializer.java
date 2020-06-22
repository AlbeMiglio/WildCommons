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
package wild.api.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.NonNull;

public class LocationSerializer {
	
	private static DecimalFormat decimalFormat;
	static {
		// More precision is not needed at all.
		decimalFormat = new DecimalFormat("0.000");
		DecimalFormatSymbols formatSymbols = decimalFormat.getDecimalFormatSymbols();
		formatSymbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(formatSymbols);
	}

	
	public static class WorldNotFoundException extends Exception {

		private static final long serialVersionUID = -6768657756719902603L;

		public WorldNotFoundException(String message) {
			super(message);
		}
		
	}
		
	public static Location fromString(@NonNull String input) throws ParseException, WorldNotFoundException {
		
		String[] parts = input.split(",");
		
		if (parts.length != 4 && parts.length != 6) {
			throw new ParseException("location parts are not 4 or 6", 0);
		}
		
		try {
			double x = Double.parseDouble(parts[1].trim());
			double y = Double.parseDouble(parts[2].trim());
			double z = Double.parseDouble(parts[3].trim());
			
			double yaw = 0;
			double pitch = 0;
			
			if (parts.length == 6) {
				yaw = Double.parseDouble(parts[4].trim());
				pitch = Double.parseDouble(parts[5].trim());
			}
		
			String worldName = parts[0].trim();
			World world = Bukkit.getWorld(worldName);
			if (world == null) {
				throw new WorldNotFoundException(worldName);
			}
			
			return new Location(world, x, y, z, (float) yaw, (float) pitch);
			
		} catch (NumberFormatException ex) {
			throw new ParseException("invalid number format", 0);
		}
	}
	
	public static String toString(Location loc) {
		return (
			loc.getWorld().getName() + ", " +
			decimalFormat.format(loc.getX()) + ", " +
			decimalFormat.format(loc.getY()) + ", " +
			decimalFormat.format(loc.getZ()) + ", " +
			decimalFormat.format(loc.getYaw()) + ", " +
			decimalFormat.format(loc.getPitch())
		);
	}
}
