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
package wild.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import wild.core.utils.ReflectionUtils;

/**
 * Wrapper for the default command executor.
 */
public abstract class CommandFramework implements CommandExecutor {
	
	protected JavaPlugin plugin;
	protected String label;
	
	public CommandFramework(JavaPlugin plugin, String label) {
		this(plugin, label, new String[0]);
	}
	
	public CommandFramework(JavaPlugin plugin, String label, String... aliases) {
		this.plugin = plugin;
		this.label = label;
		
		PluginCommand pluginCommand = plugin.getCommand(label);
		
		if (pluginCommand == null) {
			try {
				CommandMap commandMap = (CommandMap) ReflectionUtils.getPrivateField(Bukkit.getPluginManager(), "commandMap");
				Constructor<PluginCommand> commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
				commandConstructor.setAccessible(true);
				pluginCommand = commandConstructor.newInstance(label, plugin);
				if (aliases != null && aliases.length > 0) {
					for (String alias : aliases) {
						if (alias == null || alias.isEmpty()) {
							throw new RuntimeException("Empty or null alias");
						}
						if (alias.contains(":")) {
							throw new RuntimeException("Illegal characters in alias");
						}
					}

					pluginCommand.setAliases(Arrays.asList(aliases));
				}
				
				if (!commandMap.register(plugin.getName(), pluginCommand)) {
					throw new RuntimeException("Could not overwrite existing command");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Impossibile registrare al volo il comando \"" + label + "\"");
				return;
			}
		}
		
		pluginCommand.setExecutor(this);
		
		Permission permission = getClass().getAnnotation(Permission.class);
		if (permission != null) {
			pluginCommand.setPermission(permission.value());
		}
		
		NoPermissionMessage noPermMsg = getClass().getAnnotation(NoPermissionMessage.class);
		if (noPermMsg != null) {
			pluginCommand.setPermissionMessage(noPermMsg.value());
		} else {
			pluginCommand.setPermissionMessage(ChatColor.RED + "Non hai il permesso per usare questo comando.");
		}
	}
	
	@Override
	public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			execute(sender, label, args);
			
		} catch (ExecuteException ex) {

			if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
				// Usa il rosso di default
				sender.sendMessage(ChatColor.RED + ex.getMessage());
			}
		}
		return true;
	}
	
	public abstract void execute(CommandSender sender, String label, String[] args);

		
	public static class ExecuteException extends RuntimeException {

		private static final long serialVersionUID = 7052164163215272979L;
		
		public ExecuteException(String msg) {
			super(msg);
		}
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface Permission {

		String value();
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface NoPermissionMessage {

		String value();

	}

	
	public static class CommandValidate {

		public static void notNull(Object o, String msg) {
			if (o == null) {
				throw new ExecuteException(msg);
			}
		}
		
		public static void isTrue(boolean b, String msg) {
			if (!b) {
				throw new ExecuteException(msg);
			}
		}
		
		public static Player getPlayerSender(CommandSender sender) {
			if (sender instanceof Player) {
				return (Player) sender;
			} else {
				throw new ExecuteException("Non puoi farlo dalla console.");
			}
		}
		
		public static double getDouble(String input) {
			try {
				return Double.parseDouble(input);
			} catch (NumberFormatException e) {
				throw new ExecuteException("Numero non valido.");
			}
		}
		
		public static int getInteger(String input) {
			try {
				int i = Integer.parseInt(input);
				return i;
			} catch (NumberFormatException e) {
				throw new ExecuteException("Numero non valido.");
			}
		}
		
		public static int getPositiveInteger(String input) {
			try {
				int i = Integer.parseInt(input);
				if (i < 0) {
					throw new ExecuteException("Devi inserire un numero positivo.");
				}
				return i;
			} catch (NumberFormatException e) {
				throw new ExecuteException("Numero non valido.");
			}
		}
		
		public static int getPositiveIntegerNotZero(String input) {
			try {
				int i = Integer.parseInt(input);
				if (i <= 0) {
					throw new ExecuteException("Devi inserire un numero positivo.");
				}
				return i;
			} catch (NumberFormatException e) {
				throw new ExecuteException("Numero non valido.");
			}
		}
		
		public static void minLength(Object[] array, int minLength, String msg) {
			if (array.length < minLength) {
				throw new ExecuteException(msg);
			}
		}
	}
}
