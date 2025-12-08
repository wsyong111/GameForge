package io.github.wsyong11.gameforge.framework.dataflow.parser;

import com.google.gson.*;
import io.github.wsyong11.gameforge.framework.dataflow.element.*;
import io.github.wsyong11.gameforge.framework.dataflow.ex.ElementCodecException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class JsonElementCodec implements ElementCodec {
	private final Gson gson;

	public JsonElementCodec() {
		this(new GsonBuilder()
			.serializeNulls()
			.disableHtmlEscaping()
			.setPrettyPrinting()
			.create());
	}

	public JsonElementCodec(@NotNull Gson gson) {
		Objects.requireNonNull(gson, "gson is null");
		this.gson = gson;
	}

	@NotNull
	@Override
	public Element decode(@NotNull String data) throws ElementCodecException {
		Objects.requireNonNull(data, "data is null");

		JsonElement json;
		try {
			json = this.gson.fromJson(data, JsonElement.class);
		} catch (JsonSyntaxException e) {
			throw new ElementCodecException("Failed decode JSON with gson", e);
		}

		try {
			return this.jsonToElement(json);
		} catch (UnsupportedOperationException | IllegalStateException e) {
			throw new ElementCodecException("Cannot cast json to element", e);
		}
	}

	@NotNull
	@Override
	public String encode(@NotNull Element element) throws ElementCodecException {
		Objects.requireNonNull(element, "element is null");

		JsonElement json;
		try {
			json = this.elementToJson(element);
		} catch (UnsupportedOperationException | IllegalStateException e) {
			throw new ElementCodecException("Cannot cast json to element", e);
		}

		return this.gson.toJson(json);
	}

	@NotNull
	protected Element jsonToElement(@NotNull JsonElement json) {
		Objects.requireNonNull(json, "json is null");
		return this.jsonToElement(json, Collections.newSetFromMap(new IdentityHashMap<>()));
	}

	@NotNull
	private Element jsonToElement(@NotNull JsonElement json, @NotNull Set<JsonElement> visitedSet) {
		Objects.requireNonNull(json, "json is null");
		Objects.requireNonNull(visitedSet, "visitedSet is null");

		try {
			if (!visitedSet.add(json))
				throw new IllegalStateException("Circular reference detected at: " + System.identityHashCode(json));

			if (json instanceof JsonObject object)
				return Element.object(object
					.entrySet()
					.stream()
					.map(entry -> Map.entry(
						entry.getKey(),
						this.jsonToElement(entry.getValue(), visitedSet)
					))
					.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)));

			if (json instanceof JsonArray array)
				return Element.array(array
					.asList()
					.stream()
					.map(item -> this.jsonToElement(item, visitedSet))
					.toList());

			if (json instanceof JsonPrimitive primitive) {
				if (primitive.isBoolean())
					return Element.bool(primitive.getAsBoolean());

				if (primitive.isNumber())
					return Element.number(primitive.getAsNumber());

				return Element.string(primitive.getAsString());
			}

			if (json instanceof JsonNull)
				return Element.nil();

			throw new UnsupportedOperationException("Cannot cast " + json.getClass().getName() + " to element");
		} finally {
			visitedSet.remove(json);
		}
	}

	@NotNull
	protected JsonElement elementToJson(@NotNull Element element) {
		Objects.requireNonNull(element, "element is null");
		return this.elementToJson(element, Collections.newSetFromMap(new IdentityHashMap<>()));
	}

	@NotNull
	private JsonElement elementToJson(@NotNull Element element, @NotNull Set<Element> visitedSet) {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(visitedSet, "visitedSet is null");

		try {
			if (!visitedSet.add(element))
				throw new IllegalStateException("Circular reference detected at: " + System.identityHashCode(element));

			if (element instanceof ObjectElement object) {
				JsonObject obj = new JsonObject();
				object.forEachEntry((k, v) -> obj.add(k, this.elementToJson(v, visitedSet)));
				return obj;
			}

			if (element instanceof ArrayElement array)
				return array
					.asList()
					.stream()
					.map(item -> this.elementToJson(item, visitedSet))
					.collect(Collector.of(
						JsonArray::new,
						JsonArray::add,
						(obj1, obj2) -> {
							obj1.addAll(obj2);
							return obj1;
						}
					));

			if (element instanceof StringElement string)
				return new JsonPrimitive(string.getValue());

			if (element instanceof BooleanElement bool)
				return new JsonPrimitive(bool.getValue());

			if (element instanceof NumberElement number)
				return new JsonPrimitive(number.getNumber());

			if (element instanceof NullElement)
				return JsonNull.INSTANCE;

			throw new UnsupportedOperationException("Cannot cast " + element.getClass().getName() + " to json");
		} finally {
			visitedSet.remove(element);
		}
	}
}
