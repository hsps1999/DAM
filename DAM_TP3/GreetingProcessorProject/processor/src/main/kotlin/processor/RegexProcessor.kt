package processor

import annotations.Extract
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes("annotations.Extract")
class RegexProcessor : BaseProcessor() {

    override fun getAnnotationClass() = Extract::class.java

    override fun generateClass(classElement: TypeElement, methods: List<ExecutableElement>) {
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        val originalClassName = classElement.simpleName.toString()
        val generatedClassName = "${originalClassName}Extractor"

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
            .superclass(ClassName(packageName, originalClassName))
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("input", String::class)
                    .build()
            )
            .addSuperclassConstructorParameter("input")

        for (method in methods) {
            val methodName = method.simpleName.toString()
            val regex = method.getAnnotation(Extract::class.java)?.regex ?: ""

            val methodBuilder = FunSpec.builder(methodName)
                .addModifiers(KModifier.OVERRIDE)
                .returns(String::class.asTypeName().copy(nullable = true))
                .addStatement("val match = Regex(%S).find(input)", regex)
                .addStatement("return match?.groupValues?.get(1)")

            classBuilder.addFunction(methodBuilder.build())
        }

        writeToFile(packageName, generatedClassName, classBuilder.build())
    }
}