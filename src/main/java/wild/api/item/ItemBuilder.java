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
package wild.api.item;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import lombok.NonNull;

public class ItemBuilder {

	private ItemStack item;
	
	public static ItemBuilder of(@NonNull Material material) {
		return new ItemBuilder(material);
	}
	
	private ItemBuilder(Material material) {
		item = new ItemStack(material);
	}
	
	public ItemBuilder amount(int amount) {
		item.setAmount(amount);
		return this;
	}
	
	public ItemBuilder durability(int data) {
		item.setDurability((short) data);
		return this;
	}
	
	public ItemBuilder setData(MaterialData materialData) {
		item.setData(materialData);
		return this;
	}
	
	public ItemBuilder enchant(Enchantment ench) {
		return enchant(ench, 1);
	}
	
	public ItemBuilder enchant(@NonNull Enchantment ench, int level) {
		item.addUnsafeEnchantment(ench, level);
		return this;
	}
	
	public ItemBuilder name(String name) {
		ItemMeta meta = item.getItemMeta();
		if (name != null && !name.isEmpty() && name.charAt(0) != ChatColor.COLOR_CHAR) {
			// Non comincia con un colore, aggiungiamo il bianco.
			name = ChatColor.WHITE + name;
		}
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return this;
	}
	
	public ItemBuilder lore(String... lore) {
		return lore(Arrays.asList(lore));
	}
	
	public ItemBuilder lore(List<String> lore) {
		for (int i = 0; i < lore.size(); i++) {
			if (!lore.get(i).isEmpty() && lore.get(i).charAt(0) != ChatColor.COLOR_CHAR) {
				// Non comincia con un colore, aggiungiamo il grigio.
				lore.set(i, ChatColor.GRAY + lore.get(i));
			}
		}
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return this;
	}
	
	public ItemBuilder color(Color armorColor) {
		ItemMeta meta = item.getItemMeta();
		if (meta instanceof LeatherArmorMeta) {
			((LeatherArmorMeta) meta).setColor(armorColor);
			item.setItemMeta(meta);
		}
		return this;
	}
	
	public ItemBuilder unbreakable(boolean unbreakable) {
		ItemMeta meta = item.getItemMeta();
		meta.spigot().setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return this;
	}
	
	public ItemStack build() {
		return item;
	}
	
}
