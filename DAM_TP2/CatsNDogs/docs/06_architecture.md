# 06 — Architecture

## Padrão arquitetural: MVVM + Repository

```
┌─────────────────────────────────────────────────────────┐
│                         UI Layer                        │
│  ┌────────────────────┐      ┌──────────────────────┐   │
│  │   MainActivity     │      │ ImageDetailsActivity │   │
│  │   + XML Views      │      │   + XML Views        │   │
│  └─────────┬──────────┘      └──────────┬───────────┘   │
│            │ observes LiveData          │ observes      │
│  ┌─────────▼──────────┐      ┌──────────▼───────────┐   │
│  │  MainViewModel     │      │ DetailsViewModel     │   │
│  └─────────┬──────────┘      └──────────┬───────────┘   │
└────────────┼────────────────────────────┼───────────────┘
             │                            │
             └────────────┬───────────────┘
                          │ chama suspend functions
              ┌───────────▼─────────────┐
              │    ImageRepository      │  ← Singleton
              │  (single source truth)  │
              └─────┬──────────────┬────┘
                    │              │
        ┌───────────▼──┐      ┌────▼──────────┐
        │ DogApiService│      │  AppDatabase  │
        │   (Retrofit) │      │    (Room)     │
        └──────────────┘      └───────────────┘
              │                       │
              ▼                       ▼
         dog.ceo API            SQLite local
```

## Camadas

### 1. UI Layer (`ui/`)

Activities e Adapters. Responsabilidades:

- Inflar layouts XML
- Observar `LiveData` exposto pelo ViewModel
- Reagir a eventos de utilizador e delegar para o ViewModel
- **Nunca** chamar diretamente o Repository ou a API

### 2. ViewModel Layer (`viewmodel/`)

Subclasses de `androidx.lifecycle.ViewModel`. Responsabilidades:

- Expor estado via `LiveData<UiState<T>>`
- Lançar coroutines no `viewModelScope`
- Mediar entre UI e Repository
- Sobreviver a rotações de ecrã

**Estado tipado:**

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### 3. Repository Layer (`data/repository/`)

`ImageRepository` é a **única fonte de verdade**. Responsabilidades:

- Decidir entre fetch da API e leitura do cache
- Atualizar Room com novos dados
- Expor APIs `suspend` limpas para o ViewModel
- Tratar fallback offline (API falha → devolve cache)

Implementação como **singleton** via construtor passado em `Application` ou via Hilt (opcional para Phase 2).

### 4. Data Sources

#### Remote: `DogApiService` (`data/remote/`)

Interface Retrofit:

```kotlin
interface DogApiService {
    @GET("api/breeds/image/random/{count}")
    suspend fun getRandomImages(@Path("count") count: Int): DogApiResponse
}
```

#### Local: `AppDatabase` (`data/local/`)

Room database com dois DAOs:

- `FavoriteDao` — CRUD de favoritos com regra FIFO
- `CacheDao` — CRUD de cache com política LRU (50 itens)

## Estrutura de pastas

```
app/src/main/java/dam_a46104/catsndogs/
├── CatsNDogsApp.kt                 # Application class (init Repository singleton)
├── data/
│   ├── model/
│   │   ├── ImageItem.kt             # Modelo de domínio
│   │   └── DogApiResponse.kt        # DTO da API
│   ├── remote/
│   │   ├── DogApiService.kt
│   │   └── RetrofitClient.kt
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── FavoriteEntry.kt
│   │   ├── FavoriteDao.kt
│   │   ├── CachedImage.kt
│   │   └── CacheDao.kt
│   └── repository/
│       └── ImageRepository.kt
├── ui/
│   ├── main/
│   │   ├── MainActivity.kt
│   │   └── ImageAdapter.kt
│   ├── details/
│   │   └── ImageDetailsActivity.kt
│   └── common/
│       └── UiState.kt
└── viewmodel/
    ├── MainViewModel.kt
    └── DetailsViewModel.kt
```

## Threading

- **UI thread:** apenas observers de `LiveData` e setters de Views.
- **Coroutines:** `viewModelScope` para chamadas ao Repository.
- **Dispatchers:** `Dispatchers.IO` para chamadas Retrofit e Room (configurado dentro do Repository, não nos ViewModels).

## Injeção de dependências

Phase 1: manual via construtores e `Application`.
Phase 2 (opcional): Hilt — só introduzir se sobrar tempo.

## Princípios

- **Unidirectional data flow:** UI → ViewModel → Repository → Data Sources, e estado volta no sentido inverso via `LiveData`.
- **Single source of truth:** Repository é o único componente que fala com API e DB.
- **Testabilidade:** ViewModels não conhecem o Android framework além de `LiveData`; Repository pode ser mockado.
