# 04 — Implementation Plan (MIP-3)

Plano sequencial de implementação. Cada step é idealmente **um prompt ao AntiGravity** e **um commit Git**. Não saltar steps. Não combinar steps no mesmo commit.

Pré-requisito: **M1 já feito** (repo `CatsNDogs-MIP3` criado como cópia limpa do MIP-2, com a documentação MIP-3 em `docs/MIP3/`, `agents.md` substituído na raiz, e `CONTEXT.md` novo na raiz).

---

## Notas técnicas globais (a respeitar em todos os steps)

- **Version catalog já existe** (`gradle/libs.versions.toml`, herdado do MIP-2). **Não recriar.** Adicionar entradas conforme necessário.
- **AGP 9.x com built-in Kotlin:** o plugin `kotlin("android")` é aplicado automaticamente. **Não declarar explicitamente** em `plugins { }` dos novos módulos. **Não usar** `kotlinOptions { }` — provoca erro `Unresolved reference 'kotlinOptions'` (já apanhado no MIP-2, ver `prompts_log.md` Prompt 7 do MIP-2).
- **Versões fixadas (manter):** Room 2.7.0 + Room Gradle Plugin, KSP 2.3.6, Min SDK 24.

---

## Milestone M2 — Extração do `:core`

Objetivo: criar módulo Gradle `:core` com toda a data layer + business logic.

### Step M2.1 — Criar módulo `:core`

**Tarefas:**
- New Module → Android Library
- Nome do módulo: `core`
- Package: `dam_a46104.catsndogs.core`
- Min SDK: 24
- Configurar `core/build.gradle.kts`:
  - Plugins: `id("com.android.library")`, `id("androidx.room")`, `id("com.google.devtools.ksp")` — **não** declarar `kotlin("android")` (built-in pelo AGP 9.x)
  - **Não** incluir bloco `kotlinOptions { }`
  - Dependências (via version catalog): Retrofit, Gson converter, Coroutines, Room runtime, Room ktx, Room compiler (KSP), Lifecycle livedata-ktx (para `.asLiveData()`)
- Adicionar `include(":core")` ao `settings.gradle.kts`
- `./gradlew :core:build` deve passar (módulo vazio, mas configurado)

**Commit:** `chore(core): create :core android library module`

---

### Step M2.2 — Mover modelos e DTOs

**Ficheiros a mover:**
- `app/src/main/java/dam_a46104/catsndogs/data/model/ImageItem.kt`
  → `core/src/main/java/dam_a46104/catsndogs/core/model/ImageItem.kt`
- `app/src/main/java/dam_a46104/catsndogs/data/model/DogApiResponse.kt`
  → `core/src/main/java/dam_a46104/catsndogs/core/model/DogApiResponse.kt`

**Tarefas:**
- Mover ficheiros físicamente
- Atualizar `package dam_a46104.catsndogs.core.model`
- App original vai falhar build aqui — **é esperado**, será resolvido no M3

**Commit:** `refactor(core): move domain models to :core`

---

### Step M2.3 — Mover camada remota

**Ficheiros:**
- `data/remote/DogApiService.kt` → `core/remote/`
- `data/remote/RetrofitClient.kt` → `core/remote/`

**Tarefas:** mover + atualizar package para `dam_a46104.catsndogs.core.remote`

**Commit:** `refactor(core): move Retrofit client and service to :core`

---

### Step M2.4 — Mover camada local (Room)

**Ficheiros (5):**
- `data/local/CachedImage.kt` → `core/local/`
- `data/local/FavoriteEntry.kt` → `core/local/`
- `data/local/CacheDao.kt` → `core/local/`
- `data/local/FavoriteDao.kt` → `core/local/`
- `data/local/AppDatabase.kt` → `core/local/`

**Tarefas:** mover + atualizar package para `dam_a46104.catsndogs.core.local`. Garantir que o Room Gradle Plugin está configurado no `:core` (output do schema, etc.).

**Commit:** `refactor(core): move Room database, entities and DAOs to :core`

---

### Step M2.5 — Mover `UiState` e converter para `@StringRes`

**Ficheiro:** `app/src/main/java/.../ui/common/UiState.kt` → `core/common/UiState.kt`

**Mudança ao código:**
```kotlin
// Antes (MIP-2):
data class Error(val message: String) : UiState<Nothing>()

// Depois (MIP-3):
data class Error(@StringRes val messageResId: Int) : UiState<Nothing>()
```

**Atenção:** `UiState.Success<T>` no MIP-2 ganhou um campo `isFromCache: Boolean = false` na Extensão E. Manter esse campo.

