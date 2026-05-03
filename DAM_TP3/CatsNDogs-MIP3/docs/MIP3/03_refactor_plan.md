# 03 — Refactor Plan

Plano de refactoring detalhado: o que migra do MIP-2 (single-module) para o MIP-3 (multi-module). Este documento serve dois propósitos:

1. **Guia de execução** durante o M2 (extração do `:core`).
2. **Entregável de avaliação** — explica ao docente como a transformação foi feita.

---

## Estado inicial (MIP-2)

App `CatsNDogs` com um único módulo `:app`, package raiz `dam_a46104.catsndogs`. Estrutura:

```
app/src/main/java/dam_a46104/catsndogs/
├── CatsNDogsApp.kt
├── data/
│   ├── model/
│   │   ├── ImageItem.kt
│   │   └── DogApiResponse.kt
│   ├── remote/
│   │   ├── DogApiService.kt
│   │   └── RetrofitClient.kt
│   ├── local/
│   │   ├── CachedImage.kt
│   │   ├── FavoriteEntry.kt
│   │   ├── CacheDao.kt
│   │   ├── FavoriteDao.kt
│   │   └── AppDatabase.kt
│   └── repository/
│       └── ImageRepository.kt
├── ui/
│   ├── common/
│   │   └── UiState.kt
│   ├── main/
│   │   ├── MainActivity.kt
│   │   └── ImageAdapter.kt
│   ├── details/
│   │   └── ImageDetailsActivity.kt
│   └── favorites/
│       └── FavoritesBarController.kt
└── viewmodel/
    ├── MainViewModel.kt
    └── DetailsViewModel.kt
```

---

## Estado final (MIP-3)

Três módulos:

```
CatsNDogs-MIP3/
├── core/                          # Android Library
│   └── src/main/java/dam_a46104/catsndogs/core/
│       ├── model/
│       │   ├── ImageItem.kt
│       │   └── DogApiResponse.kt
│       ├── remote/
│       │   ├── DogApiService.kt
│       │   └── RetrofitClient.kt
│       ├── local/
│       │   ├── CachedImage.kt
│       │   ├── FavoriteEntry.kt
│       │   ├── CacheDao.kt
│       │   ├── FavoriteDao.kt
│       │   └── AppDatabase.kt
│       ├── repository/
│       │   └── ImageRepository.kt
│       └── common/
│           └── UiState.kt
├── app-xml/                       # Android Application
│   └── src/main/java/dam_a46104/catsndogs/xml/
│       ├── CatsNDogsApp.kt
│       ├── ui/main/
│       │   ├── MainActivity.kt
│       │   └── ImageAdapter.kt
│       ├── ui/details/
│       │   └── ImageDetailsActivity.kt
│       ├── ui/favorites/
│       │   └── FavoritesBarController.kt
│       └── viewmodel/
│           ├── MainViewModel.kt
│           └── DetailsViewModel.kt
├── app-compose/                   # Android Application (NOVO)
│   └── src/main/java/dam_a46104/catsndogs/compose/
│       ├── CatsNDogsApp.kt
│       ├── MainActivity.kt
│       ├── navigation/
│       │   └── AppNavigation.kt
│       ├── ui/...
│       └── viewmodel/...
├── settings.gradle.kts
└── build.gradle.kts (root)
```

---

## Mapa de migração (M2)

| Origem (MIP-2) | Destino (MIP-3) | Mudança ao código |
|---|---|---|
| `data/model/ImageItem.kt` | `core/model/ImageItem.kt` | só package |
| `data/model/DogApiResponse.kt` | `core/model/DogApiResponse.kt` | só package |
| `data/remote/DogApiService.kt` | `core/remote/DogApiService.kt` | só package |
| `data/remote/RetrofitClient.kt` | `core/remote/RetrofitClient.kt` | só package |
| `data/local/CachedImage.kt` | `core/local/CachedImage.kt` | só package |
| `data/local/FavoriteEntry.kt` | `core/local/FavoriteEntry.kt` | só package |
| `data/local/CacheDao.kt` | `core/local/CacheDao.kt` | só package |
| `data/local/FavoriteDao.kt` | `core/local/FavoriteDao.kt` | só package |
| `data/local/AppDatabase.kt` | `core/local/AppDatabase.kt` | só package |
| `data/repository/ImageRepository.kt` | `core/repository/ImageRepository.kt` | package + **converter `LiveData` → `Flow`** nos métodos `getFavorites()` e `isFavorite()` |
| `ui/common/UiState.kt` | `core/common/UiState.kt` | package + **`Error(message: String)` → `Error(messageResId: Int)`** |

### Strings de erro

