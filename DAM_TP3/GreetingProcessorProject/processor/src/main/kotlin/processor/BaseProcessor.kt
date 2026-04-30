package processor

import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

abstract class BaseProcessor : AbstractProcessor() {

    // Cada subclasse diz qual anotação processa
    abstract fun getAnnotationClass(): Class<out Annotation>
    // Cada subclasse define o que gera
    abstract fun generateClass(classElement: TypeElement, methods: List<ExecutableElement>)

    /*
    * Percorre anotações e agrupa por classe
    * */
    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()

        for (element in roundEnv.getElementsAnnotatedWith(getAnnotationClass())) {
            if (element is ExecutableElement) {
                val enclosingClass = element.enclosingElement as TypeElement
                classMethodMap.computeIfAbsent(enclosingClass) { mutableListOf() }.add(element)
            }
        }

        for ((classElement, methods) in classMethodMap) {
            generateClass(classElement, methods)
        }

        return true
    }

    /*
    * Escreve o ficheiro gerado
    * */
    protected fun writeToFile(packageName: String, fileName: String, typeSpec: TypeSpec) {
        val file = FileSpec.builder(packageName, fileName)
            .addType(typeSpec)
            .build()

        try {
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                file.writeTo(File(kaptKotlinGeneratedDir))
            } else {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "kapt.kotlin.generated not found"
                )
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Error generating Kotlin file: ${e.message}"
            )
        }
    }
}