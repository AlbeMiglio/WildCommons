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

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public abstract class SidebarLine {
	
	private static String[] EMPTY_ENTRIES = {
		"\u20c0",
		"\u20c1",
		"\u20c2",
		"\u20c3",
		"\u20c4",
		"\u20c5",
		"\u20c6",
		"\u20c7",
		"\u20c8",
		"\u20c9",
		"\u20ca",
		"\u20cb",
		"\u20cc",
		"\u20cd",
		"\u20ce",
		"\u20cf",
	};
	
	// ML = max length
	protected static int
		PREFIX_ML = 15,
		NAME_ML = 15,
		SUFFIX_ML = 16;
	
	
	protected Scoreboard scoreboard;
	protected Team team;
	protected String prefix, name, suffix;
	
	protected SidebarLine(Scoreboard scoreboard, Objective objective, int score, String text) {
		this.scoreboard = scoreboard;
		setup(text);
		
		String entry = getEmptyLineEntry(score) + (name != null ? name : "");
		objective.getScore(entry).setScore(score);
		
		if (prefix != null || suffix != null) {
			// Non sempre servono
			String teamName = "SidebarTeam" + score;
			team = scoreboard.getTeam(teamName);
			if (team == null) {
				team = scoreboard.registerNewTeam(teamName);
			}
			team.setPrefix(prefix != null ? prefix : "");
			team.setSuffix(suffix != null ? suffix : "");
			team.addEntry(entry);
		}
	}
	
	protected abstract void setup(String text);
	
	private static String getEmptyLineEntry(int score) {
		if (score < 0 || score >= EMPTY_ENTRIES.length) {
			throw new IllegalArgumentException("Score must be between 0-" + (EMPTY_ENTRIES.length - 1));
		}
		
		return EMPTY_ENTRIES[score];
	}
}
