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

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FakeOfflinePlayer implements OfflinePlayer {

	private String name;

	public FakeOfflinePlayer(String name) {
		if (name.length() > 16) {
			name = name.substring(0, 16);
		}
		
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public void setOp(boolean arg0) {

	}

	@Override
	public Map<String, Object> serialize() {
		return null;
	}

	@Override
	public Location getBedSpawnLocation() {
		return null;
	}

	@Override
	public long getFirstPlayed() {
		return 0;
	}

	@Override
	public long getLastPlayed() {
		return 0;
	}

	@Override
	public Player getPlayer() {
		return null;
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isBanned() {
		return false;
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public boolean isWhitelisted() {
		return false;
	}

	@Override
	public void setWhitelisted(boolean arg0) {
		
	}

	@Override
	public UUID getUniqueId() {
		return null;
	}

	public void setBanned(boolean arg0) {
		
	}

}
