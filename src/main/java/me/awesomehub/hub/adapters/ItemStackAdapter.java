package me.awesomehub.hub.adapters;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.utils.ItemBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Permet de sérializer et désérializer un ItemStack en JSON.
 * 
 * @author Gwennael
 */
public class ItemStackAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String DURABILITY = "durability";
    private static final String DISPLAY_NAME = "displayName";
    private static final String LORE = "lore";
    private static final String ENCHANTMENTS = "enchants";

    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject obj = json.getAsJsonObject();
            Material material = Material.getMaterial(obj.get(ItemStackAdapter.MATERIAL).getAsInt());
            ItemBuilder builder = new ItemBuilder(material, obj.get(ItemStackAdapter.AMOUNT).getAsInt(), (short) obj.get(ItemStackAdapter.DURABILITY).getAsInt());
            JsonElement displayName = obj.get(ItemStackAdapter.DISPLAY_NAME);
            JsonElement lore = obj.get(ItemStackAdapter.LORE);
            if (displayName != null) {
                builder.setTitle(ChatColor.translateAlternateColorCodes('&', displayName.getAsString()));
            }
            if (lore != null) {
                List<String> lores = AHPlugin.getGson().fromJson(lore, List.class);
                for (String loreStr : lores) {
                    builder.addLore(ChatColor.translateAlternateColorCodes('&', loreStr));
                }
            }
            JsonElement enchants = obj.get(ItemStackAdapter.ENCHANTMENTS);
            if (enchants != null) {
                Map<String, Double> enchantsMap = AHPlugin.getGson().fromJson(enchants, Map.class);
                for (Entry<String, Double> entry : enchantsMap.entrySet()) {
                    builder.addEnchantment(Enchantment.getByName(entry.getKey()), entry.getValue().intValue());
                }
            }
            return builder.build();
        } catch (Exception ex) {
            ex.printStackTrace();
            AHPlugin.get().getLogger().log(Level.WARNING, "Error encountered while deserializing a ItemStack.");
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        try {
            ItemMeta meta = src.getItemMeta();
            obj.addProperty(ItemStackAdapter.MATERIAL, src.getType().getId());
            obj.addProperty(ItemStackAdapter.AMOUNT, src.getAmount());
            obj.addProperty(ItemStackAdapter.DURABILITY, src.getDurability());
            obj.addProperty(ItemStackAdapter.DISPLAY_NAME, meta.getDisplayName());
            obj.add(ItemStackAdapter.LORE, AHPlugin.getGson().toJsonTree(meta.getLore(), List.class));
            Map<String, Integer> enchants = new HashMap<>();
            for (Entry<Enchantment, Integer> entry : src.getEnchantments().entrySet()) {
                enchants.put(entry.getKey().getName(), entry.getValue());
            }
            obj.add(ItemStackAdapter.ENCHANTMENTS, AHPlugin.getGson().toJsonTree(enchants, Map.class));
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            AHPlugin.get().getLogger().log(Level.WARNING, "Error encountered while serializing a ItemStack.");
            return obj;
        }
    }
}
