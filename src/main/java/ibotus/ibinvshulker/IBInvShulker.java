package ibotus.ibinvshulker;

import ibotus.ibinvshulker.configurations.Config;
import ibotus.ibinvshulker.events.ShulkerInventoryEvent;
import ibotus.ibinvshulker.events.ShulkerOpenEvent;
import ibotus.ibinvshulker.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class IBInvShulker extends JavaPlugin implements Listener {

    private void msg(String msg) {
        String prefix = HexColor.color("&aIBInvShulker &7| ");
        Bukkit.getConsoleSender().sendMessage(HexColor.color(prefix + msg));
    }

    @Override
    public void onEnable() {
        Config.loadYaml(this);
        Bukkit.getConsoleSender().sendMessage("");
        this.msg("&fDeveloper: &aIBoTuS");
        this.msg("&fVersion: &dv" + this.getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("");
        this.getServer().getPluginManager().registerEvents(new ShulkerInventoryEvent(), this);
        this.getServer().getPluginManager().registerEvents(new ShulkerOpenEvent(this), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("");
        this.msg("&fDisable plugin.");
        Bukkit.getConsoleSender().sendMessage("");
    }

}
