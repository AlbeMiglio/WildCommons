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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerTransaction extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.TRANSACTION;

	public WrapperPlayServerTransaction() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerTransaction(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Window ID.
	 * <p>
	 * Notes: the id of the window that the action occurred in.
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
	 * Retrieve Action number.
	 * <p>
	 * Notes: every action that is to be accepted has a unique number. This
	 * field corresponds to that number.
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
	 * Retrieve Accepted.
	 * <p>
	 * Notes: whether the action was accepted.
	 * 
	 * @return The current Accepted
	 */
	public boolean getAccepted() {
		return handle.getBooleans().read(0);
	}

	/**
	 * Set Accepted.
	 * 
	 * @param value - new value.
	 */
	public void setAccepted(boolean value) {
		handle.getBooleans().write(0, value);
	}

}
