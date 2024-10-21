package asd;


import me.devnatan.inventoryframework.AnvilInput;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import org.jetbrains.annotations.NotNull;

public class MyView extends View {

	private final AnvilInput input = AnvilInput.createAnvilInput();

	@Override
	public void onInit(@NotNull ViewConfigBuilder config) {
		config.type(ViewType.ANVIL).with(input);
	}
}
