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

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import lombok.Getter;

public class ScoreboardSidebarManager {
	
	@Getter private final Scoreboard scoreboard;
	@Getter private final Objective objective;

	public ScoreboardSidebarManager(Scoreboard scoreboard, String title) {
		this.scoreboard = scoreboard;
		safeRemoveObjective(scoreboard.getObjective(DisplaySlot.SIDEBAR));
		safeRemoveObjective(scoreboard.getObjective("sidebarDummy"));
		
		objective = scoreboard.registerNewObjective("sidebarDummy", "dummy");
		objective.setDisplayName(title);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void setTitle(String title) {
		objective.setDisplayName(title);
	}
	
	public void setLine(int score, String text) {
		new StaticSidebarLine(scoreboard, objective, score, text);
	}
	
	public DynamicSidebarLine setDynamicLine(int score, String text) {
		return new DynamicSidebarLine(scoreboard, objective, score, text);
	}
	
	private static void safeRemoveObjective(Objective obj) {
		if (obj != null) {
			obj.unregister();
		}
	}
	
}
