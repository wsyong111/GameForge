package io.github.wsyong11.gameforge.framework.config.annotation.processor;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.palantir.javapoet.*;
import io.github.wsyong11.gameforge.framework.config.annotation.AttrGetter;
import io.github.wsyong11.gameforge.framework.config.annotation.ConfigAutoGenerate;
import io.github.wsyong11.gameforge.framework.config.annotation.DefaultValueProvider;
import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPathFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@AutoService(Processor.class)
public class AutoGenerateConfigAnnotationProcessor extends AbstractProcessor {
	@Override
	public boolean process(@NotNull Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {
		Messager messager = this.processingEnv.getMessager();
		Elements elementUtils = this.processingEnv.getElementUtils();
		Filer filer = this.processingEnv.getFiler();

		for (Element element : roundEnv.getElementsAnnotatedWith(ConfigAutoGenerate.class)) {
			if (element.getKind() != ElementKind.CLASS)
				continue;

			TypeElement type = (TypeElement) element;
			TypeSpec implClass = this.processClass(type, roundEnv);
			if (implClass == null)
				return false;

			try {
				String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
				JavaFile.builder(packageName, implClass)
					.addFileComment("!! Generate by build system !! //")
					.build()
					.writeTo(filer);
			} catch (IOException e) {
				String msg = ExceptionUtils.getStackTrace(e);
				messager.printMessage(Diagnostic.Kind.ERROR, "Cannot write source file\n" + msg);
			}
		}

		return true;
	}

	@Nullable
	private TypeSpec processClass(@NotNull TypeElement classElement, @NotNull RoundEnvironment env) {
		Messager messager = this.processingEnv.getMessager();
		Types typeUtils = this.processingEnv.getTypeUtils();

		ClassName className = ClassName.get(classElement);

		TypeSpec.Builder implClass = TypeSpec
			.classBuilder(className.simpleName() + "$$Impl")
			.superclass(className)
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL);

		for (Element enclosedElement : classElement.getEnclosedElements()) {
			if (enclosedElement.getKind() != ElementKind.METHOD)
				continue;

			ExecutableElement method = (ExecutableElement) enclosedElement;
			AttrGetter annotation = method.getAnnotation(AttrGetter.class);
			if (annotation == null)
				continue;

			if (!method.getModifiers().contains(Modifier.PUBLIC))
				continue;

			String jsonPath = annotation.value();
			String methodName = method.getSimpleName().toString();
			TypeMirror returnType = method.getReturnType();
			TypeName returnTypeName = TypeName.get(returnType);

			String patternFieldName = "$PATTERN_" + methodName;
			implClass.addField(FieldSpec
				.builder(ElementPath.class, patternFieldName)
				.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer(CodeBlock
					.builder()
					.add("$T.compileJsonPath($S)", ElementPathFactory.class, jsonPath)
					.build())
				.build());


			DefaultValueProvider defaultValueProvider = method.getAnnotation(DefaultValueProvider.class);
			CodeBlock code;
			if (defaultValueProvider == null) {
				code = CodeBlock
					.builder()
					.addStatement("return this.get($N, $T.class, null)", patternFieldName, returnTypeName)
					.build();
			} else {
				String valueProviderMethodName = defaultValueProvider.value();

				ExecutableElement valueProviderMethod = classElement
					.getEnclosedElements()
					.stream()
					.filter(e -> e.getKind() == ElementKind.METHOD)
					.map(e -> (ExecutableElement) e)
					.filter(e-> !e.getModifiers().contains(Modifier.PRIVATE))
					.filter(e -> e.getSimpleName().contentEquals(valueProviderMethodName))
					.filter(e -> e.getParameters().isEmpty())
					.filter(e -> typeUtils.isSameType(e.getReturnType(), returnType))
					.findFirst()
					.orElse(null);

				if (valueProviderMethod == null) {
					messager.printMessage(Diagnostic.Kind.ERROR, "Default value provider method not found " + className + "#" + valueProviderMethodName + "()");
					return null;
				}

				code = CodeBlock
					.builder()
					.addStatement("return this.get($N, $T.class, $N())", patternFieldName, returnTypeName, valueProviderMethod.getSimpleName())
					.build();
			}

			implClass.addMethod(MethodSpec
				.methodBuilder(methodName)
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.returns(returnTypeName)
				.addCode(code)
				.build());
		}

		return implClass.build();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(ConfigAutoGenerate.class.getName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_17;
	}
}
