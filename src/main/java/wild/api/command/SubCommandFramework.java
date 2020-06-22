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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class SubCommandFramework extends CommandFramework {
	
	private Map<String, SubCommandPiece> subCommands;
	
	public SubCommandFramework(JavaPlugin plugin, String label) {
		this(plugin, label, new String[0]);
	}

	public SubCommandFramework(JavaPlugin plugin, String label, String... aliases) {
		super(plugin, label, aliases);
		subCommands = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		
		for (Method method : getClass().getDeclaredMethods()) {
			if (method.getAnnotation(SubCommand.class) != null) {

				String subCommandName = method.getAnnotation(SubCommand.class).value();
				String subCommandPermission = method.getAnnotation(SubCommandPermission.class) != null ? method.getAnnotation(SubCommandPermission.class).value() : null;
				String subCommandNoPermissionMessage = method.getAnnotation(SubCommandNoPermissionMessage.class) != null ? method.getAnnotation(SubCommandNoPermissionMessage.class).value() : null;
				String subCommandUsage = method.getAnnotation(SubCommandUsage.class) != null ? method.getAnnotation(SubCommandUsage.class).value() : null;
				int minArgs = method.getAnnotation(SubCommandMinArgs.class) != null ? method.getAnnotation(SubCommandMinArgs.class).value() : 0;
				
				try {
					method.setAccessible(true);
					
					Class<?>[] params = method.getParameterTypes();
					if (params == null || params.length != 3 || params[0] != CommandSender.class || params[1] != String.class || params[2] != String[].class) {
						throw new IllegalArgumentException("I parametri del sottocomando devono essere 3, in ordine: CommandSender (sender), String (label), String[] (args)");
					}
					
					SubCommandPiece piece = new SubCommandPiece(subCommandName, method, subCommandPermission, subCommandNoPermissionMessage, subCommandUsage, minArgs);
					subCommands.put(subCommandName.toLowerCase(), piece);
					
				} catch (Exception e) {
					plugin.getLogger().log(Level.SEVERE, "Impossibile aggiungere il sottocomando " + subCommandName, e);
				}
			}
		}
	}
	
	@Override
	public final void execute(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			noArgs(sender);
			return;
		}
		
		String subCommandName = args[0].toLowerCase();
		SubCommandPiece piece = subCommands.get(subCommandName);
		
		if (piece != null) {
			try {
				if (piece.permission != null) {
					if (!sender.hasPermission(piece.permission)) {
						if (piece.noPermissionMessage != null) {
							sender.sendMessage(piece.noPermissionMessage);
						} else {
							sender.sendMessage(ChatColor.RED + "Non hai il permesso per usare questo sotto-comando.");
						}
						return;
					}
				}
				
				String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
				
				if (subArgs.length < piece.minArgs) {
					sender.sendMessage(ChatColor.RED + "Utilizzo comando: " + "/" + label + " " + subCommandName + (piece.usage != null ? " " + piece.usage : ""));
					return;
				}
				
				piece.method.invoke(this, sender, label, Arrays.copyOfRange(args, 1, args.length));
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof ExecuteException) {
					throw (ExecuteException) e.getTargetException();
				} else {
					throw new RuntimeException(e.getTargetException());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			tellUnknownSubCommand(sender, label);
		}
	}
	
	public abstract void noArgs(CommandSender sender);
	
	public void tellUnknownSubCommand(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.RED + "Sotto-comando sconosciuto. Scrivi /" + label + " per ottenere aiuto.");
	}
	
	protected final List<SubCommandDetails> getSubCommands() {
		List<SubCommandDetails> list = Lists.newArrayList();
		for (SubCommandPiece subCommand : subCommands.values()) {
			list.add(new SubCommandDetails(subCommand.name, subCommand.permission, subCommand.usage));
		}
		return list;
	}
	
	protected final List<SubCommandDetails> getAccessibleSubCommands(Permissible permissible) {
		List<SubCommandDetails> list = Lists.newArrayList();
		for (SubCommandPiece subCommand : subCommands.values()) {
			if (subCommand.permission == null || permissible.hasPermission(subCommand.permission)) {
				list.add(new SubCommandDetails(subCommand.name, subCommand.permission, subCommand.usage));
			}
		}
		return list;
	}
	
	//public void showHelp() {
		
	//}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface SubCommand {

		String value();
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface SubCommandPermission {

		String value();
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface SubCommandNoPermissionMessage {

		String value();

	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface SubCommandUsage {

		String value();

	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface SubCommandMinArgs {

		int value();

	}
	
	@AllArgsConstructor
	@Getter
	public static class SubCommandDetails {
		
		private String name;
		private String permission;
		private String usage;

	}
	
	@AllArgsConstructor
	private static class SubCommandPiece {
		
		private String name;
		private Method method;
		private String permission;
		private String noPermissionMessage;
		private String usage;
		private int minArgs;
		
	}
	
	
}
