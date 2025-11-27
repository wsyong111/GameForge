package io.github.wsyong11.gameforge.framework.dataflow;

import io.github.wsyong11.gameforge.framework.dataflow.element.ArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.ObjectElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.StringElement;
import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import io.github.wsyong11.gameforge.framework.dataflow.schema.Schema;
import io.github.wsyong11.gameforge.framework.dataflow.schema.Schemas;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Main {
	private static final Schema SHADER_CONFIG_SCHEMA = Schema
		.builder()
		.type(SchemaType
			.newObject()
			.requireField("program", SchemaType
				.newObject()
				.requireField("vertex", Schemas.IDENTIFIER_SCHEMA_TYPE)
				.field("tessellationControl", Schemas.IDENTIFIER_SCHEMA_TYPE)
				.field("tessellationEvaluation", Schemas.IDENTIFIER_SCHEMA_TYPE)
				.field("geometry", Schemas.IDENTIFIER_SCHEMA_TYPE)
				.requireField("fragment", Schemas.IDENTIFIER_SCHEMA_TYPE))
			.requireField("attribute", SchemaType
				.newArray()
				.uniqueItems()
				.itemType(SchemaType
					.newObject()
					.requireField("name", SchemaType
						.newString()
						.minLength(1))
					.requireField("type", SchemaType
						.newString()
						.minLength(1))))
			.field("uniform", SchemaType
				.newObject()
				.additionalProperties(SchemaType
					.newObject()
					.requireField("type", SchemaType.newString())
					.field("system", SchemaType.newBoolean())
					.field("default", SchemaType
						.newArray()
						.minItems(1)
						.itemType(SchemaType.newNumber())))))
		.build();

	public static void main(String[] args) {
		/*
		{
			"store": {
				"book": [
					{
						"category": "reference",
						"author": "Nigel Rees",
						"price": 8.95
					},
					{
						"category": "fiction",
						"author": "Evelyn Waugh",
						"price": 12.99
					},
					{
						"category": "fiction",
						"author": "Herman Melville",
						"price": 8.99,
						"isbn": "0-553-21311-3"
					},
					{
						"category": "fiction",
						"author": "J. R. R. Tolkien",
						"price": 22.99,
						"isbn": "0-395-19395-8"
					},
					{
						"category": "game",
						"author": "J. R. R. Tolkien",
						"price": 22.99,
						"isbn": "0-395-19395-8"
					}
				],
				"bicycle": {
					"color": "red",
					"price": 19.95
				}
			},
			"expensive": 10
		}
		*/

		Element element = Element.object(Map.of(
			"store", Element.object(Map.of(
				"book", Element.array(
					Element.object(Map.of(
						"category", Element.string("reference"),
						"author", Element.string("Nigel Rees"),
						"price", Element.primitive(8.95F)
					)),
					Element.object(Map.of(
						"category", Element.string("fiction"),
						"author", Element.string("Evelyn Waugh"),
						"price", Element.primitive(12.99F)
					)),
					Element.object(Map.of(
						"category", Element.string("fiction"),
						"author", Element.string("Herman Melville"),
						"price", Element.primitive(12.99F),
						"isbn", Element.string("0-553-21311-3")
					)),
					Element.object(Map.of(
						"category", Element.string("fiction"),
						"author", Element.string("J. R. R. Tolkien"),
						"price", Element.primitive(22.99F),
						"isbn", Element.string("0-395-19395-8")
					)),
					Element.object(Map.of(
						"category", Element.string("game"),
						"author", Element.string("J. R. R. Tolkien"),
						"price", Element.primitive(22.99F),
						"isbn", Element.string("0-395-19395-8")
					))
				),
				"bicycle", Element.object(Map.of(
					"color", Element.string("red"),
					"price", Element.primitive(19.95F)
				))
			)),
			"expensive", Element.primitive(10)
		));

		Element element1 = Element.object(Map.of(
			"a", Element.object(Map.of(
				"b", Element.object(Map.of(
					"c", Element.primitive(1)
				)),
				"d", Element.object(Map.of(
					"c", Element.primitive(2)
				))
			)),
			"c", Element.primitive(3)
		));

		ElementPath path = ElementPath.compile("$.store.book[:5]");
		System.out.println(path);

		List<Element> result = path.match(element);
		StringBuilder sb = new StringBuilder();
		for (Element item : result) {
			print(0, sb, item);
			System.out.println(sb);
			sb.setLength(0);
		}
	}

	private static void print(int tab, @NotNull StringBuilder sb, @NotNull Element element) {
		if (element instanceof ObjectElement object) {
			sb.append("{\n");
			for (String key : object.keys()) {
				sb.append("\t".repeat(tab + 1))
				  .append('"')
				  .append(StringEscapeUtils.escapeJson(key))
				  .append("\": ");
				print(tab + 1, sb, object.get(key));
				sb.append(",\n");
			}
			sb.append("\t".repeat(tab))
			  .append('}');
		} else if (element instanceof ArrayElement array) {
			sb.append("[\n");
			for (Element item : array) {
				sb.append("\t".repeat(tab + 1));
				print(tab + 1, sb, item);
				sb.append(",\n");
			}
			sb.append("\t".repeat(tab))
			  .append(']');
		} else if (element instanceof StringElement string) {
			sb.append('"').append(StringEscapeUtils.escapeJson(string.getValue())).append("\"");
		} else {
			sb.append(element);
		}
	}
}