`strings.xml` do MIP-2 inclui:
- `error_no_network`
- `error_server`
- `error_unknown`

Estas strings movem-se para `core/src/main/res/values/strings.xml` (e `values-pt/strings.xml`). Resource merging do Gradle expõe-nas a ambas as apps automaticamente.

---

## Refactor do `:app-xml` (M3)

| Ficheiro | Mudança |
|---|---|
| `MainActivity.kt` | imports: `dam_a46104.catsndogs.data.model.*` → `dam_a46104.catsndogs.core.model.*` (idem para os outros packages) |
| `ImageAdapter.kt` | mesmo |
| `ImageDetailsActivity.kt` | mesmo |
| `FavoritesBarController.kt` | mesmo |
| `MainViewModel.kt` | imports + `repo.getFavorites().asLiveData()` (era `LiveData` direto) |
| `DetailsViewModel.kt` | imports + `repo.isFavorite(id).asLiveData()` |
| `CatsNDogsApp.kt` | move-se para o package `dam_a46104.catsndogs.xml` |
| `AndroidManifest.xml` | atualizar `android:name=".CatsNDogsApp"` se necessário (continua relativo ao package do módulo) |
| `app-xml/build.gradle.kts` | adicionar `implementation(project(":core"))`, remover dependências de Retrofit/Room (vêm via `:core`) |

### Ficheiros a apagar do `:app-xml`

Tudo o que migrou para `:core` (lista completa na tabela do M2).

---

## Setup do `:app-compose` (M4)

Não há "migração" aqui — é tudo código novo. O M4 é tratado como construção raiz, mas reutiliza:

- 100% da data layer do `:core`
- 100% da lógica de Repository
- Strings PT/EN do `:core` (e adiciona as suas se necessário)
- Os mesmos modelos `ImageItem`, `UiState`

A única decisão real: **Coil em vez de Glide** para image loading, porque Coil tem suporte nativo a Compose (`AsyncImage`).

---

## Decisões arquiteturais e justificações

### 1. Por que `Flow` em vez de `LiveData` no Repository?

`LiveData` é uma classe Android específica para o ciclo de vida do Activity/Fragment. `Flow` é mais geral, mais flexível, e ambos os paradigmas (imperativo XML e declarativo Compose) o suportam:

- Em XML: `flow.asLiveData()` produz `LiveData` para usar com `Observer`.
- Em Compose: `flow.collectAsStateWithLifecycle()` produz `State<T>`.

Manter `LiveData` no Repository forçaria o `:app-compose` a usar `liveData.observeAsState()`, que é menos idiomático e tem caveats (não respeita lifecycle aware sem boilerplate).

### 2. Por que `@StringRes Int` no `UiState.Error`?

No MIP-2, `Error(message: String)` recebia uma string já resolvida via `Application.getString(id)`. Isto obrigava o ViewModel a ser `AndroidViewModel` (acesso ao `Application` context).

No MIP-3, `:core` não pode aceder a recursos de cada app. Solução: o `UiState.Error` carrega o `@StringRes Int`, e cada UI resolve a string com o seu próprio `Context` no momento de mostrar o Snackbar/erro.

Bónus: o ViewModel volta a ser `ViewModel` simples (sem dependência de `Application`).

### 3. Por que ViewModels duplicados em `:app-xml` e `:app-compose`?

Apesar de a lógica ser quase idêntica, o **state shape difere**: `LiveData` vs `StateFlow`. Forçar um único ViewModel partilhado obrigaria a expor `Flow` (mais geral) e converter em ambos os lados — mas o código do `:app-xml` ficaria menos idiomático (XML world está habituado a `LiveData`).

A duplicação aqui é **assumida e justificada**: cada UI tem um ViewModel idiomático para o seu paradigma, e ambos delegam toda a lógica real ao Repository do `:core`.

### 4. Por que Application class em cada módulo de app, e não no `:core`?

`Application` é o entry point do Android. Se vivesse no `:core`, ambos os módulos de app teriam de a usar — mas cada um quer a sua (com nome distinto, possivelmente lógica de inicialização específica, e provavelmente DI setup diferente no futuro).

Compromisso: `:core` expõe uma factory `RepositoryProvider.create(context)` (helper pequeno), e cada Application chama-a no `onCreate` ou via `lazy`.

---

## O que NÃO muda

Toda a lógica de negócio:
- Paginação de cache (50 items LRU)
- FIFO de favoritos (5 max)
- Fallback offline
- Mapeamento de URL → `ImageItem` (parsing de breed/subBreed)
- Tratamento de erros tipados (IOException, HttpException)

Estas são as features que vão estar **idênticas** nas duas UIs. Esse é o ponto.
