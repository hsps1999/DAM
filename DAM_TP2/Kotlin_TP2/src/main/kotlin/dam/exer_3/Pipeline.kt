package dam.exer_3

class Pipeline {
    // Data class Stage para guardar internamente
    // os dois dados juntos (nome e função)
    private data class Stage(
        val name: String,
        val transform: (List<String>) -> List<String> // list of transformation steps
    )

    // Lista de fases
    private val stages = mutableListOf<Stage>()

    // Acrescenta um stage com name ao pipeline
    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(Stage(name, transform))
    }

    fun execute(input: List<String>): List<String> {
        return stages.fold(input) {acc, stage -> stage.transform(acc)}
    }

    fun describe() {
        stages.forEachIndexed { idx, stage ->
            println("${idx + 1}. ${stage.name}")
        }
    }

    // Compose - funde dois stages existentes num só
    // através da função andThen
    fun compose(firstName: String, secondName: String, newName: String) {
        // procurar dois stages pelo nome
        val first = stages.find { it.name == firstName }?: error("Stage '$firstName' não encontrado")
        val second = stages.find { it.name == secondName }?: error("Stage '$secondName' não encontrado")

        // criar novo stage que aplica first e depois second
        val composed = Stage(newName) { input ->
            second.transform(first.transform(input))
        }

        // substitui os dois stages pelo novo, na posição do primeiro
        val insertAt = stages.indexOfFirst { it.name == firstName }
        stages.removeAll { it.name == firstName || it.name == secondName }
        stages.add(insertAt, composed)
    }

    // devolve o resultado de dois pipelines diferentes (this e other) juntos num Pair
    fun fork(other: Pipeline, input: List<String>): Pair<List<String>, List<String>> {
        return Pair(this.execute(input), other.execute(input))
    }
}

// fun top level com receiver lambda
// assim não é necessário repetir o pipeline
// em cada linha do Main
fun buildPipeline(block: Pipeline.() -> Unit) = Pipeline().apply(block)
