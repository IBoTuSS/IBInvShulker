package ibotus.ibinvshulker.configurations;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Config {
    private static FileConfiguration config;

    public static void loadYaml(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static List<Material> getWhitelistedShulkers() {
        List<String> shulkerNames = config.getStringList("shulker-inventory.whitelist-shulker");
        return shulkerNames.stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
    }

    public static List<Material> getBlockedItems() {
        List<String> itemNames = config.getStringList("shulker-inventory.blacklist-items");
        return itemNames.stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
    }

    public static List<Material> getShulkerOpen() {
        List<String> shulkerNames = config.getStringList("shulker-open.shulker-list");
        return shulkerNames.stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
    }
}
