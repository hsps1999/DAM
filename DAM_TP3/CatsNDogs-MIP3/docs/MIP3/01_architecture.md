# 01 — Architecture

Detalhe da estrutura interna de cada módulo: pacotes, classes principais e responsabilidades.

---

## Módulo `:core`

**Tipo:** Android Library (`com.android.library`)
**Package raiz:** `dam_a46104.catsndogs.core`

### Pacotes e ficheiros

```
core/
├── model/
│   ├── ImageItem.kt              # Domain model (já existe no MIP-2)
│   └── DogApiResponse.kt         # DTO da API (já existe no MIP-2)
├── remote/
│   ├── DogApiService.kt          # Retrofit interface (já existe)
│   └── RetrofitClient.kt         # Singleton Retrofit (já existe)
├── local/
│   ├── CachedImage.kt            # Room entity (já existe)
│   ├── FavoriteEntry.kt          # Room entity (já existe)
│   ├── CacheDao.kt               # DAO (já existe)
│   ├── FavoriteDao.kt            # DAO (já existe)
│   └── AppDatabase.kt            # RoomDatabase (já existe)
├── repository/
│   └── ImageRepository.kt        # Repository (já existe)
└── common/
    └── UiState.kt                # Sealed class (já existe)
```

### Dependências do `:core` (`build.gradle.kts`)

- `androidx.room:room-runtime`
- `androidx.room:room-ktx`
- `androidx.room:room-compiler` (via KSP)
- `com.squareup.retrofit2:retrofit`
- `com.squareup.retrofit2:converter-gson`
- `org.jetbrains.kotlinx:kotlinx-coroutines-android`
- `androidx.lifecycle:lifecycle-livedata-ktx` (para `.asLiveData()` extension nas `Flow`)

### Decisões importantes

- O `ImageRepository` expõe `Flow<List<ImageItem>>` para favoritos (já era `LiveData` no MIP-2 — refactor para `Flow` durante o M2).
- `getFavorites()` e `isFavorite(id)` são `Flow` no Repository. Cada ViewModel adapta:
  - `:app-xml` → `.asLiveData()`
  - `:app-compose` → `.stateIn(viewModelScope, ..., initialValue)`
- O `CatsNDogsApp` (Application class) **não vai para o `:core`** — cada módulo de app tem a sua. A inicialização do Room (`AppDatabase.getInstance(context)`) recebe o context da Application local.

---

## Módulo `:app-xml`

**Tipo:** Android Application (`com.android.application`)
**Package raiz:** `dam_a46104.catsndogs.xml`
**ApplicationId:** `dam_a46104.catsndogs.xml`

### Pacotes e ficheiros

```
xml/
├── CatsNDogsApp.kt               # Application class (inicializa Room)
├── ui/
│   ├── main/
│   │   ├── MainActivity.kt       # já existe — só muda imports
│   │   └── ImageAdapter.kt       # já existe — só muda imports
│   ├── details/
│   │   └── ImageDetailsActivity.kt  # já existe — só muda imports
│   └── favorites/
│       └── FavoritesBarController.kt  # já existe — só muda imports
└── viewmodel/
    ├── MainViewModel.kt          # já existe — usa LiveData
    └── DetailsViewModel.kt       # já existe — usa LiveData
```

### Dependências do `:app-xml`

Todas as do MIP-2 (Glide, Material, RecyclerView, etc.) **mais**:
- `implementation(project(":core"))`

### O que NÃO está em `:app-xml`

Toda a data layer e domain model. Estes ficheiros foram movidos para `:core` no M2.

---

## Módulo `:app-compose`

**Tipo:** Android Application (`com.android.application`)
**Package raiz:** `dam_a46104.catsndogs.compose`
**ApplicationId:** `dam_a46104.catsndogs.compose`

### Pacotes e ficheiros

```
compose/
├── CatsNDogsApp.kt               # Application class (inicializa Room)
├── MainActivity.kt               # única activity (Compose-only)
├── navigation/
│   └── AppNavigation.kt          # NavHost com rotas
├── ui/
│   ├── main/
│   │   ├── MainScreen.kt         # composable principal
│   │   └── ImageCard.kt          # composable de item
│   ├── details/
│   │   └── DetailsScreen.kt      # composable de detalhes
│   ├── favorites/
│   │   └── FavoritesBar.kt       # composable horizontal
│   ├── common/
│   │   ├── LoadingIndicator.kt   # ProgressBar com AnimatedVisibility
│   │   └── ErrorSnackbar.kt      # snackbar de erro
│   └── theme/
│       ├── Theme.kt              # MaterialTheme setup
│       ├── Color.kt
│       └── Type.kt
└── viewmodel/
    ├── MainViewModel.kt          # usa StateFlow<UiState<...>>
    └── DetailsViewModel.kt       # usa StateFlow<UiState<...>>
```

### Dependências do `:app-compose`

- `implementation(project(":core"))`
- Compose BOM (`androidx.compose:compose-bom`)
- `androidx.compose.ui:ui`
- `androidx.compose.material3:material3`
- `androidx.compose.ui:ui-tooling-preview` + `ui-tooling` (debug)
- `androidx.activity:activity-compose`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `androidx.lifecycle:lifecycle-runtime-compose` (para `collectAsStateWithLifecycle`)
- `androidx.navigation:navigation-compose`
- `io.coil-kt:coil-compose` — preferido sobre Glide para Compose (mais idiomático)

### Feature exclusiva: animações

Implementadas no M5. Localização das animações:

- `LoadingIndicator.kt` — `AnimatedVisibility` com `fadeIn` + `fadeOut` quando `UiState.Loading`
- `MainScreen.kt` — `Modifier.animateContentSize()` no `LazyColumn` (suaviza altura quando lista muda)
- `ImageCard.kt` — `Modifier.animateItemPlacement()` para reordering suave
- `DetailsScreen.kt` — `AnimatedContent` no botão de favorito (transição estrela vazia ↔ cheia)

---

## Fluxo de dados (igual nos dois módulos de app)

```
User action (tap, refresh, toggle favorite)
  ↓
ViewModel (specific to :app-xml or :app-compose)
  ↓
ImageRepository (in :core)
  ↓
DogApiService (Retrofit)  +  AppDatabase (Room)
  ↓
ViewModel emite novo UiState
  ↓
UI observa (Observer / collectAsStateWithLifecycle)
  ↓
UI re-renderiza
```

A **única diferença** entre as duas apps é o último passo: como a UI é renderizada (XML inflation + view binding, vs. Compose recomposition).

---

## Resumo: o que muda, o que não muda

| Camada | MIP-2 (single module) | MIP-3 (`:app-xml`) | MIP-3 (`:app-compose`) |
|---|---|---|---|
| Models | `data/model/` | em `:core` | em `:core` |
| API client | `data/remote/` | em `:core` | em `:core` |
| Room | `data/local/` | em `:core` | em `:core` |
| Repository | `data/repository/` | em `:core` | em `:core` |
| UiState | `ui/common/` | em `:core` | em `:core` |
| ViewModels | `viewmodel/` | em `:app-xml` (LiveData) | em `:app-compose` (StateFlow) |
| Activities | `ui/main/`, `ui/details/` | em `:app-xml` (XML) | substituídas por Composables |
| Adapters | `ui/main/ImageAdapter.kt` | em `:app-xml` | n/a (LazyColumn substitui) |
| Layouts | `res/layout/*.xml` | em `:app-xml` | n/a (Composables) |
| Strings | `res/values/strings.xml` | em `:app-xml` | em `:app-compose` (separadas) |
