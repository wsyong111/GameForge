package io.github.wsyong11.gameforge.framework.dataflow;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.dataflow.codec.Codecs;
import io.github.wsyong11.gameforge.framework.dataflow.codec.SimpleCodecs;
import io.github.wsyong11.gameforge.framework.dataflow.codec.codec.CommonCodecs;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericInfo;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.codec.MapCodec;
import io.github.wsyong11.gameforge.framework.ex.CodecException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws CodecException {
		Codecs codecs = new SimpleCodecs();
		codecs.addGenericCodec(new MapCodec());

		codecs.addCodec(CommonCodecs.STRING_CODEC);
		codecs.addCodec(CommonCodecs.NUMBER_CODEC);

		System.out.println(codecs.encode(
			Map.of(
				"A", List.of(1, 2, 6)
			),
			new TypeToken<Map<String, ?>>() {})
		);
//		Element element = MutableElement
//			.object()
//			.add("store", MutableElement
//				.object()
//				.add("book", MutableElement
//					.array()
//					.add(MutableElement
//						.object()
//						.add("category", MutableElement.string("reference"))
//						.add("author", MutableElement.string("Nigel Rees"))
//						.add("price", MutableElement.number(8.95F))
//						.add("tags", MutableElement
//							.array()
//							.add(MutableElement.string("classic"))
//							.add(MutableElement.string("paperback"))))
//					.add(MutableElement
//						.object()
//						.add("category", MutableElement.string("fiction"))
//						.add("author", MutableElement.string("Evelyn Waugh"))
//						.add("price", MutableElement.number(12.99F))
//						.add("tags", MutableElement
//							.array()
//							.add(MutableElement.string("bestseller")))))
//				.add("bicycle", MutableElement
//					.object()
//					.add("color", MutableElement.string("red"))
//					.add("price", MutableElement.number(19.95F)))
//				.add("expensive", MutableElement.number(10)))
//			.asElement();
//
//		ElementPath path = ElementPathFactory.compileJsonPath("$.store.book[0].tags[*]");
//		System.out.println(path);
//		System.out.println(path.match(element));
	}
}
