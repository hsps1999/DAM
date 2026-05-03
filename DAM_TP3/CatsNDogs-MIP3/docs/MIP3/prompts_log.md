# Prompts Log — MIP-3

Registo dos prompts principais usados com o AntiGravity IDE durante a fase MIP-3 do projeto CatsNDogs. Atualizado após cada interação significativa com o agente.

Formato de cada entrada:

```
## Prompt N — <título curto>

**Step do plano:** <referência a 04_implementation_plan.md, ex: M2.1>
**Data:** YYYY-MM-DD

**Objetivo:**
<o que se queria obter>

**Prompt usado:**
<copiar prompt literal>

**Resultado:**
<descrição breve do código gerado e se foi aceite tal qual ou ajustado>

**Notas / problemas:**
<opcional — bugs encontrados, decisões tomadas>
```

---

## Prompt 1 — Bootstrap e validação do contexto MIP-3

**Step do plano:** 0 (bootstrap, antes do M2.1)
**Data:** 2026-05-03

**Objetivo:**
Garantir que o agente lê toda a documentação MIP-3 (agents.md + CONTEXT.md + docs/MIP3/) e demonstra ter compreendido as regras, a arquitetura multi-módulo e o primeiro step antes de gerar qualquer código.

**Prompt usado:**
Antes de começarmos a implementar o MIP-3, preciso que faças o seguinte por esta ordem:

1. Lê o ficheiro `agents.md` na raiz do projeto.
2. Lê o ficheiro `CONTEXT.md` na raiz do projeto.
3. Lê todos os ficheiros em `docs/MIP3/` (são 6: 00, 01, 02, 03, 04, e prompts_log).
4. Resume-me em 8-12 linhas:
   - O que é este projeto (MIP-3) e como se distingue do MIP-2
   - Qual é a arquitetura alvo (3 módulos, dependências)
   - Que tipo de feature exclusiva tem o `:app-compose`
   - As 3-5 regras mais importantes a que estás vinculado pelo `agents.md`
   - Qual é o Step M2.1 do plano de implementação (o primeiro)
   - Que decisões técnicas globais devo respeitar (versões fixadas, AGP 9.x)

Não geres código nesta resposta. Não toques em ficheiros.
Só quero confirmar que leste e percebeste tudo antes de avançarmos.
Lê literalmente os ficheiros, não inventes conteúdo.

**Resultado:**
O agente fez ligação explícita ao Prompt 7 do MIP-2 — confirma que tem
visibilidade do histórico anterior. Útil para detetar regressões conhecidas.

---

## Prompt 2 — M2.1: Criar módulo :core

**Step do plano:** M2.1
**Data:** 2026-05-03

**Objetivo:**
Criar módulo Gradle :core (Android Library) com build.gradle.kts, AndroidManifest mínimo, e registo em settings.gradle.kts. Validar build standalone.

**Prompt usado:**
Antes de gerar qualquer alteração, confirma:

1. Que ficheiros vais criar ou alterar
2. Conteúdo exato do `core/build.gradle.kts`:
   - Que plugins vais incluir (e quais NÃO vais incluir, à luz da regra AGP 9.x)
   - Que dependências vais declarar e que aliases do version catalog vais usar
   - Se vais adicionar entradas novas ao `gradle/libs.versions.toml` (e quais)
3. Que alteração vais fazer ao `settings.gradle.kts`
4. Como vais validar que o módulo compila standalone

Espera pela minha confirmação antes de aplicar mudanças.

**Resultado:**
android-library adicionado ao libs.versions.toml. core/build.gradle.kts criado com namespace, compileSdk, minSdk 24, schemaDirectory, dependências Retrofit/Room/Coroutines/Lifecycle. AndroidManifest.xml mínimo. settings.gradle.kts atualizado. Build OK (60 tasks).