**Commit:** `refactor(core): move UiState to :core and use StringRes for errors`

---

### Step M2.6 — Mover strings de erro para resources do `:core`

**Ficheiros:**
- Criar `core/src/main/res/values/strings.xml` com:
  - `error_no_network`
  - `error_server`
  - `error_unknown`
  - `info_offline_cache` (da Extensão E)
- Criar `core/src/main/res/values-pt/strings.xml` com traduções
- Apagar estas strings de `app/src/main/res/values/strings.xml` e `values-pt/strings.xml`

**Verificação:** resource merging do Gradle expõe-as automaticamente a quem depender de `:core`.

**Commit:** `refactor(core): move error and offline strings to :core resources`

---

### Step M2.7 — Mover Repository e converter `LiveData` para `Flow`

**Ficheiro:** `data/repository/ImageRepository.kt` → `core/repository/`

**Mudanças ao código:**
1. `getFavorites()` agora devolve `Flow<List<ImageItem>>` (era `LiveData`)
2. `isFavorite(id)` agora devolve `Flow<Boolean>` (era `LiveData`)
3. `fetchRandomImages()` devolve `UiState<List<ImageItem>>` em vez de `Pair<List<ImageItem>, Boolean>` + lançar exceção
   - Em sucesso: persiste em cache, devolve `UiState.Success(list, isFromCache = false)`
   - Em `IOException`: tenta cache. Se vazia, devolve `UiState.Error(R.string.error_no_network)`. Se não vazia, devolve `UiState.Success(cached, isFromCache = true)`.
   - Em `HttpException`: devolve `UiState.Error(R.string.error_server)`
   - Em outras exceções: devolve `UiState.Error(R.string.error_unknown)`
4. `findById()` mantém-se `suspend` com lookup em 3 camadas (memória → cache → favoritos) — sem alterações

**Atenção:** os DAOs do Room devem expor `Flow` em vez de `LiveData`. Verificar `CacheDao` e `FavoriteDao` — se ainda devolvem `LiveData`, alterar para `Flow`. (Note: este passo cruza com M2.4; pode haver pequenas correções aqui.)

**Commit:** `refactor(core): move repository to :core, expose Flow and UiState`

---

### Step M2.8 — Verificação `:core` standalone

**Tarefas:**
- `./gradlew :core:build` deve ter sucesso
- Verificar via `./gradlew :core:dependencies` que `:core` não depende do módulo `:app`
- `./gradlew :app:assembleDebug` **vai falhar** — esperado, será resolvido no M3

**Sem commit de código** — apenas validação. Atualizar `CONTEXT.md`:

**Commit:** `docs: update CONTEXT.md after M2 completion`

---

## Milestone M3 — Refactor `:app-xml`

Objetivo: app original consome `:core`, sem código duplicado.

### Step M3.1 — Renomear módulo `app` para `app-xml`

**Tarefas:**
- `settings.gradle.kts`:
  ```kotlin
  include(":app-xml")
  project(":app-xml").projectDir = file("app-xml")
  ```
- Renomear pasta `app/` → `app-xml/` (no filesystem)
- Reabrir projeto no Android Studio para sincronizar

**Commit:** `chore: rename :app module to :app-xml`

---

### Step M3.2 — Adicionar dependência ao `:core` e limpar dependências duplicadas

**Ficheiro:** `app-xml/build.gradle.kts`

**Tarefas:**
- Adicionar `implementation(project(":core"))`
- **Remover** dependências que agora vêm transitivamente via `:core`:
  - Retrofit
  - Gson converter
  - Room runtime, ktx, compiler
  - Lifecycle livedata-ktx (já vem do `:core`)
- **Manter** dependências de UI: Glide, Material Components, RecyclerView, Activity, Coroutines (se usado diretamente)
- Confirmar que **não** há plugins `kotlin("android")` ou `kotlinOptions { }` (AGP 9.x já trata)
- Confirmar que `applicationId` permanece consistente (recomendado: alterar para `dam_a46104.catsndogs.xml` para coexistir com `:app-compose`)

**Commit:** `chore(app-xml): depend on :core, remove duplicated dependencies`

---

### Step M3.3 — Apagar ficheiros migrados

**Tarefas:**
- Apagar pastas inteiras (já vivem no `:core`):
  - `app-xml/src/main/java/dam_a46104/catsndogs/data/`
  - `app-xml/src/main/java/dam_a46104/catsndogs/ui/common/UiState.kt` (apenas o ficheiro; manter `FavoritesBarController.kt` se ainda lá estiver)
