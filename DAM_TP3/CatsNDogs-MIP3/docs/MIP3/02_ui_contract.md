# 02 — UI Contract

Este documento define a **interface pública** entre o módulo `:core` e os módulos de UI (`:app-xml`, `:app-compose`). Qualquer ViewModel — em qualquer das duas apps — só pode usar o que está aqui descrito.

---

## Objetivo

Garantir que ambas as UIs operam **exatamente sobre o mesmo contrato**. Se uma feature funciona numa app, tem de funcionar na outra. Se houve uma alteração ao Repository, ambas beneficiam (ou ambas têm de ser atualizadas).

---

## Modelos de dados expostos

### `ImageItem`

Domain model. Imutável. Disponível em `dam_a46104.catsndogs.core.model.ImageItem`.

```kotlin
data class ImageItem(
    val id: String,
    val url: String,
    val breed: String,
    val subBreed: String?
)
```

### `UiState<T>`

Sealed class para representar estado da UI. Disponível em `dam_a46104.catsndogs.core.common.UiState`.

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val messageResId: Int) : UiState<Nothing>()
}
```

> **Nota:** o erro guarda um `@StringRes Int`, não uma `String`. Assim cada módulo de app resolve a string com o seu próprio context (PT/EN), e o `:core` continua agnóstico de recursos UI.

> **String resources de erro:** o `:core` define os IDs das strings em `core/R.string.error_no_network`, etc. Cada app inclui o ficheiro `strings.xml` do `:core` automaticamente via Gradle resource merging.

---

## Operações expostas pelo Repository

A `ImageRepository` em `:core` é o **único ponto de acesso** a dados pelas UIs.

### `suspend fun fetchRandomImages(count: Int = 20): UiState<List<ImageItem>>`

- Chama Dog CEO API.
- Em sucesso: persiste em cache (Room) e devolve `UiState.Success(list)`.
- Em falha de rede: lê do cache. Se cache não vazia: devolve `UiState.Success(list)`. Se vazia: devolve `UiState.Error(R.string.error_no_network)`.
- Em outras falhas: devolve `UiState.Error(R.string.error_unknown)`.
- **Nunca lança exceção** para o ViewModel. Toda a falha é capturada e mapeada para `UiState.Error`.

### `fun getFavorites(): Flow<List<ImageItem>>`

- Stream reativo dos favoritos atuais (máximo 5, FIFO).
- Emite nova lista sempre que um favorito é adicionado/removido.
- Subscritores não precisam de chamar refresh — Room emite automaticamente.

### `fun isFavorite(id: String): Flow<Boolean>`

- Stream reativo do estado de favorito de uma imagem específica.
- Útil para o ícone de estrela no `DetailsScreen` / `ImageDetailsActivity`.

### `suspend fun toggleFavorite(item: ImageItem)`

- Adiciona se não estiver, remove se estiver.
- Aplica a regra FIFO: ao adicionar o 6.º, remove o mais antigo (`createdAt` mais baixo).

### `suspend fun findById(id: String): ImageItem?`

- Resolve um `ImageItem` por id, procurando nesta ordem:
  1. Lista em memória (last fetch)
  2. Tabela `cached_images` (Room)
  3. Tabela `favorites` (Room)
- Devolve `null` se não encontrar.

### `suspend fun getCachedImages(): List<ImageItem>`

- Devolve todos os itens da tabela de cache (sem favoritos).
- Usado para offline mode.

---

## Adaptação por módulo de UI

### `:app-xml` (LiveData)

```kotlin
class MainViewModel(private val repo: ImageRepository) : ViewModel() {
    private val _uiState = MutableLiveData<UiState<List<ImageItem>>>()
    val uiState: LiveData<UiState<List<ImageItem>>> = _uiState

    val favorites: LiveData<List<ImageItem>> = repo.getFavorites().asLiveData()

    fun loadImages(count: Int = 20) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repo.fetchRandomImages(count)
        }
    }
}
```

### `:app-compose` (StateFlow)

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

> Repara: **a lógica é literalmente igual**. Só muda o tipo de holder de estado. Esta simetria é o "selling point" da arquitetura multi-módulo.

---

## Eventos da UI para o ViewModel

Ambas as UIs emitem o mesmo conjunto de eventos:

| Evento | Trigger XML | Trigger Compose | Método no ViewModel |
|---|---|---|---|
| Refresh | tap no FAB | tap no FAB | `loadImages()` |
| Open details | tap no item da lista | tap no item da lista | navegação (não passa por VM) |
| Toggle favorite | tap na estrela (Details) | tap na estrela (DetailsScreen) | `toggleFavorite(item)` |
| Retry após erro | tap em "Retry" no Snackbar | tap em "Retry" no Snackbar | `loadImages()` |
| Open favorite | tap em miniatura | tap em miniatura | navegação para Details com `id` |

---

## O que **não** está no contrato

- A UI não acede diretamente a `DogApiService`, `AppDatabase`, ou aos DAOs.
- A UI não conhece a existência de Retrofit ou Room.
- A UI não constrói o `ImageRepository` — recebe-o por DI manual (mesma estratégia do MIP-2: `companion object getInstance(api, db)`).
- Mensagens de erro humanas são da UI — o `:core` só fornece o ID da string.

---

## Como o `Repository` é construído

Cada Application class (`:app-xml/CatsNDogsApp.kt` e `:app-compose/CatsNDogsApp.kt`) constrói o Repository uma vez:

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

Os ViewModels recebem o Repository via Factory (mesma abordagem do MIP-2).
