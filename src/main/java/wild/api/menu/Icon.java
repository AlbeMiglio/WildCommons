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

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import wild.api.WildCommons;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Icon {

	private Material material;
	private int amount;
	private short dataValue;
	
	@Setter @Getter private String name;
	private String[] lore;
	
	@Setter @Getter private String skullOwner;
	
	private Map<Enchantment, Integer> enchants;
	
	private boolean hideAttributes;
	private boolean closeOnClick;
	private ClickHandler clickHandler;
	@Setter private MetaModifier metaModifier;
	
	public Icon() {
		hideAttributes = true;
		amount = 1;
		enchants = Maps.newHashMap();
	}
	
	public Icon(Material material) {
		this();
		this.material = material;
	}
	
	public void setMaterial(Material material) {
		if (material == Material.AIR) material = null;
		this.material = material;
	}
	
	public void setAmount(int amount) {
		if 		(amount < 1)	amount = 1;
		else if (amount > 127) 	amount = 127;
		
		this.amount = amount;
	}
	
	public void setDataValue(short dataValue) {
		if (dataValue < 0) dataValue = 0;
		
		this.dataValue = dataValue;
	}
	
	public boolean hasName() {
		return name != null;
	}
	
	public void setLore(String... lore) {
		this.lore = lore;
	}
	
	public void setLore(List<String> lore) {
		if (lore != null) {
			this.lore = new String[lore.size()];
			lore.toArray(this.lore);
		} else {
			this.lore = null;
		}
	}
	
	public boolean hasLore() {
		return lore != null && lore.length > 0;
	}
	
	public void addEnchantment(Enchantment ench) {
		addEnchantment(ench, 1);
	}
	
	public void addEnchantment(Enchantment ench, int level) {
		enchants.put(ench, level);
	}
	
	public boolean isHideAttributes() {
		return hideAttributes;
	}

	public void setHideAttributes(boolean hideAttributes) {
		this.hideAttributes = hideAttributes;
	}
	
	public void setCloseOnClick(boolean closeOnClick) {
		this.closeOnClick = closeOnClick;
	}
	
	public boolean isCloseOnClick() {
		return closeOnClick;
	}
	
	public void setClickHandler(ClickHandler clickHandler) {
		this.clickHandler = clickHandler;
	}
	
	public ClickHandler getClickHandler() {
		return clickHandler;
	}
	
	protected String calculateName() {
		if (hasName()) {
			
			String name = this.name;
			
			if (name.indexOf(ChatColor.COLOR_CHAR) != 0) {
				name = ChatColor.WHITE + name;
			}
			return name;
		}
		
		return null;
	}

	protected List<String> calculateLore() {

		List<String> output = null;
		
		if (hasLore()) {
			
			output = Lists.newArrayList();

			for (String line : lore) {
				if (line.indexOf(ChatColor.COLOR_CHAR) != 0) {
					line = ChatColor.WHITE + line;
				}
				output.add(line);
			}
		}
		
		if (material == null) {

			if (output == null) output = Lists.newArrayList();
			
			// Add an error message.
			output.add(ChatColor.RED + "(Invalid material)");
		}
		
		return output;
	}
	
	public ItemStack createItemstack() {
		
		// If the material is not set, display Bedrock.
		ItemStack itemStack = (material != null) ? new ItemStack(material, amount, dataValue) : new ItemStack(Material.BEDROCK, amount);
		
		// Apply lore and name.
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(calculateName());
		itemMeta.setLore(calculateLore());
		
		if (skullOwner != null && itemMeta instanceof SkullMeta) {
			((SkullMeta) itemMeta).setOwner(skullOwner);
		}
		
		if (metaModifier != null) {
			metaModifier.apply(itemMeta);
		}
		
		itemStack.setItemMeta(itemMeta);
		
		if (!enchants.isEmpty()) {
			itemStack.addUnsafeEnchantments(enchants);
		}
		
		if (hideAttributes) {
			WildCommons.removeAttributes(itemStack);
		}
		
		return itemStack;
	}

	public void onClick(Player whoClicked) {
		if (clickHandler != null) {
			clickHandler.onClick(whoClicked);
		}
		
	}
}