- Build vai falhar com imports partidos — esperado, é resolvido no M3.4

**Commit:** `refactor(app-xml): remove files migrated to :core`

---

### Step M3.4 — Atualizar imports e adaptar ViewModels

**Ficheiros afetados (todos a modificar):**
- `MainActivity.kt`
- `ImageAdapter.kt`
- `ImageDetailsActivity.kt`
- `FavoritesBarController.kt` (manter onde está, ou mover para `ui/favorites/` agora se preferires)
- `MainViewModel.kt`
- `DetailsViewModel.kt`
- `CatsNDogsApp.kt`

**Refactor em massa de imports:**
- `dam_a46104.catsndogs.data.model.*` → `dam_a46104.catsndogs.core.model.*`
- `dam_a46104.catsndogs.data.remote.*` → `dam_a46104.catsndogs.core.remote.*`
- `dam_a46104.catsndogs.data.local.*` → `dam_a46104.catsndogs.core.local.*`
- `dam_a46104.catsndogs.data.repository.*` → `dam_a46104.catsndogs.core.repository.*`
- `dam_a46104.catsndogs.ui.common.UiState` → `dam_a46104.catsndogs.core.common.UiState`

**Adaptação dos ViewModels (`Flow` → `LiveData`):**
```kotlin
// MainViewModel
val favorites: LiveData<List<ImageItem>> = repo.getFavorites().asLiveData()

// DetailsViewModel
val isFavorite: LiveData<Boolean> = repo.isFavorite(id).asLiveData()
```

**Adaptação do ramo de erro nas Activities:**
```kotlin
// Antes (MIP-2):
Snackbar.make(view, error.message, ...)

// Depois (MIP-3):
Snackbar.make(view, getString(error.messageResId), ...)
```

**Adaptação do `UiState.Success` (já tinha `isFromCache`):** sem mudanças, apenas confirmar que continua a funcionar.

**Commit:** `refactor(app-xml): update imports and adapt to Flow-based core API`

---

### Step M3.5 — Build e smoke test

**Tarefas:**
- `./gradlew :app-xml:assembleDebug`
- Deploy no emulador
- Testes manuais (paridade total com MIP-2):
  - Lista carrega com WiFi
  - Refresh funciona
  - Tocar numa imagem abre Details
  - Toggle favorito atualiza estrela e FavoritesBar
  - 6.º favorito remove o mais antigo
  - WiFi off + cache populado: mostra cache + Snackbar offline
  - WiFi off + cache vazio: mostra erro com Retry

**Sem commit de código** se tudo funcionar. Se houver bug, criar M3.5.1, M3.5.2, etc., para correções. Atualizar `CONTEXT.md`:

**Commit:** `docs: update CONTEXT.md after M3 completion`

---

## Milestone M4 — Novo módulo `:app-compose`

### Step M4.1 — Criar módulo `:app-compose`

**Tarefas:**
- New Module → Android Application (Empty Compose Activity)
- Nome: `app-compose`
- Package: `dam_a46104.catsndogs.compose`
- ApplicationId: `dam_a46104.catsndogs.compose`
- `include(":app-compose")` ao `settings.gradle.kts`

**Commit:** `chore: create :app-compose module`

---

### Step M4.2 — Configurar dependências

**Ficheiro:** `app-compose/build.gradle.kts`

**Adicionar:**
- `implementation(project(":core"))`
- Compose BOM (`androidx.compose:compose-bom`)
- `androidx.compose.material3:material3`
- `androidx.compose.ui:ui` + `ui-tooling-preview`
- `debugImplementation(...ui-tooling)`
- `androidx.activity:activity-compose`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `androidx.lifecycle:lifecycle-runtime-compose`
- `androidx.navigation:navigation-compose`
- `io.coil-kt:coil-compose` (image loading idiomático em Compose)

**Atenção AGP 9.x:** mesmas regras — não declarar `kotlin("android")`, não usar `kotlinOptions { }`. Para Compose, configurar `buildFeatures { compose = true }` e Compose Compiler.

**Commit:** `chore(app-compose): add Compose, Navigation and Coil dependencies`

---

### Step M4.3 — Application class

**Ficheiro:** `app-compose/src/main/java/.../CatsNDogsApp.kt`

**Conteúdo:** mesmo padrão do `:app-xml`, constrói Repository via lazy.