**Notas / problemas:**
Bug AGP 9.x descoberto: alias() em submodules para plugins do classpath falha com "plugin already on classpath with unknown version". Solução: id("com.android.library") direto sem alias(). Regra adicionada ao agents.md para os próximos módulos (:app-xml, :app-compose).

---

## Prompt 3 — M2.2: Mover modelos e DTOs

**Step do plano:** M2.2
**Data:** 2026-05-03

**Objetivo:**
Mover ImageItem.kt e DogApiResponse.kt de :app para :core, atualizando package declaration.

**Prompt usado:**
Vamos avançar para o Step M2.2 do `docs/MIP3/04_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:

1. Que ficheiros vais mover e para onde (paths exatos)
2. Que mudanças vais fazer ao package declaration de cada ficheiro
3. Que efeitos colaterais esperas (build do :app vai falhar com imports
   partidos? confirmas que isso é esperado e não bloqueia o commit?)
4. Vais tocar em algum ficheiro além dos 2 .kt a mover?

Espera pela minha confirmação antes de aplicar mudanças.

**Resultado:**
Ambos os ficheiros movidos com package core.model. Git detectou rename (95% e 89% similaridade). :core:build OK. :app:assembleDebug falha em 4 ficheiros (esperado — resolvido em M3).

**Notas / problemas:**
Nenhum.

---

## Prompt 4 — M2.3: Mover camada remota

**Step do plano:** M2.3
**Data:** 2026-05-03

**Objetivo:**
Mover DogApiService.kt e RetrofitClient.kt para :core/remote/, atualizando package e imports.

**Prompt usado:**
Vamos avançar para o Step M2.3 do `docs/MIP3/04_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:

1. Que ficheiros vais mover e paths exatos
2. Mudanças ao package declaration
3. Mudanças aos imports dentro dos ficheiros (RetrofitClient deve
   importar DogApiService — confirma se o import precisa de ajuste)
4. Outros ficheiros tocados

Espera pela minha confirmação antes de aplicar.

Após aplicar:
1. `./gradlew :core:build` verde
2. `:app:assembleDebug` continua a falhar (esperado, mais imports partidos)
3. Commit: `refactor(core): move Retrofit client and service to :core`

**Resultado:**
Ambos movidos. DogApiService com import DogApiResponse atualizado para core.model. RetrofitClient sem imports de projeto. :core:build OK. :app:assembleDebug falha em mais ficheiros (esperado).

**Notas / problemas:**
Nenhum.

---

## Prompt 5 — M2.4: Mover camada local Room

**Step do plano:** M2.4
**Data:** 2026-05-03

**Objetivo:**
Mover 5 ficheiros Room (entities, DAOs, AppDatabase) para :core/local/ com package e imports atualizados.

**Prompt usado:**
Vamos avançar para o Step M2.4 do `docs/MIP3/04_implementation_plan.md`.

Este step move 5 ficheiros interligados (entities, DAOs, AppDatabase).
Antes de aplicar, confirma:

1. Os 5 ficheiros e paths exatos (origem → destino)
2. Mudanças ao package declaration de cada um
3. **Mapa de imports cruzados** entre os 5 ficheiros:
   - CacheDao usa CachedImage?
   - FavoriteDao usa FavoriteEntry?
   - AppDatabase usa todas as entities + DAOs?
   - Lista cada import que precisa de ser atualizado e onde
4. Confirmação que o `schemaDirectory` configurado no M2.1 cobre os
   schemas que vão ser gerados aqui
5. Se algum ficheiro tem imports do `model/` (ImageItem) que precisem
   de update para core.model
6. Outros ficheiros tocados

Espera pela minha confirmação antes de aplicar.

Após aplicar:
1. `./gradlew :core:build` verde — Room deve gerar código sem erros
2. `:app:assembleDebug` continua a falhar (esperado)
3. Commit: `refactor(core): move Room database, entities and DAOs to :core`

