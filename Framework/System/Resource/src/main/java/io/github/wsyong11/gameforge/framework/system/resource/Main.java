package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.AssetsResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourceWalkVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		AssetsResourcePack resourcePack = new AssetsResourcePack();
		System.out.println(resourcePack.getSource());

		System.out.println(resourcePack.isDirectory(ResourcePath.of("assets/")));
		resourcePack.walk(ResourcePath.of("/"), Integer.MAX_VALUE, new ResourceWalkVisitor() {
			private int space = 0;

			@Override
			public @NotNull VisitResult preVisitDirectory(@NotNull ResourcePath path) throws IOException {
				System.out.println("|   ".repeat(this.space) + "|- " + path.getName() + "/");
				this.space++;
				return VisitResult.CONTINUE;
			}

			@Override
			public @NotNull VisitResult postVisitDirectory(@NotNull ResourcePath path, @Nullable IOException exception) throws IOException {
				this.space--;
//				System.out.println("|   ".repeat(this.space));
				return VisitResult.CONTINUE;
			}

			@NotNull
			@Override
			public VisitResult visitFile(@NotNull ResourcePath path) throws IOException {
				System.out.println("|   ".repeat(this.space) + "|- " + path.getName());
				return VisitResult.CONTINUE;
			}
		});
	}
}