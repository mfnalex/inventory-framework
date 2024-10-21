package asd;

import me.devnatan.inventoryframework.AnvilInputFeature;
import me.devnatan.inventoryframework.ViewFrame;

import me.devnatan.inventoryframework.runtime.thirdparty.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

public class Asd extends JavaPlugin implements Listener {

	ViewFrame viewFrame = null;

	@Override
	public void onEnable() {

		// Register views.
		viewFrame = ViewFrame.create(this).install(AnvilInputFeature.AnvilInput).defaultConfig(config -> {
			config.cancelOnDrop();
			config.cancelOnDrag();
			config.cancelOnClick();
			config.interactionDelay(Duration.ofMillis(300));
		}).with(
			new MyView()
		).register();

		Bukkit.getPluginManager().registerEvents(this, this);

		ReflectionUtils.supports(21);

	}

	@EventHandler
	public void asd(PlayerToggleSneakEvent event) {
		viewFrame.open(MyView.class, event.getPlayer());
	}
}
