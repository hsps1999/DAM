# CONTEXT.md — CatsNDogs MIP-3

## Estado atual: **M2 concluído — pronto para iniciar M3** 🟢

Snapshot atualizado a cada milestone fechado. Serve de "estado-resumo" para o agente AntiGravity contextualizar-se no início de cada sessão sem ter de reler todos os ficheiros do `prompts_log.md`.

---

## O projeto

App Android **CatsNDogs** refatorada de single-module (MIP-2, Tutorial 2) para multi-module (MIP-3, Tutorial 3). Três módulos:

- **`:core`** — Android Library com data layer + business logic (Retrofit, Room, Repository, models, UiState)
- **`:app-xml`** — Android Application com UI em XML Views (refactor da app MIP-2)
- **`:app-compose`** — Android Application com UI em Jetpack Compose (novo)

Ambas as apps consomem **exatamente o mesmo `:core`**. Feature exclusiva do `:app-compose`: animações (`AnimatedVisibility`, `animateContentSize`, `AnimatedContent`).

---

## Baseline herdada do MIP-2

App MIP-2 entregue completa, com Phase 1 (Steps 1-15) + Phase 2 (Extensões A-F):

- Phase 1: MVVM + Repository pattern, Retrofit, RecyclerView com Glide
- Phase 2: Loading indicator, error handling com Retry, Details screen, cache Room (50 LRU), offline access, favoritos FIFO (5 max)

Ver histórico completo em `docs/01_overview.md` até `docs/09_feature_extensions.md` (documentação herdada do MIP-2, intacta).

**Versões fixadas (a manter):**
- AGP 9.x (built-in Kotlin)
- KSP 2.3.6
- Room 2.7.0 + Room Gradle Plugin
- Min SDK 24

---

## O que foi feito no M1

- Cópia limpa do projeto `CatsNDogs` (MIP-2) para `CatsNDogs-MIP3`
- Limpeza: `.git/`, `.gradle/`, `.kotlin/`, `build/`, `local.properties` removidos
- Criação de `docs/MIP3/` com documentação MIP-3
- `agents.md` substituído pela versão MIP-3
- Repo Git inicializado

---

## O que foi feito no M2 — Extração do `:core` ✅

Todos os 8 steps concluídos. O `:core` é agora um Android Library module **standalone** — build verde, sem dependências para `:app`.

| Step | O que foi feito | Commit |
|------|-----------------|--------|
| M2.1 | Criação do módulo `:core` (build.gradle.kts, AndroidManifest, settings.gradle.kts) | `chore(core): create :core android library module` |
| M2.2 | `ImageItem.kt` + `DogApiResponse.kt` → `core.model` | `refactor(core): move domain models to :core` |
| M2.3 | `DogApiService.kt` + `RetrofitClient.kt` → `core.remote` | `refactor(core): move Retrofit client and service to :core` |
| M2.4 | `CachedImage`, `FavoriteEntry`, `CacheDao`, `FavoriteDao`, `AppDatabase` → `core.local` | `refactor(core): move Room database, entities and DAOs to :core` |
| M2.5 | `UiState` → `core.common`; `Error.message: String` → `Error.messageResId: @StringRes Int` | `refactor(core): move UiState to :core and use StringRes for errors` |
| M2.6 | 4 strings de erro/offline → `core/src/main/res/values/strings.xml` | `refactor(core): move error and offline strings to :core resources` |
| M2.7 | `ImageRepository` → `core.repository`; `LiveData` → `Flow` em DAO+Repository; `fetchRandomImages` devolve `UiState` | `refactor(core): move ImageRepository to :core, convert LiveData to Flow` |
| M2.8 | Validação standalone: `:core:build` verde; sem `project ":app"` nas deps; `:app` falha como esperado | `docs: update CONTEXT.md after M2 completion` |

### Estrutura do `:core` após M2

