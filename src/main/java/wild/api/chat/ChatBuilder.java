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
package wild.api.chat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;


public class ChatBuilder {
	
	private ComponentBuilder componentBuilder;

	public ChatBuilder(String text) {
		componentBuilder = new ComponentBuilder(text);
	}
	
	public ChatBuilder runCommand(String command) {
		componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return this;
	}
	
	public ChatBuilder suggestCommand(String command) {
		componentBuilder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
		return this;
	}
	
	public ChatBuilder openUrl(String url) {
		componentBuilder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		return this;
	}
	
	public ChatBuilder tooltip(ChatColor color, String tooltip) {
		TextComponent tooltipComponent = new TextComponent(tooltip.replace("\n", "\n" + color));
		tooltipComponent.setColor(color);
		componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {tooltipComponent}));
		return this;
	}

	public void send(CommandSender sender) {
		if (sender instanceof Player) {
			((Player) sender).spigot().sendMessage(componentBuilder.create());
		} else {
			sender.sendMessage(toLegacyString());
		}
	}
	
	private String toLegacyString() {
		StringBuilder builder = new StringBuilder();
		
		for (BaseComponent part : componentBuilder.create()) {
			builder.append(part.getColor());
			
			if (part.isBold()) 			builder.append(ChatColor.BOLD);
			if (part.isUnderlined()) 	builder.append(ChatColor.UNDERLINE);
			if (part.isItalic()) 		builder.append(ChatColor.ITALIC);
			if (part.isStrikethrough()) builder.append(ChatColor.STRIKETHROUGH);
			if (part.isObfuscated()) 	builder.append(ChatColor.MAGIC);

			if (part instanceof TextComponent) {
				builder.append(((TextComponent) part).getText());
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Delegate methods below
	 */

	public ChatBuilder append(String text, FormatRetention retention) {
		componentBuilder.append(text, retention);
		return this;
	}

	public ChatBuilder append(String text) {
		componentBuilder.append(text);
		return this;
	}

	public ChatBuilder bold(boolean bold) {
		componentBuilder.bold(bold);
		return this;
	}

	public ChatBuilder color(ChatColor color) {
		componentBuilder.color(color);
		return this;
	}

	public ChatBuilder event(ClickEvent clickEvent) {
		componentBuilder.event(clickEvent);
		return this;
	}

	public ChatBuilder event(HoverEvent hoverEvent) {
		componentBuilder.event(hoverEvent);
		return this;
	}

	public ChatBuilder italic(boolean italic) {
		componentBuilder.italic(italic);
		return this;
	}

	public ChatBuilder obfuscated(boolean obfuscated) {
		componentBuilder.obfuscated(obfuscated);
		return this;
	}

	public ChatBuilder reset() {
		componentBuilder.reset();
		return this;
	}

	public ChatBuilder retain(FormatRetention retention) {
		componentBuilder.retain(retention);
		return this;
	}

	public ChatBuilder strikethrough(boolean strikethrough) {
		componentBuilder.strikethrough(strikethrough);
		return this;
	}

	public ChatBuilder underlined(boolean underlined) {
		componentBuilder.underlined(underlined);
		return this;
	}

	public ChatBuilder applyStylesFromString(String string) {
		for (ChatColor color : ChatColor.values()) {
			if (string.contains(color.toString())) {
				switch (color) {
					case BOLD:
						bold(true);
						break;
					case ITALIC:
						italic(true);
						break;
					case UNDERLINE:
						underlined(true);
						break;
					case STRIKETHROUGH:
						strikethrough(true);
						break;
					case MAGIC:
						obfuscated(true);
						break;
					default:
						color(color);
						break;
				}
			}
		}
		return this;
	}
	
	

}