**Resultado:**
5 ficheiros movidos. Imports core.model.ImageItem atualizados em CachedImage e FavoriteEntry. KSP processou sem erros. FavoriteDao mantém LiveData (conversão reservada para M2.7). :core:build OK.

**Notas / problemas:**
copyRoomSchemas NO-SOURCE (esperado — exportSchema=false).

---

## Prompt 6 — M2.5: Mover UiState e converter para @StringRes

**Step do plano:** M2.5
**Data:** 2026-05-03

**Objetivo:**
Mover UiState.kt para :core/common/ e alterar UiState.Error para receber @StringRes em vez de String.

**Prompt usado:**
Vamos avançar para o Step M2.5 do `docs/MIP3/04_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:

1. Ficheiro a mover: `app/src/main/java/.../ui/common/UiState.kt` → `core/src/main/java/.../core/common/UiState.kt`
2. Mudança de package: `ui.common` → `core.common`
3. Mudança de assinatura:
   - `data class Error(val message: String)` → `data class Error(@StringRes val messageResId: Int)`
   - `Success` mantém-se igual
4. Se `Success` tem campos extras (como `isFromCache` da Extensão E) — confirma quais e mantém
5. Outros ficheiros tocados

Espera pela minha confirmação antes de aplicar.

Após aplicar:
1. `./gradlew :core:build` verde
2. `:app:assembleDebug` continua a falhar (esperado)
3. Commit: `refactor(core): move UiState to :core and use StringRes for errors`

**Resultado:**
Ficheiro movido e editado conforme especificado. isFromCache preservado no Success. :core:build OK. :app:assembleDebug falha (esperado).

**Notas / problemas:**
Nenhum.

---

## Prompt 7 — M2.6: Mover strings de erro para resources do :core

**Step do plano:** M2.6
**Data:** 2026-05-03

**Objetivo:**
Criar `core/src/main/res/values/strings.xml` e `core/src/main/res/values-pt/strings.xml` com as strings de erro e offline, e remover estas strings do :app.

**Prompt usado:**
Vamos avançar para o Step M2.6 do `docs/MIP3/04_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:

1. Que recursos vais criar no :core:
   - `core/src/main/res/values/strings.xml`
   - `core/src/main/res/values-pt/strings.xml`
2. Conteúdo exato de cada um:
   - error_no_network
   - error_server
   - error_unknown
   - info_offline_cache
   - traduções em -pt
3. Que ficheiros vais editar ou remover no :app (as strings a apagar)
4. Confirmação que o resource merging vai expor estas strings a quem
   depender de :core (via R.string.xxx)

Espera pela minha confirmação antes de aplicar.

Após aplicar:
1. `./gradlew :core:build` verde
2. `:app:assembleDebug` continua a falhar (esperado)
3. Commit: `refactor(core): move error and offline strings to :core resources`

**Resultado:**
Recursos criados no :core com traduções. Strings apagadas do :app. :core:build OK. :app falha (esperado).

**Notas / problemas:**
Nenhum.

---

## Prompt 8 — M2.7: Mover Repository e converter LiveData para Flow

**Step do plano:** M2.7
**Data:** 2026-05-03

**Objetivo:**
Mover ImageRepository.kt para :core/repository/ e converter todas as suas LiveData para Flow.

**Prompt usado:**
Vamos avançar para o Step M2.7 do `docs/MIP3/04_implementation_plan.md`.

Este step muda assinatura do Repository, converte LiveData→Flow nos DAOs
e mapeia exceções para UiState.Error com @StringRes. É o step mais
complexo do M2. Antes de aplicar, confirma em detalhe:

1. Ficheiros a mover/modificar (paths exatos)
2. FavoriteDao: que métodos devolvem LiveData hoje? Qual o plano de
   conversão para Flow? Há métodos que devem manter-se síncronos
   (não-Flow)?
