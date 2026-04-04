package io.github.wsyong11.gameforge.framework.system.render.mesh.vertex;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.mesh.Mesh;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

public class VertexLayout {
	public static final VertexLayout LAYOUT = VertexLayout
		.builder()
		.mixin(BASE_LAYOUT)
		.add(LAYOUT2.get("my_attr"))
		.add("position", Mesh.ATTR_POSITION, VertexDataType.FLOAT3)
		.add("normal", Mesh.ATTR_NORMAL, VertexDataType.BYTE4_NORM)
		.add("uv", Mesh.ATTR_UV, VertexDataType.U_SHORT2_NORM)
		.build();

	@NotNull
	private static Builder builder() {
		return new Builder();
	}

	private final List<Item> items;
	private final Map<Identifier, Item> itemMap;
	private final Map<String, Item> itemKeyMap;

	protected VertexLayout(@NotNull List<Item> items) {
		Objects.requireNonNull(items, "items is null");

		this.items = List.copyOf(items);

		this.itemMap = new HashMap<>();
		this.itemKeyMap = new HashMap<>();
		for (Item item : items) {
			Identifier semantic = item.getSemantic();
			if (this.itemMap.containsKey(semantic))
				throw new IllegalArgumentException("Duplicate semantic found in items: " + semantic);

			this.itemMap.put(semantic, item);
			this.itemKeyMap.put(item.getKey(), item);
		}
	}

	@NotNull
	public Item getBySemantic(@NotNull Identifier semantic) {
		Objects.requireNonNull(semantic, "semantic is null");
		return this.itemMap.get(semantic);
	}

	public boolean hasSemantic(@NotNull Identifier semantic) {
		Objects.requireNonNull(semantic, "semantic is null");
		return this.itemMap.containsKey(semantic);
	}

	@NotNull
	public Item getByKey(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.itemKeyMap.get(key);
	}

	public boolean hasKey(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.itemKeyMap.containsKey(key);
	}

	@NotNull
	@UnmodifiableView
	public List<Item> getItems() {
		return this.items;
	}

	public static class Item {
		private final String key;
		private final VertexDataType dataType;
		private final Identifier semantic;

		public Item(@NotNull String key, @NotNull VertexDataType dataType, @NotNull Identifier semantic) {
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(dataType, "dataType is null");
			Objects.requireNonNull(semantic, "semantic is null");

			this.key = key;
			this.dataType = dataType;
			this.semantic = semantic;
		}

		@NotNull
		public String getKey() {
			return this.key;
		}

		@NotNull
		public VertexDataType getDataType() {
			return this.dataType;
		}

		@NotNull
		public Identifier getSemantic() {
			return this.semantic;
		}

		@Override
		public String toString() {
			return "[\"" + StringEscapeUtils.escapeJava(this.key) + "\" " + this.dataType + " " + this.dataType.getByteSize() + "bytes]";
		}
	}

	@Override
	public String toString() {
		return "VertexLayout[" + this.items
			.stream()
			.map(Item::toString)
			.collect(Collectors.joining()) + "]";
	}

	public static class Builder {
		private final Map<Identifier, Item> items;

		public Builder() {
			this.items = new LinkedHashMap<>();
		}

		@NotNull
		public Builder mixin(@NotNull VertexLayout layout, boolean cover) {
			Objects.requireNonNull(layout, "layout is null");

			List<Item> items = layout.getItems();
			for (Item item : items) {
				Identifier semantic = item.getSemantic();
				if (!cover && this.items.containsKey(semantic))
					continue;

				this.items.remove(semantic);
				this.items.put(semantic, item);
			}

			return this;
		}

		@NotNull
		public Builder add(@NotNull Item item) {
			Objects.requireNonNull(item, "item is null");

			Identifier semantic = item.getSemantic();
			if (this.items.containsKey(semantic))
				throw new IllegalStateException("Semantic " + semantic + " existed");

			this.items.put(semantic, item);
			return this;
		}

		@NotNull
		public Builder add(@NotNull String semantic, @NotNull String meshKey, @NotNull VertexDataType dataType) {
			Objects.requireNonNull(semantic, "semantic is null");
			Objects.requireNonNull(meshKey, "meshKey is null");
			Objects.requireNonNull(dataType, "dataType is null");
			return this.add(Identifier.parseTolerance(semantic), meshKey, dataType);
		}

		@NotNull
		public Builder add(@NotNull Identifier semantic, @NotNull String meshKey, @NotNull VertexDataType dataType) {
			Objects.requireNonNull(semantic, "semantic is null");
			Objects.requireNonNull(meshKey, "meshKey is null");
			Objects.requireNonNull(dataType, "dataType is null");

			if (this.items.containsKey(semantic))
				throw new IllegalStateException("Semantic " + semantic + " existed");

			this.items.put(semantic, new Item(meshKey, dataType, semantic));
			return this;
		}

		@NotNull
		public Builder replace(@NotNull String semantic, @NotNull String meshKey, @NotNull VertexDataType dataType) {
			Objects.requireNonNull(semantic, "semantic is null");
			Objects.requireNonNull(meshKey, "meshKey is null");
			Objects.requireNonNull(dataType, "dataType is null");
			return this.replace(Identifier.parseTolerance(semantic), meshKey, dataType);
		}

		@NotNull
		public Builder replace(@NotNull Item item) {
			Objects.requireNonNull(item, "item is null");
			this.items.put(item.getSemantic(), item);
			return this;
		}

		@NotNull
		public Builder replace(@NotNull Identifier semantic, @NotNull String meshKey, @NotNull VertexDataType dataType) {
			Objects.requireNonNull(semantic, "semantic is null");
			Objects.requireNonNull(meshKey, "meshKey is null");
			Objects.requireNonNull(dataType, "dataType is null");
			this.items.put(semantic, new Item(meshKey, dataType, semantic));
			return this;
		}

		@NotNull
		public Builder remove(@NotNull Identifier semantic) {
			Objects.requireNonNull(semantic, "semantic is null");
			this.items.remove(semantic);
			return this;
		}

		@NotNull
		public VertexLayout build() {
			List<Item> items = List.copyOf(this.items.values());
			return new VertexLayout(items);
		}
	}
}
