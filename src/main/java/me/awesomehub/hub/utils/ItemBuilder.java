package me.awesomehub.hub.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Permet de gérer simplement les métadonnées d'un item.
 * 
 * @author Gwennael
 */
public class ItemBuilder {
    private String title;
    private int amount;
    private short damage;
    private Color leatherColor;
    private Material material;
    private List<String> lores = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    /**
     * Construit un nouveau ItemBuilder.
     * 
     * @param item Un ItemStack.
     */
    public ItemBuilder(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability());
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                leatherColor = leatherMeta.getColor();
            }
            if (meta.hasDisplayName()) {
                title = meta.getDisplayName();
            }
            if (meta.hasEnchants()) {
                enchantments.putAll(meta.getEnchants());
            }
            if (meta.hasLore()) {
                lores.addAll(meta.getLore());
            }
        }
    }

    /**
     * Construit un nouveau ItemBuilder.
     * 
     * @param material Un matériel.
     */
    public ItemBuilder(Material material) {
        this(material, 1, (short) 0);
    }

    /**
     * Construit un nouveau ItemBuilder.
     * 
     * @param material Un matériel.
     * @param amount Le nombre d'items.
     */
    public ItemBuilder(Material material, int amount) {
        this(material, amount, (short) 0);
    }

    /**
     * Construit un nouveau ItemBuilder.
     * 
     * @param material Un matériel.
     * @param amount Le nombre d'items.
     * @param damage La durabilité.
     */
    public ItemBuilder(Material material, int amount, short damage) {
        this.material = material;
        this.amount = amount;
        this.damage = damage;
    }

    /**
     * Construit un nouveau ItemBuilder.
     * 
     * @param material Un matériel.
     * @param damage La durabilité.
     */
    public ItemBuilder(Material material, short damage) {
        this(material, 1, damage);
    }

    /**
     * Ajoute un enchantement à l'item.
     * 
     * @param enchantment L'enchantement.
     * @param level Le niveau.
     * @return {@link ItemBuilder}r
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Ajoute une description à l'item.
     * 
     * @param lore La ligne.
     * @return {@link ItemBuilder}
     */
    public ItemBuilder addLore(String lore) {
        lores.add(lore);
        return this;
    }

    /**
     * Ajoute une description à l'item.
     * 
     * @param lores Les lignes.
     * @return {@link ItemBuilder}er
     */
    public ItemBuilder addLores(String... lores) {
        for (String lore : lores) {
            this.addLore(lore);
        }
        return this;
    }

    /**
     * Défini un nom à l'item.
     * 
     * @param title Le nom.
     * @return {@link ItemBuilder}
     */
    public ItemBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Défini une couleur à l'item
     * (seulement pour l'armure en cuir).
     * 
     * @param color La couleur.
     * @return {@link ItemBuilder}
     */
    public ItemBuilder setLeatherColor(Color color) {
        leatherColor = color;
        return this;
    }

    /**
     * Construit l'ItemStack.
     * 
     * NullPoinNullPointerException
     * @return {@link ItemStack}
     */
    public ItemStack build() {
        if (material == null) throw new NullPointerException("Material cannot be null!");
        ItemStack item = new ItemStack(material, amount, damage);
        if (!enchantments.isEmpty()) {
            item.addUnsafeEnchantments(enchantments);
        }
        ItemMeta meta = item.getItemMeta();
        if (title != null) {
            meta.setDisplayName(title);
        }
        if (leatherColor != null && item.getType().name().contains("LEATHER_")) {
            ((LeatherArmorMeta) meta).setColor(leatherColor);
        }
        if (!lores.isEmpty()) {
            meta.setLore(lores);
        }
        item.setItemMeta(meta);
        return item;
    }
}
