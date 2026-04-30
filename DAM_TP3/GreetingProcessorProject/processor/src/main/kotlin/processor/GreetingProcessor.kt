package processor

import annotations.Greeting
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes("annotations.Greeting")
class GreetingProcessor : BaseProcessor() {

    override fun getAnnotationClass() = Greeting::class.java

    override fun generateClass(classElement: TypeElement, methods: List<ExecutableElement>) {
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        val originalClassName = classElement.simpleName.toString()
        val wrapperClassName = "${originalClassName}Wrapper"

        val classBuilder = TypeSpec.classBuilder(wrapperClassName)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("original", ClassName(packageName, originalClassName))
                    .build()
            )
            .addProperty(
                PropertySpec.builder("original", ClassName(packageName, originalClassName))
                    .initializer("original")
                    .build()
            )
            .addModifiers(KModifier.PUBLIC, KModifier.FINAL)

        for (method in methods) {
            val methodName = method.simpleName.toString()
            val parameters = method.parameters.map { param ->
                ParameterSpec.builder(param.simpleName.toString(), param.asType().asTypeName()).build()
            }
            val arguments = method.parameters.joinToString(", ") { it.simpleName.toString() }
            val greetingMessage = method.getAnnotation(Greeting::class.java)?.message ?: "Hello!"

            val methodBuilder = FunSpec.builder(methodName)
                .addModifiers(KModifier.PUBLIC, KModifier.FINAL)
                .addParameters(parameters)
                .addStatement("println(%S)", greetingMessage)
                .addStatement("original.$methodName($arguments)")

            classBuilder.addFunction(methodBuilder.build())
        }

        writeToFile(packageName, wrapperClassName, classBuilder.build())
    }
}