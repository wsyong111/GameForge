package io.github.wsyong11.gameforge.framework.dataflow.schema.dsl

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder.*

@SchemaDSL
abstract class SchemaTypeDSL<T : SchemaTypeBuilder<T>>(
	protected val builder: T,
) : BaseSchemaDSL{
	var require: Boolean = false

	fun get(): T = this.builder

	fun require() {
		this.require = true
	}
}

abstract class DefaultSchemaTypeDSL<T : DefaultSchemaTypeBuilder<T, V>, V>(builder: T) : SchemaTypeDSL<T>(builder) {
	var default: V? = null
		set(value) {
			this.builder.defaultValue(value)
			field = value
		}
}

// ---------------- //

@SchemaDSL
class ArraySchemaTypeDSL : SchemaTypeDSL<ArraySchemaTypeBuilder>(ArraySchemaTypeBuilder()) {
	var type: SchemaTypeBuilder<*>? = null
		set(value) {
			this.builder.itemType(value)
			field = value
		}
}

@SchemaDSL
class ObjectSchemaTypeDSL : SchemaTypeDSL<ObjectSchemaTypeBuilder>(ObjectSchemaTypeBuilder()) {
	private fun addField(name: String, dsl: SchemaTypeDSL<*>) {
		if (dsl.require)
			this.builder.requireField(name, dsl.get())
		else
			this.builder.field(name, dsl.get())
	}

	infix fun String.obj(block: ObjectSchemaTypeDSL.() -> Unit) =
		this@ObjectSchemaTypeDSL.addField(this, ObjectSchemaTypeDSL().apply(block))

	infix fun String.string(block: StringSchemaTypeDSL.() -> Unit) =
		this@ObjectSchemaTypeDSL.addField(this, StringSchemaTypeDSL().apply(block))

	infix fun String.integer(block: NumberSchemaTypeDSL.() -> Unit) =
		this@ObjectSchemaTypeDSL.addField(this, NumberSchemaTypeDSL().apply(block))

	infix fun String.boolean(block: BooleanSchemaTypeDSL.() -> Unit) =
		this@ObjectSchemaTypeDSL.addField(this, BooleanSchemaTypeDSL().apply(block))

	infix fun String.enum(block: EnumSchemaTypeDSL.() -> Unit) =
		this@ObjectSchemaTypeDSL.addField(this, EnumSchemaTypeDSL().apply(block))

	infix fun String.array(block: ArraySchemaTypeDSL.() -> Unit) =
		this@ObjectSchemaTypeDSL.addField(this, ArraySchemaTypeDSL().apply(block))
}

@SchemaDSL
class StringSchemaTypeDSL : DefaultSchemaTypeDSL<StringSchemaTypeBuilder, String>(StringSchemaTypeBuilder()) {
	var pattern: String? = null
		set(value) {
			this.builder.pattern(value)
			field = value
		}
}

@SchemaDSL
class NumberSchemaTypeDSL : DefaultSchemaTypeDSL<NumberSchemaTypeBuilder, Number>(
	NumberSchemaTypeBuilder()
) {
	var minimum: Number? = null
		set(value) {
			this.builder.minimum(value ?: Int.MIN_VALUE)
			field = value
		}

	var maximum: Number? = null
		set(value) {
			this.builder.maximum(value ?: Int.MAX_VALUE)
			field = value
		}

	fun <T : Comparable<T>> range(r: ClosedRange<T>) {
		when (r) {
			is IntRange -> {
				minimum = r.first.toLong()
				maximum = r.last.toLong()
			}

			is LongRange -> {
				minimum = r.first
				maximum = r.last
			}

			is CharRange -> {
				minimum = r.first.code.toLong()
				maximum = r.last.code.toLong()
			}

			else -> error("Unsupported range type ${r::class}")
		}
	}
}

@SchemaDSL
class BooleanSchemaTypeDSL : DefaultSchemaTypeDSL<BooleanSchemaTypeBuilder, Boolean>(BooleanSchemaTypeBuilder())

@SchemaDSL
class EnumSchemaTypeDSL : DefaultSchemaTypeDSL<EnumSchemaTypeBuilder, String>(EnumSchemaTypeBuilder()) {
	var ignoreCase = false
		set(value) {
			this.builder.ignoreCase(value)
			field = value
		}

	fun ignoreCase() {
		this.ignoreCase = true
	}

	fun add(vararg enumValue: String) {
		this.builder.add(*enumValue)
	}

	operator fun Iterable<String>.unaryPlus() {
		this@EnumSchemaTypeDSL.builder.add(this)
	}

	operator fun String.unaryPlus() {
		this@EnumSchemaTypeDSL.builder.add(this)
	}
}
