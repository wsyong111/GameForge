package io.github.wsyong11.gameforge.framework.dataflow;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.dataflow.codec.Codecs;
import io.github.wsyong11.gameforge.framework.dataflow.codec.SimpleCodecs;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.handler.ListGenericHandler;
import io.github.wsyong11.gameforge.framework.ex.CodecException;

import java.util.List;

public class Main {
	public static void main(String[] args) throws CodecException {
		Codecs codecs = new SimpleCodecs();
		codecs.registerGenericHandler(new ListGenericHandler());

		System.out.println(codecs.encode(
			List.of("L", "K", "P"),
			new TypeToken<List<String>>() {})
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
