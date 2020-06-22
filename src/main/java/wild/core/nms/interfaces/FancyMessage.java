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
package wild.core.nms.interfaces;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.compact.JSONException;
import org.json.compact.JSONWriter;

public interface FancyMessage {

	public FancyMessage color(final ChatColor color);

	public FancyMessage style(final ChatColor... styles);

	public FancyMessage link(final String url);
	
	public FancyMessage suggest(final String command);
	
	public FancyMessage command(final String command);
	
	public FancyMessage tooltip(String... text);

	public FancyMessage then(final String text);
	
	public void send(Player player);
	
	public void send(CommandSender receiver);
	
	public static class MessagePart {

		public ChatColor color = null;
		public ChatColor[] styles = null;
		public String clickActionName = null;
		public String clickActionData = null;
		public String hoverActionName = null;
		public String hoverActionData = null;
		public final String text;
		
		public MessagePart(final String text) {
			this.text = text;
		}
		
		public JSONWriter writeJson(final JSONWriter json) throws JSONException {
			json.object().key("text").value(text);
			if (color != null) {
				json.key("color").value(color.name().toLowerCase());
			}
			if (styles != null) {
				for (final ChatColor style : styles) {
					json.key(style == ChatColor.UNDERLINE ? "underlined" : style.name().toLowerCase()).value(true);
				}
			}
			if (clickActionName != null && clickActionData != null) {
				json.key("clickEvent")
					.object()
						.key("action").value(clickActionName)
						.key("value").value(clickActionData)
					.endObject();
			}
			if (hoverActionName != null && hoverActionData != null) {
				json.key("hoverEvent")
					.object()
						.key("action").value(hoverActionName)
						.key("value").value(hoverActionData)
					.endObject();
			}
			return json.endObject();
		}
		
	}
	
}
