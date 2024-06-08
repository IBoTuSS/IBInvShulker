package ibotus.ibinvshulker.utils;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Utils {

    public static ICombatLogX getCombatLogXAPI() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin combatLogXPlugin = pluginManager.getPlugin("CombatLogX");
        if (combatLogXPlugin instanceof ICombatLogX) {
            return (ICombatLogX) combatLogXPlugin;
        }
        return null;
    }

    public static boolean isInCombat(Player player) {
        ICombatLogX combatLogX = getCombatLogXAPI();
        if (combatLogX != null) {
            ICombatManager combatManager = combatLogX.getCombatManager();
            return combatManager.isInCombat(player);
        }
        return false;
    }

}
