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
package wild.api.bridges;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wild.core.WildCommonsPlugin;

public class EconomyBridge {
	
	private static boolean enabled;

	public static void setup() {
		if (enabled) {
			return;
		}
		enabled = Bukkit.getPluginManager().isPluginEnabled("Essentials");
	}
	
	public static boolean hasValidEconomy() {
		return enabled;
	}
	
	public static double getMoney(Player player) {
		return getMoneyExact(player).doubleValue();
	}
	
	public static double getMoney(UUID playerUUID) throws PlayerNotFoundException {
		return getMoneyExact(playerUUID).doubleValue();
	}
	
	public static BigDecimal getMoneyExact(Player player) {
		try {
			return getMoneyExact(player.getUniqueId());
		} catch (PlayerNotFoundException e) {
			WildCommonsPlugin.instance.getLogger().log(Level.SEVERE, "Couldn't find money of online player " + player.getName() + "/" + player.getUniqueId(), e);
			return BigDecimal.ZERO;
		}
	}
	
	public static BigDecimal getMoneyExact(UUID playerUUID) throws PlayerNotFoundException {
		checkState();
		try {
			return Economy.getMoneyExact(playerUUID);
		} catch (UserDoesNotExistException e) {
			throw new PlayerNotFoundException(playerUUID.toString());
		}
	}
	
	public static boolean hasMoney(Player player, long amount) {
		return hasMoney(player, BigDecimal.valueOf(amount));
	}
	
	public static boolean hasMoney(Player player, double amount) {
		return hasMoney(player, BigDecimal.valueOf(amount));
	}
	
	public static boolean hasMoney(UUID playerUUID, long amount) throws PlayerNotFoundException {
		return hasMoney(playerUUID, BigDecimal.valueOf(amount));
	}
	
	public static boolean hasMoney(UUID playerUUID, double amount) throws PlayerNotFoundException {
		return hasMoney(playerUUID, BigDecimal.valueOf(amount));
	}
	
	public static boolean hasMoney(Player player, BigDecimal amount) {
		try {
			return hasMoney(player.getUniqueId(), amount);
		} catch (PlayerNotFoundException e) {
			WildCommonsPlugin.instance.getLogger().log(Level.SEVERE, "Couldn't check money of online player " + player.getName() + "/" + player.getUniqueId(), e);
			return false;
		}
	}
	
	public static boolean hasMoney(UUID playerUUID, BigDecimal amount) throws PlayerNotFoundException {
		checkState();
		checkNotNegative(amount);
		
		return getMoneyExact(playerUUID).compareTo(amount) >= 0;
	}
	
	public static boolean takeMoney(Player player, long amount) {
		return takeMoney(player, BigDecimal.valueOf(amount));
	}
	
	public static boolean takeMoney(Player player, double amount) {
		return takeMoney(player, BigDecimal.valueOf(amount));
	}
	
	public static boolean takeMoney(UUID playerUUID, long amount) throws PlayerNotFoundException {
		return takeMoney(playerUUID, BigDecimal.valueOf(amount));
	}
	
	public static boolean takeMoney(UUID playerUUID, double amount) throws PlayerNotFoundException {
		return takeMoney(playerUUID, BigDecimal.valueOf(amount));
	}
	
	public static boolean takeMoney(Player player, BigDecimal amount) {
		try {
			return takeMoney(player.getUniqueId(), amount);
		} catch (PlayerNotFoundException e) {
			WildCommonsPlugin.instance.getLogger().log(Level.SEVERE, "Couldn't take money of online player " + player.getName() + "/" + player.getUniqueId(), e);
			return false;
		}
	}

	public static boolean takeMoney(UUID playerUUID, BigDecimal amount) throws PlayerNotFoundException {
		checkState();
		checkNotNegative(amount);
		
		try {
			Economy.substract(String.valueOf(playerUUID), amount);
			return true;
		} catch (UserDoesNotExistException e) {
			throw new PlayerNotFoundException(playerUUID.toString());
		} catch (NoLoanPermittedException e) {
			return false;
		}
	}
	
	public static void giveMoney(Player player, long amount) {
		giveMoney(player, BigDecimal.valueOf(amount));
	}
	
	public static void giveMoney(Player player, double amount) {
		giveMoney(player, BigDecimal.valueOf(amount));
	}
	
	public static void giveMoney(UUID playerUUID, long amount) throws PlayerNotFoundException {
		giveMoney(playerUUID, BigDecimal.valueOf(amount));
	}
	
	public static void giveMoney(UUID playerUUID, double amount) throws PlayerNotFoundException {
		giveMoney(playerUUID, BigDecimal.valueOf(amount));
	}
	
	public static void giveMoney(Player player, BigDecimal amount) {
		try {
			giveMoney(player.getUniqueId(), amount);
		} catch (PlayerNotFoundException e) {
			WildCommonsPlugin.instance.getLogger().log(Level.SEVERE, "Couldn't give money to online player " + player.getName() + "/" + player.getUniqueId(), e);
		}
	}
	
	public static void giveMoney(UUID playerUUID, BigDecimal amount) throws PlayerNotFoundException {
		checkState();
		checkNotNegative(amount);
		
		try {
			Economy.add(playerUUID, amount);
		} catch (UserDoesNotExistException e) {
			throw new PlayerNotFoundException(playerUUID.toString());
		} catch (NoLoanPermittedException e) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}
	
	private static void checkState() {
		if (!hasValidEconomy()) {
			throw new IllegalStateException("Economy plugin was not found!");
		}
	}
	
	private static void checkNotNegative(BigDecimal amount) {
		if (amount.signum() < 0) {
			throw new IllegalArgumentException("Invalid amount of money: " + amount);
		}
	}
	
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlayerNotFoundException extends Exception {

		private static final long serialVersionUID = 1L;
		
		@Getter private final String name;
		
	}

}