```kotlin
class CatsNDogsApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val apiService by lazy { RetrofitClient.dogApiService }

    val imageRepository by lazy {
        ImageRepository.getInstance(
            apiService = apiService,
            cacheDao = database.cacheDao(),
            favoriteDao = database.favoriteDao()
        )
    }
}
```

**Manifest:** registar `android:name=".CatsNDogsApp"`.

**Commit:** `feat(app-compose): add Application class with Repository setup`

---

### Step M4.4 — Theme setup

**Ficheiros:**
- `ui/theme/Color.kt` — paleta da app (pode reutilizar cores do `:app-xml` ou criar nova)
- `ui/theme/Type.kt` — typography Material 3
- `ui/theme/Theme.kt` — `CatsNDogsTheme { content }` composable wrapper

**Commit:** `feat(app-compose): add Material 3 theme`

---

### Step M4.5 — `MainViewModel` com StateFlow

**Ficheiro:** `viewmodel/MainViewModel.kt`

```kotlin
class MainViewModel(private val repo: ImageRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<ImageItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ImageItem>>> = _uiState.asStateFlow()

    val favorites: StateFlow<List<ImageItem>> = repo.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun loadImages(count: Int = 20) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repo.fetchRandomImages(count)
        }
    }
}
```

Factory para injetar o Repository (mesma estratégia do MIP-2).

**Commit:** `feat(app-compose): add MainViewModel with StateFlow`

---

### Step M4.6 — `MainScreen` composable

**Ficheiros:**
- `ui/main/MainScreen.kt`
- `ui/main/ImageCard.kt`

**Componentes:**
- `Scaffold` com `TopAppBar` + `FloatingActionButton`
- `LazyColumn` com items
- `CircularProgressIndicator` quando `Loading`
- `Snackbar` (via `SnackbarHostState`) quando `Error` com action "Retry"
- Snackbar informativa quando `Success(isFromCache = true)`
- Click no item → callback para navegação (passa `imageId`)

**Sem animações ainda** — vêm em M5.

**Commit:** `feat(app-compose): add MainScreen and ImageCard composables`

---

### Step M4.7 — `DetailsViewModel` e `DetailsScreen`

**Ficheiros:**
- `viewmodel/DetailsViewModel.kt`
- `ui/details/DetailsScreen.kt`

**Funcionalidades:**
- `DetailsViewModel` recebe `imageId: String`, expõe:
  - `image: StateFlow<ImageItem?>` (resolvido via `repo.findById(id)`)
  - `isFavorite: StateFlow<Boolean>` (via `repo.isFavorite(id).stateIn(...)`)
  - método `toggleFavorite()`
- `DetailsScreen`:
  - `Scaffold` com `TopAppBar` (back arrow)
  - Coil `AsyncImage` em tamanho grande
  - Painel de metadados (raça, subraça, ID, URL)
  - Botão de favorito (toggle estrela vazia/cheia, observa `isFavorite`)

**Commit:** `feat(app-compose): add DetailsScreen and DetailsViewModel`

---

### Step M4.8 — `FavoritesBar` composable

**Ficheiro:** `ui/favorites/FavoritesBar.kt`

**Componentes:**
- `LazyRow` com até 5 miniaturas circulares (`Modifier.clip(CircleShape)`)
- Coil `AsyncImage` para cada miniatura
- Click → callback `(ImageItem) -> Unit` para navegação

**Integração:** incluído em `MainScreen` e `DetailsScreen` (passar `favorites: List<ImageItem>` e `onItemClick`).

**Commit:** `feat(app-compose): add FavoritesBar composable`

---

### Step M4.9 — Navigation

**Ficheiro:** `navigation/AppNavigation.kt`

**Rotas:**
- `main` → `MainScreen`
- `details/{imageId}` → `DetailsScreen` (com argumento)

