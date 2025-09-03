package io.github.wsyong11.gameforge.util

import java.util.function.BooleanSupplier
import java.util.function.Function
import java.util.function.Supplier

abstract class ImplSelector<T> : Supplier<T> {
	// -------------------------------------------------------------------------------------------------------------- //
	override fun get(): T {
		val impl: T = this.getImpl() ?: throw IllegalArgumentException("Cannot get impl")
		return impl
	}

	protected abstract fun getImpl(): T?

	// -------------------------------------------------------------------------------------------------------------- //
	private class BooleanImplSelector<T>(
		private val checker: BooleanSupplier,
		private val falseImpl: Supplier<out T?>,
		private val trueImpl: Supplier<out T?>,
	) : ImplSelector<T>() {
		override fun getImpl() =
			if (this.checker.asBoolean) this.trueImpl.get() else this.falseImpl.get()
	}

	private class EnumImplSelector<T, E : Enum<E>>(
		private val checker: Supplier<E>,
		private val getter: Function<E, T?>,
	) : ImplSelector<T>() {
		override fun getImpl() =
			this.getter.apply(this.checker.get())
	}

	companion object {
		@JvmStatic
		fun <T> forBoolean(
			checker: BooleanSupplier,
			falseImpl: Supplier<out T?>,
			trueImpl: Supplier<out T?>,
		): ImplSelector<T> {
			return BooleanImplSelector(checker, falseImpl, trueImpl)
		}

		@JvmStatic
		fun <T, E : Enum<E>> forEnum(checker: Supplier<E>, getter: Function<E, T?>): ImplSelector<T> {
			return EnumImplSelector(checker, getter)
		}

		@JvmStatic
		fun <T, E : Enum<E>> forEnum(
			checker: Supplier<E>,
			defaultGetter: Function<E, T?>?,
			castMap: Map<E, T>,
		): ImplSelector<T> {
			return forEnum(checker) {
				castMap[it] ?: defaultGetter?.apply(it)
			}
		}
	}
}
