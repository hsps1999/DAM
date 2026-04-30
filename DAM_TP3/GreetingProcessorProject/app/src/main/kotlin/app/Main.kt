package app

fun main() {
    // Testar o GreetingProcessor
    val myClass = MyClass()
    val wrappedMyClass = MyClassWrapper(myClass)
    wrappedMyClass.sayHello()
    wrappedMyClass.compute()

    println("---")

    // Testar o RegexProcessor
    val input = "Name:John Address:123 Street"
    val extractor = DataProcessorExtractor(input)
    println("Name: ${extractor.getName()}")
    println("Address: ${extractor.getAddress()}")
}