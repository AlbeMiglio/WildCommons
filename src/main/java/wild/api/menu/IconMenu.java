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
package wild.api.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.NonNull;

public class IconMenu {
	
	//private String title;
	private Icon[] icons;
	private int rows;
	
	Inventory inventory;
	
	public IconMenu(String title, int rows) {
		this.rows = rows;
		icons = new Icon[rows * 9];
		inventory = Bukkit.createInventory(new MenuInventoryHolder(this), icons.length, title);
	}
	
	/*
	 *    MEMO: SLOT NUMBERS
	 * 
	 *    | 0| 1| 2| 3| 4| 5| 6| 7| 8|
	 *    | 9|10|11|12|13|14|15|16|17|
	 *    ...
	 * 
	 */
	public void setIcon(int x, int y, Icon icon) {
		int slot = (y - 1) * 9 + (x - 1);
		if (slot >= 0 && slot < icons.length) {
			icons[slot] = icon;
		}
	}
	
//	public void setIcon(int x, int y, IconBuilder builder) {
//		setIcon(x, y, builder.build());
//	}
	
	public void setIconRaw(int index, Icon icon) {
		if (index >= 0 && index < icons.length) {
			icons[index] = icon;
		}
	}
	
//	public void setIconRaw(int index, IconBuilder builder) {
//		setIconRaw(index, builder.build());
//	}
	
	public Icon getIconAt(int slot) {
		if (slot >= 0 && slot < icons.length)  {
			return icons[slot];
		}

		return null;
	}
	
	public void clearIcons() {
		icons = new Icon[rows * 9];
	}
	
	public void refresh() {
		inventory.clear();
		for (int i = 0; i < icons.length; i++) {
			if (icons[i] != null) {
				inventory.setItem(i, icons[i].createItemstack());
			}
		}
	}
	
	public void refresh(@NonNull Icon icon) {
		for (int i = 0; i < icons.length; i++) {
			if (icons[i] == icon) {
				inventory.setItem(i, icons[i].createItemstack());
			}
		}
	}
	
	public void refresh(int slot) {
		inventory.clear(slot);
		if (icons[slot] != null) {
			inventory.setItem(slot, icons[slot].createItemstack());
		}
	}
	
	public int getRows() {
		return icons.length / 9;
	}
	
	public int getSize() {
		return icons.length;
	}
	
	public void open(Player player) {
		player.openInventory(inventory);
	}

	public void onClose(Player player) {
		// Override
	}
	
}
