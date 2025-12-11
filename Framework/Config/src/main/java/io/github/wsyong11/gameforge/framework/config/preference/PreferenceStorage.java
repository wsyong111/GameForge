package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.preference.ex.PreferencesCodecException;
import io.github.wsyong11.gameforge.framework.config.preference.listener.PreferenceChangedListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * {@code PreferenceStorage} 是用于存储和管理应用配置首选项的接口。
 * 它支持基本的键值操作、自定义编码器管理、监听器通知以及批量编辑操作。
 * <p>
 * 实现类可以决定内部存储方式（如文件、数据库、内存等），并可选择异步或同步触发监听器。
 * </p>
 */
public interface PreferenceStorage {
	/**
	 * 判断指定键的首选项是否存在。
	 *
	 * @param key 要检查的首选项键，不能为 {@code null}。
	 * @return 如果首选项存在返回 {@code true}，否则返回 {@code false}。
	 */
	boolean contains(@NotNull String key);

	/**
	 * 列出所有已存储的首选项键。
	 *
	 * @return 不可修改的首选项键集合。
	 */
	@NotNull
	@Unmodifiable
	Set<String> getKeys();

	/**
	 * 获取指定键的首选项值。
	 *
	 * @param key  要获取的首选项键，不能为 {@code null}。
	 * @param type 期望的值类型，不能为 {@code null}。
	 * @param <T>  值的类型。
	 * @return 如果存在对应的首选项，返回对应类型的值；否则返回 {@code null}。
	 * @throws PreferencesCodecException 当解码失败时抛出。
	 */
	@Nullable
	<T> T getValue(@NotNull String key, @NotNull Class<T> type);

	/**
	 * 获取指定键的首选项值，如果不存在则返回默认值。
	 *
	 * @param key          要获取的首选项键，不能为 {@code null}。
	 * @param type         期望的值类型，不能为 {@code null}。
	 * @param defaultValue 如果首选项不存在则返回的默认值，可以为 {@code null}。
	 * @param <T>          值的类型。
	 * @return 如果存在对应的首选项，返回对应类型的值；否则返回 {@code defaultValue}。
	 * @throws PreferencesCodecException 当解码失败时抛出。
	 * @see #getValue(String, Class)
	 */
	@Nullable
	@Contract("_, _, !null -> !null; _, _, null -> _")
	default <T> T getValue(@NotNull String key, @NotNull Class<T> type, @Nullable T defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");

		T value = this.getValue(key, type);
		return value == null ? defaultValue : value;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 添加首选项变化监听器。
	 * <p>
	 * 监听器可能通过线程池异步触发，具体方式由实现类决定。
	 * </p>
	 *
	 * @param listener 要添加的监听器，不能为 {@code null}。
	 */
	void addChangeListener(@NotNull PreferenceChangedListener listener);

	/**
	 * 移除首选项变化监听器。
	 *
	 * @param listener 要移除的监听器，不能为 {@code null}。
	 */
	void removeChangeListener(@NotNull PreferenceChangedListener listener);

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 注册自定义编解码器。
	 * <p>
	 * 如果多个编解码器支持相同类型，将按注册顺序选择第一个返回 {@code true} 的编解码器。
	 * </p>
	 * <p>
	 * 解码过程中若编解码器抛出异常，该异常会被挂载在 {@link PreferencesCodecException#getSuppressed()} 列表中，
	 * 并尝试下一个可用编解码器。
	 * </p>
	 *
	 * @param codec 要注册的编解码器，不允许为 {@code null}。
	 */
	void registerCodec(@NotNull ValueCodec<?> codec);

	/**
	 * 注销已注册的编解码器。
	 *
	 * @param codec 要注销的编解码器，不能为 {@code null}。
	 */
	void unregisterCodec(@NotNull ValueCodec<?> codec);

	/**
	 * 获取当前注册的所有编解码器。
	 *
	 * @return 不可修改的编解码器列表，列表本身和元素都不能为空。
	 */
	@NotNull
	@Unmodifiable
	List<ValueCodec<?>> getCodecs();

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取首选项编辑器，用于批量修改首选项。
	 *
	 * @return 首选项编辑器实例。
	 */
	@NotNull
	Editor edit();

	/**
	 * 首选项编辑器接口，用于设置、删除、清空和提交首选项修改。
	 */
	interface Editor {
		/**
		 * 设置指定键的首选项值，值将在实际提交时序列化。
		 *
		 * @param key   要设置的首选项键，不能为 {@code null}。
		 * @param value 要设置的值，可以为 {@code null}。
		 * @param type  值的类型，不能为 {@code null}。
		 * @param <T>   值的类型。
		 * @return 当前编辑器实例，用于链式调用。
		 */
		@NotNull
		<T> Editor setValue(@NotNull String key, @Nullable T value, @NotNull Class<T> type);

		/**
		 * 删除指定的首选项。
		 *
		 * @param key 要删除的键，不能为 {@code null}。
		 * @return 当前编辑器实例，用于链式调用。
		 */
		@NotNull
		Editor delete(@NotNull String key);

		/**
		 * 清空所有已存储的首选项，包括当前未提交的修改。
		 *
		 * @return 当前编辑器实例，用于链式调用。
		 */
		@NotNull
		Editor clearAll();

		/**
		 * 取消所有未提交的修改。
		 */
		void cancel();

		/**
		 * 提交所有修改。
		 * <p>
		 * 提交方式可为同步或异步，由具体实现决定。
		 * 提交时会触发更改监听器，如果编码器在序列化过程中出现异常，
		 * 将通过 {@link PreferencesCodecException} 抛出；未出现异常的字段仍会被写入。
		 * </p>
		 *
		 * @throws PreferencesCodecException 序列化失败时抛出。
		 */
		void apply();
	}
}
