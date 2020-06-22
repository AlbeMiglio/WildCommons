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

public class WrapperPlayClientWindowClick extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Client.WINDOW_CLICK;

	public WrapperPlayClientWindowClick() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayClientWindowClick(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Window ID.
	 * <p>
	 * Notes: the id of the window which was clicked. 0 for player inventory.
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
	 * Notes: the clicked slot. See below.
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
	 * Retrieve Button.
	 * <p>
	 * Notes: the button used in the click. See below.
	 * 
	 * @return The current Button
	 */
	public int getButton() {
		return handle.getIntegers().read(2);
	}

	/**
	 * Set Button.
	 * 
	 * @param value - new value.
	 */
	public void setButton(int value) {
		handle.getIntegers().write(2, value);
	}

	/**
	 * Retrieve Action number.
	 * <p>
	 * Notes: a unique number for the action, used for transaction handling (See
	 * the Transaction packet).
	 * 
	 * @return The current Action number
	 */
	public short getActionNumber() {
		return handle.getShorts().read(0);
	}

	/**
	 * Set Action number.
	 * 
	 * @param value - new value.
	 */
	public void setActionNumber(short value) {
		handle.getShorts().write(0, value);
	}

	/**
	 * Retrieve Clicked item.
	 * 
	 * @return The current Clicked item
	 */
	public ItemStack getClickedItem() {
		return handle.getItemModifier().read(0);
	}

	/**
	 * Set Clicked item.
	 * 
	 * @param value - new value.
	 */
	public void setClickedItem(ItemStack value) {
		handle.getItemModifier().write(0, value);
	}

	public InventoryClickType getShift() {
		return handle.getEnumModifier(InventoryClickType.class, 5).read(0);
	}

	public void setShift(InventoryClickType value) {
		handle.getEnumModifier(InventoryClickType.class, 5).write(0, value);
	}

	public enum InventoryClickType {
		PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL;
	}
}