3. CacheDao: alguma alteração necessária?
4. ImageRepository — assinaturas novas:
   - getFavorites(): Flow<List<ImageItem>> (era LiveData)
   - isFavorite(id): Flow<Boolean> (era LiveData)
   - fetchRandomImages(count): UiState<List<ImageItem>> (era
     Pair<List<ImageItem>, Boolean> + lançava exceção)
   - findById(id): mantém suspend, sem mudanças
   - toggleFavorite(item): mantém suspend, sem mudanças
   Confirma todas estas assinaturas e mostra-me o esqueleto do
   try/catch dentro de fetchRandomImages com o mapeamento:
   - sucesso → UiState.Success(list, isFromCache=false)
   - IOException + cache não vazia → UiState.Success(cached, isFromCache=true)
   - IOException + cache vazia → UiState.Error(R.string.error_no_network)
   - HttpException → UiState.Error(R.string.error_server)
   - outras → UiState.Error(R.string.error_unknown)
5. Confirma que :app vai ter mais imports partidos (esperado, M3 corrige)

Espera pela minha confirmação antes de aplicar.

**Resultado:**
FavoriteDao actualizado: getAll() e isFavorite() convertidos de LiveData para Flow (CacheDao não precisou de alterações). ImageRepository criado em :core/repository/ com: fetchRandomImages a devolver UiState em vez de lançar exceções; getFavorites() e isFavorite() a devolver Flow; import dam_a46104.catsndogs.core.R para R.string.*. :core:build BUILD SUCCESSFUL. :app:assembleDebug falha (esperado — imports partidos nos ViewModels e Activities, resolvidos no M3).

**Notas / problemas:**
Warning KT-73255 sobre @StringRes em data class mantém-se (não-blocking, registado desde M2.5). getCachedImages() foi tornada private no novo Repository (era pública no :app — não há usos externos agora que está em :core).

---

### Step M3.4 — Atualizar imports e adaptar ViewModels
**Prompt User:**
"Avança"
"Confirma que FavoritesBarController não precisa de adaptação além dos imports (ainda recebe LiveData do MainViewModel/DetailsViewModel)"

**Ações do Agente:**
- Modificou `FavoritesBarController.kt`, `ImageAdapter.kt` para usar `core.*`.
- Modificou `MainViewModel.kt` para remover bloco try/catch e usar `.asLiveData()`.
- Modificou `DetailsViewModel.kt` para usar `flatMapLatest` + `.asLiveData()`.
- Modificou `MainActivity.kt` e `ImageDetailsActivity.kt` para usar `state.messageResId`.
- Fix de string de offline cache em `MainActivity` para `core.R.string.info_offline_cache`.
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `refactor(app-xml): fix broken imports and adapt viewmodels to use Flow/UiState from :core`

---

## Milestone M4 — Novo módulo :app-compose

### Step M4.1 — Criar módulo :app-compose
**Prompt User:**
"Vamos avançar para o Step M4.1 do `docs/MIP3/04_implementation_plan.md`.
1. Como vais criar o módulo (New Module no Android Studio? Manual?)
2. Confirma: Nome, Package, ApplicationId, Min SDK, Template
3. Alterações ao settings.gradle.kts
4. Estrutura de pastas que vai ser criada
5. AGP 9.x: lembra-te que o plugin com.android.application aplica-se via id() direto, não via alias() — mesma regra do M2.1"
"avança"

**Ações do Agente:**
- Modificou `settings.gradle.kts` para adicionar `include(":app-compose")`.
- Criou estrutura manual de ficheiros: `build.gradle.kts`, `AndroidManifest.xml`, `CatsNDogsComposeApp.kt`, e `MainActivity.kt`.
- Omitiu plugins do Compose e de Kotlin que são geridos automaticamente ou no M4.2 para não partir a compilação base.
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `chore: create :app-compose module`

---

### Step M4.2 — Configurar dependências
**Prompt User:**
"Avança
 Confirma que NÃO declaras kotlin("android") nem kotlinOptions { }
   (mesma armadilha do M2.1)"

