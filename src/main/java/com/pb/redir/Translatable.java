package com.pb.redir;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.MutableComponent;

public class Translatable {
	public static MutableComponent create(String string) {
		/*if(Integer.parseInt(Countrywar.version.split("\\.")[1]) >= 19) {
			return Component.translatable(string);
		} else {*/
			return new TranslatableComponent(string).copy();
		// }
    }

	public static MutableComponent create(String string, Object ... arrobject) {
		/* if(Integer.parseInt(Countrywar.version.split("\\.")[1]) >= 19) {
			return Component.translatable(string, arrobject);
		} else { */
			return new TranslatableComponent(string, arrobject).copy();
		// }
    }
}
