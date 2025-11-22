package io.github.wsyong11.gameforge.framework.dataflow.schema.dsl

import io.github.wsyong11.gameforge.framework.dataflow.schema.SchemaBuilder
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder.SchemaTypeBuilder

@SchemaDSL
class SchemaBuilderDSL : BaseSchemaDSL {
	var type: SchemaTypeBuilder<*>? = null

	fun build() =
		SchemaBuilder()
			.type(this.type?.build() ?: throw IllegalArgumentException("Require type field"))
			.build()
}

fun createSchema(block: SchemaBuilderDSL. () -> Unit) =
	SchemaBuilderDSL().apply(block).build()
