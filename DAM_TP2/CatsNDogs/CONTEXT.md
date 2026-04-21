# CONTEXT.md — CatsNDogs

## Estado atual: **Phase 2 concluída ✅**

---

## O que foi feito

### Phase 1 (sessões anteriores — Steps 1-15)
- Projeto Android criado com MVVM + Repository pattern
- `ImageItem` (modelo de domínio), `DogApiService` (Retrofit), `RetrofitClient`
- `ImageRepository` (singleton), `MainViewModel` (AndroidViewModel)
- `MainActivity` com RecyclerView, `ImageAdapter` com Glide
- `UiState` (sealed class: Loading / Success / Error)

### Phase 2 — Extensões

#### Extensão A — Loading Indicator ✅
- `activity_main.xml`: ProgressBar centrada, `visibility="gone"`
- `MainActivity`: toggle ProgressBar / RecyclerView (mutuamente exclusivos)

#### Extensão B — Error Handling ✅
- `ImageRepository`: try/catch tipado (IOException, HttpException)
- `MainViewModel`: migrado para AndroidViewModel; mapeia exceções para strings
- `MainActivity`: Snackbar de erro com botão "Retry"
- `strings.xml`: `error_no_network`, `error_server`, `error_unknown`

#### Extensão C — Image Details Screen ✅
- `ImageDetailsActivity` + `activity_image_details.xml`
- `DetailsViewModel` (AndroidViewModel, switchMap para isFavorite)
- `ImageAdapter`: click listener com callback `(ImageItem) -> Unit`
- `AndroidManifest.xml`: `parentActivityName` para back arrow

#### Extensão D — Cache Room (até 50 itens) ✅
- KSP 2.3.6 + Room 2.7.0 (Room Gradle Plugin)
- `CachedImage` (@Entity), `CacheDao` (pruning LRU em SQL)
- `AppDatabase` (singleton, version 1→2, exportSchema=false)
- `CatsNDogsApp` (Application class, inicializa AppDatabase)
- `ImageRepository`: persiste após fetch, `getCachedImages()`

#### Extensão E — Acesso Offline ✅
- `UiState.Success`: novo campo `isFromCache: Boolean = false`
- `ImageRepository.fetchRandomImages()`: retorna `Pair<List<ImageItem>, Boolean>`; fallback Room em IOException
- `MainViewModel`: desestrutura o Pair, passa isFromCache ao Success
- `MainActivity`: Snackbar informativa "Sem rede — a mostrar conteúdo guardado"
- `strings.xml`: `info_offline_cache`

#### Extensão F — Favoritos FIFO (máx. 5) ✅
- `FavoriteEntry` (@Entity), `FavoriteDao` (insert, deleteById, getAll, count, getOldest, isFavorite, isFavoriteSync, findByIdSync)
- `AppDatabase` v2: entidade FavoriteEntry + MIGRATION_1_2 (CREATE TABLE favorites)
- `ImageRepository`: `toggleFavorite()` FIFO, `getFavorites()`, `isFavorite()`
- `DetailsViewModel`: `isFavorite` (switchMap), `favorites`, `toggleFavorite()`
- `view_favorites_bar.xml` + `FavoritesBarController` (observer via LifecycleOwner)
- `activity_main.xml` + `activity_image_details.xml`: `<include>` da barra
- `MainViewModel`: expõe `favorites: LiveData<List<ImageItem>>`
- `themes.xml`: `ShapeAppearance.Circular` para miniaturas

#### Fix pós-Extensão F ✅
- `findById()` → `suspend`, lookup em 3 camadas: memória → cached_images → favorites
- `CacheDao`: +`findById(id)`; `FavoriteDao`: +`findByIdSync(id)`
- `DetailsViewModel.loadImage()`: envolto em `viewModelScope.launch`

---

## Ficheiros criados nesta fase

| Ficheiro | Descrição |
|----------|-----------|
| `data/local/CachedImage.kt` | Entity Room cache |
| `data/local/CacheDao.kt` | DAO cache (LRU) |
| `data/local/AppDatabase.kt` | DB singleton v2 |
| `data/local/FavoriteEntry.kt` | Entity Room favoritos |
| `data/local/FavoriteDao.kt` | DAO favoritos |
| `CatsNDogsApp.kt` | Application class |
| `ui/common/FavoritesBarController.kt` | Controller da barra |
| `res/layout/view_favorites_bar.xml` | Layout da barra |
| `res/layout/item_favorite_thumbnail.xml` | Miniatura individual |
| `README.md` | README final do projeto |

---

## Ficheiros alterados nesta fase

`gradle/libs.versions.toml`, `build.gradle.kts` (root), `app/build.gradle.kts`,
`AndroidManifest.xml`, `ImageRepository.kt`, `MainViewModel.kt`, `DetailsViewModel.kt`,
`MainActivity.kt`, `ImageDetailsActivity.kt`, `activity_main.xml`, `activity_image_details.xml`,
`UiState.kt`, `strings.xml`, `themes.xml`

---

## Decisões técnicas tomadas

- **KSP:** versão 2.3.6 (KSP2) — compatível com AGP 9.x "built-in Kotlin"
- **Room:** versão 2.7.0 + Room Gradle Plugin (obrigatório em 2.7+)
- **`AppDatabase`:** recebido pelo `ImageRepository` (resolve DAOs internamente)
- **FIFO:** lógica no Repository, DAO mantém-se simples
- **`findById()`:** suspend, 3 camadas (memória → cache → favoritos)
- **`FavoritesBarController`:** recebe `LifecycleOwner` e observa LiveData internamente
- **`isFavorite`:** `switchMap` em `_currentId` para correcta re-derivação após rotação

---

## O que falta fazer

- [ ] Adicionar screenshots reais em `docs/screenshots/`
- [ ] Commit final: `docs: final README with screenshots and project notes`
- [ ] Checkpoint final da Phase 2 (checklist em `09_feature_extensions.md`)

---

## Problemas conhecidos / pendentes

- A `FavoritesBarController` usa `android.R.drawable.ic_menu_gallery` como placeholder — substituir por drawable próprio se necessário
- Screenshots ainda não capturados — a tabela no README usa caminhos de placeholder
