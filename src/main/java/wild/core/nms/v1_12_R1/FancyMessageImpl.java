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
package wild.core.nms.v1_12_R1;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

import com.google.common.base.Joiner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.compact.JSONException;
import org.json.compact.JSONStringer;

import wild.core.nms.interfaces.FancyMessage;

public class FancyMessageImpl implements FancyMessage {
	
	Joiner joiner = Joiner.on('\n');
	private final List<MessagePart> messageParts;
	
	public FancyMessageImpl(final String firstPartText) {
		messageParts = new ArrayList<MessagePart>();
		messageParts.add(new MessagePart(firstPartText));
	}
	
	@Override
	public FancyMessageImpl color(final ChatColor color) {
		if (!color.isColor()) {
			throw new IllegalArgumentException(color.name() + " is not a color");
		}
		latest().color = color;
		return this;
	}
	
	@Override
	public FancyMessageImpl style(final ChatColor... styles) {
		for (final ChatColor style : styles) {
			if (!style.isFormat()) {
				throw new IllegalArgumentException(style.name() + " is not a style");
			}
		}
		latest().styles = styles;
		return this;
	}

	@Override
	public FancyMessageImpl link(final String url) {
		onClick("open_url", url);
		return this;
	}
	
	@Override
	public FancyMessageImpl suggest(final String command) {
		onClick("suggest_command", command);
		return this;
	}
	
	@Override
	public FancyMessageImpl command(final String command) {
		onClick("run_command", command);
		return this;
	}
	
	@Override
	public FancyMessageImpl tooltip(String... text) {
		if (text.length == 1) {
			onHover("show_text", text[0]);
		} else if (text.length > 1) {
			onHover("show_text", joiner.join(text));
		}

		return this;
	}

	@Override
	public FancyMessageImpl then(final String text) {
		messageParts.add(new MessagePart(text));
		return this;
	}
	
	public String toJSONString() {
		final JSONStringer json = new JSONStringer();
		try {
			if (messageParts.size() == 1) {
				latest().writeJson(json);
			} else {
				json.array();
				for (final MessagePart part : messageParts) {
					part.writeJson(json);
				}
				json.endArray();
			}
		} catch (final JSONException e) {
			throw new RuntimeException("Invalid JSON message", e);
		}
		return json.toString();
	}
	
	public String toLegacyString() {
		StringBuilder builder = new StringBuilder();
		for (final MessagePart part : messageParts) {
			if (part.color != null) {
				builder.append(part.color);
			}
			if (part.styles != null) {
				for (ChatColor style : part.styles) {
					builder.append(style);
				}
			}
			
			builder.append(part.text);
		}
		
		return builder.toString();
	}
	
	@Override
	public void send(Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(toJSONString())));
	}
	
	@Override
	public void send(CommandSender receiver) {
		if (receiver instanceof Player) {
			((CraftPlayer) receiver).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(toJSONString())));
		} else {
			receiver.sendMessage(toLegacyString());
		}
	}
	
	private MessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}
	
	private void onClick(final String name, final String data) {
		final MessagePart latest = latest();
		latest.clickActionName = name;
		latest.clickActionData = data;
	}
	
	private void onHover(final String name, final String data) {
		final MessagePart latest = latest();
		latest.hoverActionName = name;
		latest.hoverActionData = data;
	}
	
}