**Ações do Agente:**
- Modificou `gradle/libs.versions.toml` para adicionar as dependências do Compose (BOM 2024.10.00, material3, ui, tooling), navigation-compose (2.8.3), coil-compose (2.7.0) e o plugin `org.jetbrains.kotlin.plugin.compose`.
- Modificou `app-compose/build.gradle.kts`:
  - Adicionou `buildFeatures { compose = true }`.
  - Aplicou o plugin do Compose compiler via `alias(libs.plugins.kotlin.compose)`.
  - Adicionou as dependências todas mencionadas.
  - Omitiu `kotlinOptions` e o plugin `kotlin("android")` conforme indicado.
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `chore(app-compose): add Compose, Navigation and Coil dependencies`

---

### Step M4.3 — Application class
**Prompt User:**
"Vamos avançar para o Step M4.3.
Confirma que o ImageRepository é construído via lazy + singleton,
   tal como no :app-xml"

**Ações do Agente:**
- Modificou `CatsNDogsComposeApp.kt` introduzindo propriedades `by lazy` para `database`, `apiService` e `imageRepository`.
- Validou que `AndroidManifest.xml` já tinha a tag `android:name=".CatsNDogsComposeApp"` registada durante a sua criação no M4.1.
- Commit: `feat(app-compose): add Application class with Repository setup`

---

### Step M4.4 — Theme setup
**Prompt User:**
"Avança"

**Ações do Agente:**
- Criou a diretoria `ui/theme` em `app-compose`.
- Criou os ficheiros base do Material 3: `Color.kt` (paleta clara e escura baseada em roxo moderno), `Type.kt` (tipografia Default Material 3) e `Theme.kt` (suporte a Dynamic Color e alteração do statusBarColor).
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `feat(app-compose): add Material 3 theme`

---

### Step M4.5 — `MainViewModel` com StateFlow
**Prompt User:**
"Avança"

**Ações do Agente:**
- Criou a pasta `viewmodel` e o ficheiro `MainViewModel.kt`.
- Implementou o `MainViewModel` com `StateFlow` e `MutableStateFlow` para observar a UI state e os favoritos reativamente sem a dependência do LiveData.
- Incluiu a lógica para popular as propriedades utilizando `stateIn` da coroutine flow API, com `SharingStarted.WhileSubscribed(5_000)`.
- Incluiu o provider factory manual para o `ImageRepository`.
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `feat(app-compose): add MainViewModel with StateFlow`

---

### Step M4.6 — `MainScreen` composable
**Prompt User:**
"Step M4.6. Mostra-me MainScreen.kt e ImageCard.kt completos antes de aplicar:
1. Scaffold com TopAppBar e FloatingActionButton
2. LazyColumn com items
3. CircularProgressIndicator quando Loading
4. Snackbar com retry quando Error (resolve messageResId via stringResource)
5. Snackbar informativa quando Success(isFromCache=true)
6. Click no item → callback (passa imageId — navegação vem em M4.9)
7. SEM animações ainda (são M5)
Espera confirmação."
"Avança"

**Ações do Agente:**
- Criou a pasta `ui/main/` e os ficheiros `MainScreen.kt` e `ImageCard.kt`.
- No `ImageCard.kt`, implementou `Card` com `AsyncImage` (Coil Compose) e informações da raça da imagem, sem animações.
- No `MainScreen.kt`, geriu a `LazyColumn` a partir da subscrição de `uiState` utilizando `collectAsStateWithLifecycle()`.
- Lançou Side Effects `LaunchedEffect(state)` para a visualização das `Snackbars` quando a lista vem de cache (informativo) ou de erro (onde adicionou um `actionLabel = "Retry"` a invocar o `loadImages`).
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `feat(app-compose): add MainScreen and ImageCard composables`

---

