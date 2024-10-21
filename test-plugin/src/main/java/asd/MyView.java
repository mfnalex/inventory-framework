package asd;


import me.devnatan.inventoryframework.AnvilInput;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class MyView extends View {

	private final Logger log = Logger.getLogger(MyView.class.getName());

	private final AnvilInput input = AnvilInput.createAnvilInput();

	@Override
	public void onInit(@NotNull ViewConfigBuilder config) {
		config.type(ViewType.ANVIL).with(input);
		config.title("Hello World!");
	}


	@Override
	public void onFirstRender(@NotNull RenderContext render) {
		render.firstSlot(new ItemStack(Material.STONE));
		render.resultSlot()
			.onClick(context -> {
				log.warning(input.get(context));
			});
	}
}
