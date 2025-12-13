package io.github.wsyong11.gameforge.framework.dataflow;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPathFactory;

public class Main {
	public static void main(String[] args) {
		Element element = MutableElement
			.object()
			.add("store", MutableElement
				.object()
				.add("book", MutableElement
					.array()
					.add(MutableElement
						.object()
						.add("category", MutableElement.string("reference"))
						.add("author", MutableElement.string("Nigel Rees"))
						.add("price", MutableElement.number(8.95F))
						.add("tags", MutableElement
							.array()
							.add(MutableElement.string("classic"))
							.add(MutableElement.string("paperback"))))
					.add(MutableElement
						.object()
						.add("category", MutableElement.string("fiction"))
						.add("author", MutableElement.string("Evelyn Waugh"))
						.add("price", MutableElement.number(12.99F))
						.add("tags", MutableElement
							.array()
							.add(MutableElement.string("bestseller")))))
				.add("bicycle", MutableElement
					.object()
					.add("color", MutableElement.string("red"))
					.add("price", MutableElement.number(19.95F)))
				.add("expensive", MutableElement.number(10)))
			.asElement();

		ElementPath path = ElementPathFactory.compileJsonPath("$.store.book[0].tags[*]");
		System.out.println(path);
		System.out.println(path.match(element));
	}
}
