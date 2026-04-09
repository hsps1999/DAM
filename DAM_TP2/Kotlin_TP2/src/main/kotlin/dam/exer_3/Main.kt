package dam.exer_3

fun main() {
    val logs = listOf(
        " INFO: server started ",
        " ERROR: disk full ",
        " DEBUG: checking config ",
        " ERROR: out of memory ",
        " INFO: request received ",
        " ERROR: connection timeout "
    )

    val pipeline = buildPipeline {
        addStage("Trim") { list ->
            list.map { it.trim() }
        }

        addStage("Filter errors") { list ->
            list.filter { it.contains("ERROR") }
        }

        addStage("Uppercase") { list ->
            list.map { it.uppercase() }
        }

        addStage("Add index") { list ->
            list.mapIndexed { idx, line -> "${idx + 1}. $line" }
        }
    }

    println("Pipeline stages:")
    pipeline.describe()

    println("\nResult:")
    pipeline.execute(logs).forEach { println(it) }

    println()
    println("==CHALLENGE==")

    // == COMPOSE ==
    pipeline.compose("Trim", "Filter errors", "Clean")

    pipeline.describe()
    // 1. Clean (substituiu os dois)
    // 2. Uppercase
    // 3. Add index

    // == FORK ==
    val pipelineErros = buildPipeline {
        addStage("Trim") { list -> list.map { it.trim() } }
        addStage("Filter errors") { list -> list.filter { it.contains("ERROR") } }
    }

    val pipelineInfo = buildPipeline {
        addStage("Trim") { list -> list.map { it.trim() } }
        addStage("Filter info") { list -> list.filter { it.contains("INFO") } }
    }

    val (erros, infos) = pipelineErros.fork(pipelineInfo, logs)

    println(erros)
    // [ERROR: disk full, ERROR: out of memory, ERROR: connection timeout]

    println(infos)
    // [INFO: server started, INFO: request received]
}