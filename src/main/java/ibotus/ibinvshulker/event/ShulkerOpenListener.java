package ibotus.ibinvshulker.event;

import ibotus.ibinvshulker.configurations.Config;
import ibotus.ibinvshulker.utils.HexColor;
import ibotus.ibinvshulker.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShulkerOpenListener implements Listener {

    private final Map<Player, ItemStack> openedShulkers = new HashMap<>();
    private final JavaPlugin plugin;

    public ShulkerOpenListener(JavaPlugin plugin) {
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
                playCombatSound(player);
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
                        openedShulkers.put(player, item);
                        event.setCancelled(true);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!isShulkerInInventory(player, item)) {
                                    player.closeInventory();
                                    openedShulkers.remove(player);
                                    cancel();
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (openedShulkers.containsKey(player)) {
            saveShulkerContents(player, event.getInventory());
            openedShulkers.remove(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (openedShulkers.containsKey(player)) {
            Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory != null && clickedInventory.equals(player.getInventory())) {
                ItemStack currentItem = event.getCurrentItem();
                ItemStack cursorItem = event.getCursor();

                if ((currentItem != null && currentItem.equals(openedShulkers.get(player))) ||
                        (cursorItem != null && cursorItem.equals(openedShulkers.get(player))) ||
                        event.getSlot() == player.getInventory().getHeldItemSlot()) {
                    event.setCancelled(true);
                }

                if (event.getClick().isKeyboardClick()) {
                    int hotbarButton = event.getHotbarButton();
                    if (hotbarButton >= 0 && hotbarButton < player.getInventory().getSize()) {
                        ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);
                        if (hotbarItem != null && hotbarItem.equals(openedShulkers.get(player))) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (openedShulkers.containsKey(player)) {
            ItemStack mainHandItem = event.getMainHandItem();
            ItemStack offHandItem = event.getOffHandItem();
            if ((mainHandItem != null && mainHandItem.equals(openedShulkers.get(player))) ||
                    (offHandItem != null && offHandItem.equals(openedShulkers.get(player)))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (openedShulkers.containsKey(player)) {
            saveShulkerContents(player, player.getOpenInventory().getTopInventory());
        }
    }

    private void saveShulkerContents(Player player, Inventory inventory) {
        if (openedShulkers.containsKey(player)) {
            ItemStack item = openedShulkers.get(player);
            if (item != null && item.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                BlockState state = meta.getBlockState();
                if (state instanceof ShulkerBox) {
                    ShulkerBox shulkerBox = (ShulkerBox) state;
                    Inventory shulkerInventory = shulkerBox.getInventory();

                    ItemStack[] newContents = new ItemStack[shulkerInventory.getSize()];
                    ItemStack[] eventContents = inventory.getContents();
                    int minSize = Math.min(eventContents.length, newContents.length);
                    for (int i = 0; i < minSize; i++) {
                        if (eventContents[i] != null) {
                            newContents[i] = eventContents[i];
                        }
                    }
                    shulkerInventory.setContents(newContents);
                    meta.setBlockState(shulkerBox);
                    item.setItemMeta(meta);

                    openedShulkers.put(player, item);
                    openedShulkers.remove(player);
                }
            }
        }
    }

    private boolean isShulkerInInventory(Player player, ItemStack shulker) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.equals(shulker)) {
                return true;
            }
        }
        return false;
    }

    private void playCombatSound(Player player) {
        String soundKey = "sound.sound-combat";
        String soundConfig = Config.getConfig().getString(soundKey);
        if (soundConfig != null) {
            String[] soundParts = soundConfig.split(":");
            if (soundParts.length == 3) {
                try {
                    Sound sound = Sound.valueOf(soundParts[0]);
                    float volume = Float.parseFloat(soundParts[1]);
                    float pitch = Float.parseFloat(soundParts[2]);
                    player.playSound(player.getLocation(), sound, volume, pitch);
                    player.sendMessage(Objects.requireNonNull(HexColor.color(Objects.requireNonNull(Config.getConfig().getString("shulker-open.shulker-combat-message")))));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Неверное название звука для " + soundKey + ": " + soundParts[0]);
                }
            } else {
                Bukkit.getLogger().warning("Неверный формат конфигурации звука для " + soundKey + ". Ожидаемый формат: sound:volume:pitch");
            }
        }
    }
}
