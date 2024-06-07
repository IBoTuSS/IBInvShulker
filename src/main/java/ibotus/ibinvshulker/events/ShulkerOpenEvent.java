package ibotus.ibinvshulker.events;

import ibotus.ibinvshulker.IBInvShulker;
import ibotus.ibinvshulker.configurations.Config;

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

public class ShulkerOpenEvent implements Listener {

    private final IBInvShulker plugin;

    public ShulkerOpenEvent(IBInvShulker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Config.getConfig().getBoolean("shulker-open.enabled")) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
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
                    shulkerInventory.setContents(event.getInventory().getContents());
                    meta.setBlockState(shulkerBox);
                    item.setItemMeta(meta);
                    player.getInventory().setItemInMainHand(item);
                    player.removeMetadata("openedShulker", plugin);
                }
            }
        }
    }
}
