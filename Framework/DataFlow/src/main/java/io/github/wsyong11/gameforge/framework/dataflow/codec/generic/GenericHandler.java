package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.info.GenericHandlerInfoBuilder;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface GenericHandler<T> {
	@NotNull
	Class<? super T> getType();

	void configuration(@NotNull GenericInfo.Builder<T> parameter);

//	@NotNull
//	List<TypeStructure> getStructure();
//
//	@NotNull
//	Element encode(
//		@NotNull CodecContext ctx,
//		@NotNull @UnmodifiableView List<Element> elements,
//		@NotNull Class<?> rawType,
//		@NotNull Type type
//	);
//
//	@NotNull
//	Object decode(
//		@NotNull CodecContext ctx,
//		@NotNull @UnmodifiableView List<Object> values,
//		@NotNull Class<?> rawType,
//		@NotNull Type type
//	);
}
/*
把编解码系统抽象成Codec<T>，GenericHandler和Codecs
Codecs负责管理和调度Codec和GenericHandler
GenericHandler负责解析Type嵌套转换成Class并编解码成容器并把泛型传递给GenericHandler（直到拆无可拆为止）或者Codec

Codecs.encode(value, token) : Map<Integer, Set<List<String>>>
| MapGenericHandler : Integer, Set<List<String>>
  | NumberCodec : Class<Integer>
  | SetGenericHandler : List<String>
    | ListGenericHandler : String
      | StringCodec : Class<String>

并通过缓存加速
GenericHandler
| buildCache() -> List<TypeCache>
| | TypeCache
|   | type: Class<?>
|   | handler: GenericHandler
|   | parameter: BitMap
| encode(List<Element>, Class<?>) -> Element // List中的每个元素对应泛型所在位置
| decode(Element, Class<?>) -> T

Map:
	TypeCache
	| type: Class<Map<?, ?>>
	| handler: MapGenericHandler
    | parameter: BitMap(11) // 两个泛型参数，没有自引用参数，设0忽略某个泛型参数
 */