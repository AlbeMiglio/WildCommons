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

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;

public abstract class AbstractPacket {
	// The packet we will be modifying
	protected PacketContainer handle;

	/**
	 * Constructs a new strongly typed wrapper for the given packet.
	 * 
	 * @param handle - handle to the raw packet data.
	 * @param type - the packet type.
	 */
	protected AbstractPacket(PacketContainer handle, PacketType type) {
		// Make sure we're given a valid packet
		if (handle == null)
			throw new IllegalArgumentException("Packet handle cannot be NULL.");
		if (!Objects.equal(handle.getType(), type))
			throw new IllegalArgumentException(handle.getHandle()
					+ " is not a packet of type " + type);

		this.handle = handle;
	}

	/**
	 * Retrieve a handle to the raw packet data.
	 * 
	 * @return Raw packet data.
	 */
	public PacketContainer getHandle() {
		return handle;
	}

	/**
	 * Send the current packet to the given receiver.
	 * 
	 * @param receiver - the receiver.
	 * @throws RuntimeException If the packet cannot be sent.
	 */
	public void sendPacket(Player receiver) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(receiver,
					getHandle());
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Cannot send packet.", e);
		}
	}

	/**
	 * Send the current packet to all online players.
	 */
	public void broadcastPacket() {
		ProtocolLibrary.getProtocolManager().broadcastServerPacket(getHandle());
	}

	/**
	 * Simulate receiving the current packet from the given sender.
	 * 
	 * @param sender - the sender.
	 * @throws RuntimeException If the packet cannot be received.
	 * @deprecated Misspelled. recieve to receive
	 * @see #receivePacket(Player)
	 */
	@Deprecated
	public void recievePacket(Player sender) {
		try {
			ProtocolLibrary.getProtocolManager().recieveClientPacket(sender,
					getHandle());
		} catch (Exception e) {
			throw new RuntimeException("Cannot recieve packet.", e);
		}
	}

	/**
	 * Simulate receiving the current packet from the given sender.
	 * 
	 * @param sender - the sender.
	 * @throws RuntimeException if the packet cannot be received.
	 */
	public void receivePacket(Player sender) {
		try {
			ProtocolLibrary.getProtocolManager().recieveClientPacket(sender,
					getHandle());
		} catch (Exception e) {
			throw new RuntimeException("Cannot receive packet.", e);
		}
	}
}