### Step M4.7 — `DetailsScreen` e `DetailsViewModel`
**Prompt User:**
"Step M4.7. Mostra-me DetailsViewModel.kt e DetailsScreen.kt completos:
DetailsViewModel:
- recebe imageId via factory
- image: StateFlow<ImageItem?> via repo.findById()
- isFavorite: StateFlow<Boolean> via repo.isFavorite(id).stateIn(...)
- toggleFavorite()

DetailsScreen:
- Scaffold com TopAppBar (back arrow)
- AsyncImage do Coil em tamanho grande
- Painel metadata: raça, subraça, ID, URL
- Botão favorito (estrela vazia/cheia, observa isFavorite)
- SEM animação no toggle (vem em M5.3)"
"segue"

**Ações do Agente:**
- Criou `DetailsViewModel` que utiliza o `ImageRepository` para carregar a imagem e observar o seu estado de favorito usando `stateIn(WhileSubscribed)`.
- Criou a pasta `ui/details` e o ficheiro `DetailsScreen.kt`.
- Implementou o `Scaffold` com `TopAppBar`, contendo a ação de regressar (`onNavigateBack`) e o action button de favoritar.
- Adicionou a visualização do `AsyncImage` em grande destaque e o painel de propriedades do cão/gato (Scrollable column).
- Validou que o toggle respeita o `isFavorite` (estrela preenchida/vazia), sem animações.
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `feat(app-compose): add DetailsScreen and DetailsViewModel`

---

### Step M4.8 — Grelha de Favoritos na Home
**Prompt User:**
"segue"

**Ações do Agente:**
- Criou o `FavoritesBar.kt`, implementando uma `LazyRow` para iterar horizontalmente a lista de favoritos com thumbnails `AsyncImage`. O componente oculta-se automaticamente quando a lista de favoritos (`favorites.isEmpty()`) está vazia.
- Integrado na `MainScreen.kt`, recolhendo `favorites` do `viewModel.favorites.collectAsStateWithLifecycle()` e exibido através do escopo `item { FavoritesBar(...) }` no topo do `LazyColumn`.
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `feat(app-compose): add FavoritesBar to MainScreen`

---

### Step M4.9 — Navegação e MainActivity
**Prompt User:**
"segue"

**Ações do Agente:**
- No `MainActivity.kt`, inicializou o Theme (`CatsNDogsTheme`) e um `Surface`.
- Implementou o **Navigation Compose** usando um `NavHost` (`startDestination = "main"`) e `rememberNavController()`.
- Criou as duas rotas base:
  - `"main"`: Instancia o `MainViewModel` (injetando `app.imageRepository` via factory manual) e delega para o `MainScreen`, passando o callback de navegação para o ecrã de detalhes (`navController.navigate("details/$imageId")`).
  - `"details/{imageId}"`: Lê o ID dos argumentos, instancia o `DetailsViewModel` (via factory manual) e exibe o `DetailsScreen`, delegando a ação de retroceder para o `navController.popBackStack()`.
- Executou build: `BUILD SUCCESSFUL`.
- Commit: `feat(app-compose): implement Navigation Compose routing`

---

### Step M4.10 — Recursos de String
**Prompt User:**
"Sim"

**Ações do Agente:**
- Criou o diretório `app-compose/src/main/res/values/` e o ficheiro `strings.xml`.
- Mapeou todos os textos estáticos (`"Cats & Dogs"`, `"Details"`, `"Favorites"`, content descriptions e metadata labels) para strings devidamente formatadas.
- Substituiu as strings hard-coded nos ficheiros `MainScreen.kt`, `DetailsScreen.kt`, `FavoritesBar.kt` e `ImageCard.kt` através da chamada idiomática `stringResource(id = dam_a46104.catsndogs.compose.R.string...)`.
- Executou build: `BUILD FAILED` (vários problemas detetados).

