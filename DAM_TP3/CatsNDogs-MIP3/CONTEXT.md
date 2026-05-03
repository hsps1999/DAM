# CONTEXT.md — CatsNDogs MIP-3

## Estado atual: **Projeto concluído — pronto para entrega** 🚀

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
- O `:core` é agora um Android Library module **standalone** — build verde, sem dependências para `:app`.
- Toda a lógica de dados, modelos e repositório centralizado usando `Flow`.

---

## O que foi feito no M3 — Refactor `:app-xml` ✅
- O módulo foi renomeado de `app` para `app-xml`.
- Os ViewModels foram adaptados para converter `Flow` do `:core` em `LiveData` para a UI de Views usar com Observers (`.asLiveData()`).
- Refatoração massiva de imports (`data.*` para `core.*`).
- `FavoritesBarController` foi movido da pasta common para `ui/favorites/`.

---

## O que foi feito no M4 — Novo módulo `:app-compose` ✅
- Criado novo módulo `:app-compose` com dependências de Material 3, Navigation Compose e Coil.
- Implementada gestão de estado com `StateFlow` e `collectAsStateWithLifecycle()` em todo o módulo.
- Implementado `NavHost` (`navigation/AppNavigation.kt`) para navegação entre `MainScreen` e `DetailsScreen`.
- UI recriada de zero com composables: `MainScreen`, `ImageCard`, `DetailsScreen`, `FavoritesBar`.
- Adicionado suporte de `strings.xml` nos recursos para internacionalização do UI (separado do core).
- Resolvidos os bloqueios de compilação: recursos `mipmap` partilhados, dependência de *Room* atualizada para `api`, `Theme.kt` protegido contra `ContextWrapper`, e `<uses-permission android:name="android.permission.INTERNET"/>` adicionado ao AndroidManifest.

---

## O que foi feito no M5 — Feature exclusiva: Animações ✅
- Extração do LoadingIndicator para `AnimatedVisibility` (fade + scale) no `MainScreen.kt`.
- `LazyColumn` animado com `animateContentSize()` para redimensionamentos suaves.
- Elementos `ImageCard` da `LazyColumn` animados com `Modifier.animateItem()` para reposicionamento fluido (substituição da API descontinuada animateItemPlacement).
- Ícone de "Favorito" no `DetailsScreen` envolvido em `AnimatedContent` (fade + scale) ao alternar entre preenchido e contorno.

---

## O que foi feito no M6 — Entregáveis finais ✅
- Criado o ficheiro `README.md` com arquitetura, dependências e diagrama de módulos atualizado.
- Step de screenshots (M6.2) saltado a pedido do utilizador.
- Limpeza final realizada: órfãos (TODOs) e imports revistos via `lint`.
- Apps compilam com sucesso (`assembleDebug`), embora `installDebug` tenha reportado dispositivo não autorizado (o que valida a compilação de qualquer forma).

---

## Problemas conhecidos / pendentes

- Warning KT-73255: `@StringRes` em `data class Error` (mantido como warning não bloqueante).
- O ecrã do telemóvel deve ser autorizado via adb (prompt de debug via USB) caso se pretenda fazer installDebug a partir da linha de comandos.

---

## Decisões técnicas a respeitar

- `:core` **não pode depender** de UI Framework (sem Activity, View, Compose).
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