```
core/src/main/java/dam_a46104/catsndogs/core/
  ├── common/     UiState.kt
  ├── local/      AppDatabase.kt, CacheDao.kt, CachedImage.kt, FavoriteDao.kt, FavoriteEntry.kt
  ├── model/      DogApiResponse.kt, ImageItem.kt
  ├── remote/     DogApiService.kt, RetrofitClient.kt
  └── repository/ ImageRepository.kt
core/src/main/res/values/strings.xml   (error_no_network, error_server, error_unknown, info_offline_cache)
```

### Decisões técnicas tomadas no M2

- **AGP 9.x rule**: plugins em submodules via `id("...")` direto, **não** `alias()` — evita "plugin already on classpath" error.
- **Flow em vez de LiveData no `:core`**: `FavoriteDao.getAll()` e `isFavorite()` convertidos para `Flow`; o ViewModel de cada app converte conforme necessário.
- **`UiState.Error` com `@StringRes Int`**: warning KT-73255 (Kotlin 2.x) sobre anotação em `data class` é não-blocking; resolver com `@param:StringRes` no M6 cleanup.
- **`getCachedImages()` é `private`** no novo Repository (sem usos externos em `:core`).
- **`FavoritesBarController.kt`** permanece em `app/ui/common/` — não faz parte do `:core`.

---

## O que vem a seguir: **M3 — Adaptar `:app-xml` para consumir `:core`**

Ver detalhe em `docs/MIP3/04_implementation_plan.md`.

O `:app` (futuro `:app-xml`) está atualmente com build partido — todos os imports que apontavam para `data.model`, `data.remote`, `data.local`, `data.repository` e `ui.common.UiState` estão unresolved.

Sequência do M3:
1. **M3.1** — Adicionar `implementation(project(":core"))` ao `app/build.gradle.kts`
2. **M3.2** — Corrigir todos os imports partidos (substituir `data.*` por `core.*`)
3. **M3.3** — Atualizar `CatsNDogsApp` para inicializar `AppDatabase` e `ImageRepository` de `:core`
4. **M3.4** — Adaptar ViewModels: converter `Flow` → `LiveData` via `.asLiveData()`; adaptar `UiState.Error` para usar `getString(messageResId)`
5. **M3.5** — Verificar build verde (`./gradlew :app:assembleDebug`) e correr no emulador
6. **M3.6** — Renomear módulo para `:app-xml` (opcional, pode ficar para M6)

---

## Problemas conhecidos / pendentes

- Warning KT-73255: `@StringRes` em `data class Error` — silenciar com `@param:StringRes` no M6 cleanup.
- `FavoritesBarController.kt` em `app/ui/common/` — no M3 mover para `ui/favorites/` (estético, sem impacto técnico).
- Após M3, o `applicationId` do `:app-xml` muda para `dam_a46104.catsndogs.xml` — pode invalidar instalações existentes no emulador (resolve-se com `uninstall + install`).
- `:app-compose` terá `applicationId` diferente (`dam_a46104.catsndogs.compose`) — propositadamente, para conviverem instaladas em paralelo no mesmo emulador.

---

## Decisões técnicas a respeitar

- `:core` **não pode depender** de UI Framework (sem Activity, View, Compose). ✅ Validado em M2.8.
- Repository expõe `Flow<...>` para streams reativos — cada UI converte:
  - `:app-xml` → `.asLiveData()`
  - `:app-compose` → `.stateIn(...)` ou `collectAsStateWithLifecycle()`
- `UiState.Error` carrega `@StringRes Int`, **não** `String` — strings resolvidas pelo módulo de UI com o seu `Context`.
- `Application` class **fica em cada módulo de app**, não no `:core`.
- AGP 9.x aplica Kotlin implicitamente — **não usar** `kotlin("android")` explícito nem `kotlinOptions { }` nos novos módulos.

---

## Como atualizar este ficheiro

A cada milestone fechado:
1. Actualizar a linha "Estado atual" no topo
2. Mover items de "O que vem a seguir" para "O que foi feito"
3. Adicionar quaisquer novas decisões técnicas
4. Registar problemas/bugs descobertos durante o milestone

Frequência típica: 1 commit `docs: update CONTEXT.md after Mx` por milestone fechado.
