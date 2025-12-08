package io.github.wsyong11.gameforge.framework.dataflow.schema.dsl

interface BaseSchemaDSL {
	fun obj(block: ObjectSchemaTypeDSL.() -> Unit) =
		ObjectSchemaTypeDSL().apply(block).get()

	fun string(block: StringSchemaTypeDSL.() -> Unit) =
		StringSchemaTypeDSL().apply(block).get()

	fun integer(block: NumberSchemaTypeDSL.() -> Unit) =
		NumberSchemaTypeDSL().apply(block).get()

	fun boolean(block: BooleanSchemaTypeDSL.() -> Unit) =
		BooleanSchemaTypeDSL().apply(block).get()

	fun enum(block: EnumSchemaTypeDSL.() -> Unit) =
		EnumSchemaTypeDSL().apply(block).get()

	fun array(block: ArraySchemaTypeDSL.() -> Unit) =
		ArraySchemaTypeDSL().apply(block).get()
}
