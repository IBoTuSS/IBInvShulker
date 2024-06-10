package ibotus.ibinvshulker.event;

import ibotus.ibinvshulker.IBInvShulker;
import ibotus.ibinvshulker.configurations.Config;
import ibotus.ibinvshulker.utils.HexColor;
import ibotus.ibinvshulker.utils.Utils;

import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class ShulkerOpenEvent implements Listener {

    private final IBInvShulker plugin;

    public ShulkerOpenEvent(IBInvShulker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!Config.getConfig().getBoolean("shulker-open.enabled")) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!Config.getConfig().getBoolean("shulker-open.enable-in-combat") && Utils.isInCombat(player)) {
                String soundKey = "sound-combat";
                Sound sound = Sound.valueOf(Config.getConfig().getString(soundKey + ".sound"));
                float volume = (float) Config.getConfig().getDouble(soundKey + ".volume");
                float pitch = (float) Config.getConfig().getDouble(soundKey + ".pitch");
                player.playSound(player.getLocation(), sound, volume, pitch);
                player.sendMessage(Objects.requireNonNull(HexColor.color(Objects.requireNonNull(Config.getConfig().getString("shulker-open.shulker-combat-message")))));
                return;
            }
            if (Config.getShulkerOpen().contains(item.getType())) {
                if (item.getItemMeta() instanceof BlockStateMeta) {
                    BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                    BlockState state = meta.getBlockState();
                    if (state instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) state;
                        Inventory shulkerInventory = shulkerBox.getInventory();
                        player.openInventory(shulkerInventory);
                        player.setMetadata("openedShulker", new FixedMetadataValue(plugin, item));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.hasMetadata("openedShulker")) {
            ItemStack item = (ItemStack) player.getMetadata("openedShulker").get(0).value();
            assert item != null;
            if (item.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                BlockState state = meta.getBlockState();
                if (state instanceof ShulkerBox) {
                    ShulkerBox shulkerBox = (ShulkerBox) state;
                    Inventory shulkerInventory = shulkerBox.getInventory();

                    ItemStack[] newContents = new ItemStack[shulkerInventory.getSize()];
                    ItemStack[] eventContents = event.getInventory().getContents();
                    for (int i = 0; i < eventContents.length; i++) {
                        if (eventContents[i] != null) {
                            newContents[i] = eventContents[i];
                        }
                    }
                    shulkerInventory.setContents(newContents);
                    meta.setBlockState(shulkerBox);
                    item.setItemMeta(meta);
                    player.getInventory().setItemInMainHand(item);
                    player.removeMetadata("openedShulker", plugin);
                }
            }
        }
    }
}
