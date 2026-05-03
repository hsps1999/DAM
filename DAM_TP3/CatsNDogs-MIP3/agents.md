# AI Agent Guidelines — CatsNDogs MIP-3 (Multi-Module)

Este documento define as regras que o agente AntiGravity deve seguir durante toda a fase MIP-3. Substitui qualquer versão anterior do `agents.md` da fase MIP-2.

---

## Contexto do projeto

Este é o **MIP-3** do Tutorial 3 da disciplina DAM. Parte da app `CatsNDogs` desenvolvida no MIP-2 (Tutorial 2) — uma aplicação Android em Kotlin + XML Views que consome a Dog CEO API, com MVVM, Room (cache + favoritos), offline access e error handling.

O objetivo do MIP-3 é **refatorar essa app numa arquitetura multi-módulo** com três módulos Gradle:

- `:core` — biblioteca partilhada com data layer e business logic
- `:app-xml` — versão da app com UI XML (refactor da app original)
- `:app-compose` — versão nova da app com UI em Jetpack Compose

Ambas as apps devem consumir **exatamente o mesmo `:core`**, sem duplicar lógica.

---

## Regras do agente

### Planning-first (regra absoluta)

1. **Antes de gerar qualquer código**, lê todos os ficheiros em `docs/MIP3/`.
2. Para cada step do `04_implementation_plan.md`:
   - Lê o step
   - Resume o que vais fazer (ficheiros a alterar/criar, conteúdo, ordem)
   - **Espera pela minha confirmação** antes de aplicar mudanças
3. Não combinar steps num só commit.
4. Não saltar steps.

### Disciplina arquitetural

5. **`:core` não pode depender de Android Framework UI** (sem `Activity`, `View`, `Compose`, `LiveData` específico de Android UI). Pode usar Room (Android), Retrofit, Coroutines, e `androidx.lifecycle` para `ViewModel` se justificado — mas a tendência é manter UI logic fora do `:core`.
6. **`:app-xml` e `:app-compose` não podem ter código duplicado de data/repository/api**. Se o agente detetar duplicação, sinaliza e pergunta antes de proceder.
7. **ViewModels podem viver em cada módulo de UI**, porque o seu state shape é diferente entre LiveData (XML) e StateFlow (Compose). O agente não deve forçar um único ViewModel partilhado.
8. **Os imports de packages em `:app-xml` mudam todos** — o que era `dam_a46104.catsndogs.data.model.ImageItem` passa a ser `dam_a46104.catsndogs.core.model.ImageItem` (ou similar, conforme `01_architecture.md`).

### Estilo de código

9. Kotlin idiomático (extension functions, scope functions, `when` exaustivo, sealed classes).
10. Usa `Flow`/`StateFlow` no `:core` em vez de `LiveData` quando estiver a expor streams reativos do Repository — assim ambas as UIs consomem o mesmo tipo. Conversão para LiveData faz-se no ViewModel do `:app-xml` via `.asLiveData()`.
11. Comentários em português. Strings de UI sempre em `strings.xml` (PT + EN).
12. Sem hardcoded strings na UI — nem em XML, nem em Compose.

### Compose-specific

13. Material 3 (`androidx.compose.material3`).
14. ViewModels Compose obtidos via `hiltViewModel()` se houver Hilt, caso contrário via `viewModel()` da `androidx.lifecycle:lifecycle-viewmodel-compose`.
15. Navegação entre Main e Details via **Navigation Compose** (`androidx.navigation:navigation-compose`).
16. Animações são a **feature exclusiva do `:app-compose`** — devem estar implementadas e visíveis (ver `04_implementation_plan.md` M5).

### Build e versões

17. Kotlin DSL (`build.gradle.kts`) em todos os módulos.
18. Centralizar versões em `gradle/libs.versions.toml` (version catalog) — se ainda não existir, criar no início do M2.
19. Min SDK 24, Target SDK alinhado com o do MIP-2.
20. **AGP 9.x — aplicação de plugins em submodules:** plugins já no
    classpath via `pluginManagement` (como `com.android.library`,
    `com.android.application`) devem ser aplicados via
    `id("com.android.library")` direto, **não** via `alias(...)` com
    `version.ref`. Usar `alias()` para estes provoca o erro
    "plugin already on classpath with unknown version".

    Aplica-se a: `:core`, `:app-xml`, `:app-compose`.
    Confirmado durante M2.1.

### Comportamento em caso de dúvida

20. Se o agente encontrar uma situação não coberta pela documentação, **pergunta primeiro, gera depois**.
21. Se detetar conflito entre dois ficheiros de docs (ex: arquitetura vs implementation plan), sinaliza o conflito explicitamente e pede para resolver.
22. Se um step do plano falhar (build error, runtime crash), seguir o protocolo do MIP-2:
    `Identify problem → Question why → Analyse solutions → Pick best → Update plan → Commit → Fix → Commit → Test`

### Logging de prompts

23. Após cada step concluído, lembra-me de adicionar uma entrada em `docs/MIP3/prompts_log.md` (mesmo formato do MIP-2).

---

## O que NÃO fazer

- Não usar Hilt/Dagger se não estava na app original — manter dependency injection manual via `companion object getInstance()` como já existia.
- Não introduzir bibliotecas novas sem confirmar comigo (exceção: bibliotecas Compose oficiais necessárias para o módulo `:app-compose`).
- Não tentar fazer M2 + M3 + M4 num único batch. Cada um é validado e committed separadamente.
- Não eliminar `docs/` da raiz (a documentação MIP-2 mantém-se como histórico). A documentação MIP-3 vive em `docs/MIP3/`.
