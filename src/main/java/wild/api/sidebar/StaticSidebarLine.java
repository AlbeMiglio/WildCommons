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
package wild.api.sidebar;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class StaticSidebarLine extends SidebarLine {
	
	protected StaticSidebarLine(Scoreboard scoreboard, Objective objective, int score, String text) {
		super(scoreboard, objective, score, text);
	}

	protected void setup(String text) {
		if (text.length() <= 15) {
			// Non serve creare team, sta tutto nel nome
			this.name = text;
			
		} else {
			if (text.length() > PREFIX_ML + NAME_ML + SUFFIX_ML) {
				text = text.substring(0, PREFIX_ML + NAME_ML + SUFFIX_ML); // Non ci sta, viene tagliato
			}
			
			if (text.charAt(PREFIX_ML) == ChatColor.COLOR_CHAR) {
				prefix = text.substring(0, PREFIX_ML);
			} else {
				prefix = text.substring(0, PREFIX_ML + 1); // Guadagna un carattere
			}
			
			if (text.length() > prefix.length() + NAME_ML) {
				// Serve anche il suffisso
				name = text.substring(prefix.length(), prefix.length() + NAME_ML);
				suffix = text.substring(prefix.length() + NAME_ML, text.length());
			} else {
				name = text.substring(prefix.length(), text.length());
			}
		}
	}

}
