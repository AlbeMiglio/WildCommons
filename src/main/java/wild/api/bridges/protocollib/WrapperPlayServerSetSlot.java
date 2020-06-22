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
package wild.api.bridges.protocollib;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerSetSlot extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.SET_SLOT;

	public WrapperPlayServerSetSlot() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerSetSlot(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Window ID.
	 * <p>
	 * Notes: the window which is being updated. 0 for player inventory. Note
	 * that all known window types include the player inventory. This packet
	 * will only be sent for the currently opened window while the player is
	 * performing actions, even if it affects the player inventory. After the
	 * window is closed, a number of these packets are sent to update the
	 * player's inventory window (0).
	 * 
	 * @return The current Window ID
	 */
	public int getWindowId() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Window ID.
	 * 
	 * @param value - new value.
	 */
	public void setWindowId(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve Slot.
	 * <p>
	 * Notes: the slot that should be updated
	 * 
	 * @return The current Slot
	 */
	public int getSlot() {
		return handle.getIntegers().read(1);
	}

	/**
	 * Set Slot.
	 * 
	 * @param value - new value.
	 */
	public void setSlot(int value) {
		handle.getIntegers().write(1, value);
	}

	/**
	 * Retrieve Slot data.
	 * 
	 * @return The current Slot data
	 */
	public ItemStack getSlotData() {
		return handle.getItemModifier().read(0);
	}

	/**
	 * Set Slot data.
	 * 
	 * @param value - new value.
	 */
	public void setSlotData(ItemStack value) {
		handle.getItemModifier().write(0, value);
	}

}