**Ficheiro:** `MainActivity.kt`

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatsNDogsTheme {
                AppNavigation()
            }
        }
    }
}
```

**Commit:** `feat(app-compose): add Navigation Compose with Main and Details routes`

---

### Step M4.10 — Strings PT/EN

**Ficheiros:**
- `app-compose/src/main/res/values/strings.xml`
- `app-compose/src/main/res/values-pt/strings.xml`

**Conteúdo:** strings específicas do `:app-compose` (ex: `app_name`, labels). Strings de erro vêm do `:core` automaticamente via resource merging.

**Commit:** `feat(app-compose): add multilingual strings (PT/EN)`

---

### Step M4.11 — Build e smoke test

**Tarefas:**
- `./gradlew :app-compose:installDebug`
- Verificar paridade com `:app-xml`: lista, refresh, details, favoritos, offline, error handling
- Confirmar que ambas as apps coexistem no emulador (ApplicationIds diferentes)

**Sem commit** se tudo OK. Atualizar `CONTEXT.md`:

**Commit:** `docs: update CONTEXT.md after M4 completion`

---

## Milestone M5 — Feature exclusiva: Animações

### Step M5.1 — Animar `LoadingIndicator`

**Ficheiro:** `ui/common/LoadingIndicator.kt`

```kotlin
@Composable
fun LoadingIndicator(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut(targetScale = 0.8f)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
```

Substituir o `CircularProgressIndicator` simples no `MainScreen` por este `LoadingIndicator`.

**Commit:** `feat(app-compose): animate loading indicator with AnimatedVisibility`

---

### Step M5.2 — Animar mudanças na lista

**Ficheiro:** `ui/main/MainScreen.kt`

**Mudanças:**
- `LazyColumn(modifier = Modifier.animateContentSize())` no container (suaviza mudanças de altura)
- Cada `ImageCard` com `Modifier.animateItemPlacement()` (no scope do `LazyListScope`)

**Commit:** `feat(app-compose): animate list content size and item placement`

---

### Step M5.3 — Animar toggle do favorito

**Ficheiro:** `ui/details/DetailsScreen.kt`

**Mudança no botão de favorito:**
```kotlin
AnimatedContent(
    targetState = isFavorite,
    transitionSpec = {
        (scaleIn(initialScale = 0.5f) + fadeIn()) togetherWith
        (scaleOut(targetScale = 0.5f) + fadeOut())
    },
    label = "favorite_toggle"
) { fav ->
    Icon(
        imageVector = if (fav) Icons.Filled.Star else Icons.Outlined.StarBorder,
        contentDescription = null
    )
}
```

**Commit:** `feat(app-compose): animate favorite toggle with AnimatedContent`

Atualizar `CONTEXT.md`:

**Commit:** `docs: update CONTEXT.md after M5 completion`

---

## Milestone M6 — Entregáveis finais

### Step M6.1 — README global

**Ficheiro:** `README.md` (raiz)

**Conteúdo:**
- Descrição do projeto MIP-3
- Diagrama de módulos (embed Mermaid de `docs/MIP3/00_module_diagram.md`)
- Como correr cada app:
  - `./gradlew :app-xml:installDebug`
  - `./gradlew :app-compose:installDebug`
- Stack técnico (Kotlin, Retrofit, Room, Compose, etc.)
- Screenshots lado-a-lado XML vs Compose
- Link para `docs/MIP3/` para detalhes da arquitetura

**Commit:** `docs: add README with multi-module overview`

---

### Step M6.2 — Screenshots e finalização

**Tarefas:**
- Capturar screenshots:
  - MainScreen XML vs MainScreen Compose
  - DetailsScreen XML vs DetailsScreen Compose
  - FavoritesBar em ação (com 5 favoritos)
  - Animações Compose (gif curto, opcional)
- Guardar em `docs/MIP3/screenshots/`
- Atualizar `README.md` com referências

**Commit:** `docs: add comparison screenshots between :app-xml and :app-compose`

---

### Step M6.3 — Lint e cleanup

**Tarefas:**
- `./gradlew lint`
- Remover TODOs deixados nos ficheiros
- Remover imports não usados
- Confirmar que ambas as apps correm e passam testes manuais

**Commit:** `chore: final cleanup and lint fixes`

---

## Checkpoint final

Antes da entrega:

- [ ] `:core` compila standalone (`./gradlew :core:build`)
- [ ] `:app-xml` instala e funciona com paridade ao MIP-2
- [ ] `:app-compose` instala e tem paridade funcional com `:app-xml`
- [ ] Animações são visíveis no `:app-compose` (Loading, lista, favorito)
- [ ] README explica a arquitetura e mostra screenshots
- [ ] `prompts_log.md` (em `docs/MIP3/`) tem entrada por step
- [ ] `CONTEXT.md` (raiz) reflete o estado final
- [ ] Sem código duplicado entre `:app-xml` e `:app-compose`
- [ ] Diagrama de módulos no README
- [ ] Histórico Git limpo (commits atómicos, mensagens claras)

---

## Logging de prompts

Manter `docs/MIP3/prompts_log.md` (mesmo formato do MIP-2). Uma entrada por step, com: objetivo, prompt usado, resultado, notas.

Adicionalmente, **atualizar o `CONTEXT.md` da raiz a cada milestone fechado** (ver instruções no próprio `CONTEXT.md`).
