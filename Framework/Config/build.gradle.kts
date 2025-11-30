import com.palantir.javapoet.ClassName
import com.palantir.javapoet.TypeName
import io.github.wsyong11.gameforge.plugin.codegen.dsl.boxSafe
import io.github.wsyong11.gameforge.plugin.codegen.dsl.unboxSafe
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import java.util.*
import javax.lang.model.element.Modifier

//plugins {
//    id("io.github.wsyong11.gameforge.codegen")
//}
//
//codegen {
//    val jObjects = type(Objects::class)
//
//    val cNotNull = "org.jetbrains.annotations" withClass "NotNull"
//    val cNullable = "org.jetbrains.annotations" withClass "Nullable"
//
//    val dcConfigAccessor = "io.github.wsyong11.gameforge.framework.config" withClass "ConfigAccessor"
//
//    val dataTypes = listOf<Pair<String, TypeName>>(
//        "boolean" to jBoolean,
//        "byte" to jByte,
//        "short" to jShort,
//        "int" to jInt,
//        "long" to jLong,
//        "char" to jChar,
//        "float" to jFloat,
//        "double" to jDouble,
//        "string" to jString
//    )
//
//    register(dcConfigAccessor) {
//        createInterface(dcConfigAccessor) {
//            modifiers(Modifier.PUBLIC)
//
//            for (type in dataTypes) {
//                val methodName = "get${type.first.uppercaseFirstChar()}"
//                val typeName = type.second
//
//                method(methodName) {
//                    modifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
//                    parameter(jString, "key") {
//                        annotation(cNotNull)
//                    }
//                    returns(typeName.boxSafe())
//                    annotation(cNullable)
//                }
//
//                method(methodName) {
//                    val isPrimitive = typeName.isPrimitive || typeName.isBoxedPrimitive
//
//                    modifiers(Modifier.PUBLIC, Modifier.DEFAULT)
//                    parameter(jString, "key") {
//                        annotation(cNotNull)
//                    }
//                    parameter(typeName.unboxSafe(), "defaultValue") {
//                        if (!isPrimitive)
//                            annotation(cNotNull)
//                    }
//                    returns(typeName.unboxSafe())
//                    code {
//                        statement("\$T.requireNonNull(key, \"key is null\")", jObjects)
//                        if (!isPrimitive)
//                            statement("\$T.requireNonNull(defaultValue, \"defaultValue is null\")", jObjects)
//
//                        statement("\$T value = this.${methodName}(key)", typeName.boxSafe())
//                        returns("value == null ? defaultValue : value")
//                    }
//                }
//            }
//        }
//    }
//}
