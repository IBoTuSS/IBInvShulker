package ibotus.ibinvshulker;

import ibotus.ibinvshulker.configurations.Config;
import ibotus.ibinvshulker.event.ShulkerInventoryEvent;
import ibotus.ibinvshulker.event.ShulkerOpenListener;
import ibotus.ibinvshulker.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class IBInvShulker extends JavaPlugin {

    private static IBInvShulker instance;

    private void msg(String msg) {
        String prefix = HexColor.color("&aIBInvShulker &7| ");
        Bukkit.getConsoleSender().sendMessage(HexColor.color(prefix + msg));
    }

    @Override
    public void onEnable() {
        instance = this;
        Config.loadYaml(this);
        Bukkit.getConsoleSender().sendMessage("");
        msg("&fDeveloper: &aIBoTuS");
        msg("&fVersion: &dv" + this.getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("");
        getServer().getPluginManager().registerEvents(new ShulkerInventoryEvent(), this);
        getServer().getPluginManager().registerEvents(new ShulkerOpenListener(), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("");
        msg("&fDisable plugin.");
        Bukkit.getConsoleSender().sendMessage("");
    }

    public static IBInvShulker getInstance() {
        return instance;
    }

}