**Ações Corretivas (Fixes para M4):**
- Copiados os diretórios `mipmap` e os respetivos `ic_launcher_*.xml` do `:app-xml` para o `:app-compose` de forma a suportar os ícones pedidos no Manifest.
- Adicionado um `themes.xml` (com base em `Theme.Material.Light.NoActionBar`) e referenciado no `AndroidManifest.xml` para resolver falta de atributos de tema.
- No `core/build.gradle.kts`, substituída a dependência de Room de `implementation` para `api(libs.room.runtime)` para garantir transitividade ao `:app-compose`.
- No `CatsNDogsComposeApp.kt`, corrigidos os argumentos do `ImageRepository.getInstance(...)` (não precisa de DAOs explícitos).
- No `DetailsScreen.kt` e `ImageCard.kt`, corrigidas referências inexistentes a `breeds`, `breedGroup` e `bredFor` porque a nossa API apenas suporta `breed` e `subBreed`. Smart casts resolvidos com a extração de variáveis locais.
- No `DetailsScreen.kt`, corrigido o ícone inexistente `StarBorder` para os standard `Icons.Filled.FavoriteBorder` e `Icons.Filled.Favorite`.
- Adicionadas permissões `INTERNET` e `ACCESS_NETWORK_STATE` ao `AndroidManifest.xml` (pois a app crashava silenciosamente ao tentar fazer chamadas de rede à Dog API).
- No `Theme.kt`, adicionada validação e *unwrap* seguro do `ContextWrapper` para extrair a `Activity` corretamente sem originar um `ClassCastException` quando usada em conjunto com o `NavHost`.
- Executou novo build: `BUILD SUCCESSFUL`.
- Commit: `fix(app-compose): resolve runtime crashes missing internet permission and theme cast`

### Step M5.1 Animar \LoadingIndicator\

**Objetivo:** Substituir o \CircularProgressIndicator\ simples por um \LoadingIndicator\ animado com \AnimatedVisibility\.

**Prompt:** \Step M5.1. Cria ui/common/LoadingIndicator.kt com AnimatedVisibility e substitui no MainScreen.\

**Ações do Agente:**
- Criou o ficheiro \app-compose/src/main/java/dam_a46104/catsndogs/compose/ui/common/LoadingIndicator.kt\.
- Implementou o composable com \AnimatedVisibility(enter = fadeIn() + scaleIn(0.8f), exit = fadeOut() + scaleOut(0.8f))\.
- Modificou o \MainScreen.kt\ para usar o \LoadingIndicator\ extraído do \when\ para que as animações de exit pudessem ocorrer corretamente.
- Commit: \feat(app-compose): animate loading indicator with AnimatedVisibility\


### Step M5.2 Animar mudanças na lista

**Objetivo:** Animar as transições de reordenação e re-sizing do LazyColumn na \MainScreen\.

**Prompt:** \Step M5.2. Anima LazyColumn no MainScreen com animateContentSize() e os ImageCards com Modifier.animateItem().\

**Ações do Agente:**
- Modificou o \MainScreen.kt\, adicionando \Modifier.animateContentSize()\ ao \LazyColumn\ e \Modifier.animateItem()\ aos items.
- Commit em conjunto com M5.3.

### Step M5.3 � Animar toggle do favorito

**Objetivo:** Adicionar transição suave de fade e scale quando o estado do icon de Favorito muda.

**Prompt:** \Step M5.3. Envolve o Icon de Favorite no DetailsScreen num AnimatedContent com fadeIn+scaleIn.\

**Ações do Agente:**
- Modificou \DetailsScreen.kt\, removendo o comentário placeholder.
- Envolveu o \Icon\ dentro do \IconButton\ com um bloco \AnimatedContent\, definindo \	ransitionSpec\ usando \	ogetherWith\ para \fadeIn + scaleIn\ e \fadeOut + scaleOut\.
- Commit: \feat(app-compose): animate list content size, item placement, and favorite toggle\

