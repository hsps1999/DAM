# CONTEXT.md — CatsNDogs MIP-3

## Estado atual: **M1 concluído — pronto para iniciar M2** 🟢

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

## O que foi feito no M1 (sessão atual)

- Cópia limpa do projeto `CatsNDogs` (MIP-2) para `CatsNDogs-MIP3`
- Limpeza: `.git/`, `.gradle/`, `.kotlin/`, `build/`, `local.properties` removidos
- Criação de `docs/MIP3/` com documentação MIP-3:
  - `00_module_diagram.md` — diagrama Mermaid de dependências
  - `01_architecture.md` — pacotes e classes por módulo
  - `02_ui_contract.md` — API pública do `:core`
  - `03_refactor_plan.md` — antes/depois detalhado
  - `04_implementation_plan.md` — steps atómicos M2-M6
  - `prompts_log.md` — log de prompts (vazio, formato pronto)
- `agents.md` da raiz substituído pela versão MIP-3
- Documentação MIP-2 mantida intacta em `docs/` (raiz)
- Repo Git inicializado com primeiro commit

---

## O que vem a seguir: **M2 — Extração do `:core`**

Ver detalhe em `docs/MIP3/04_implementation_plan.md`.

Sequência:
1. **M2.1** — Criar módulo `:core` (Android Library)
2. **M2.2** — Mover modelos e DTOs (`ImageItem`, `DogApiResponse`)
3. **M2.3** — Mover camada remota (`DogApiService`, `RetrofitClient`)
4. **M2.4** — Mover camada local Room (5 ficheiros)
5. **M2.5** — Mover `UiState` + converter para `@StringRes`
6. **M2.6** — Mover strings de erro para resources do `:core`
7. **M2.7** — Mover `ImageRepository` + converter `LiveData` para `Flow`
8. **M2.8** — Verificação `:core` standalone

Ao fim do M2, **a app `:app-xml` está com build partido** — esperado, será reparado no M3.

---

## Decisões técnicas a respeitar

- `:core` **não pode depender** de UI Framework (sem Activity, View, Compose).
- `:core` pode usar `androidx.lifecycle:viewmodel` se necessário, mas o tendência é manter ViewModels nos módulos de UI.
- Repository expõe `Flow<...>` para streams reativos — cada UI converte:
  - `:app-xml` → `.asLiveData()`
  - `:app-compose` → `.stateIn(...)` ou `collectAsStateWithLifecycle()`
- `UiState.Error` carrega `@StringRes Int`, **não** `String` — strings resolvidas pelo módulo de UI com o seu `Context`.
- `Application` class **fica em cada módulo de app**, não no `:core`. Cada uma constrói o seu Repository via `lazy`.
- AGP 9.x aplica Kotlin implicitamente — **não usar** `kotlin("android")` explícito nem `kotlinOptions { }` no `build.gradle.kts` dos novos módulos.

---

## Problemas conhecidos / pendentes

- `FavoritesBarController.kt` está em `ui/common/` no MIP-2 — no MIP-3 deve mover-se para `ui/favorites/` no `:app-xml` (decisão estética, sem impacto técnico).
- Após M3, o `applicationId` do `:app-xml` muda para `dam_a46104.catsndogs.xml` — pode invalidar instalações existentes no emulador (resolve-se com `uninstall + install`).
- `:app-compose` terá `applicationId` diferente (`dam_a46104.catsndogs.compose`) — propositadamente, para conviverem instaladas em paralelo no mesmo emulador.

---

## Como atualizar este ficheiro

A cada milestone fechado:
1. Actualizar a linha "Estado atual" no topo
2. Mover items de "O que vem a seguir" para "O que foi feito"
3. Adicionar quaisquer novas decisões técnicas
4. Registar problemas/bugs descobertos durante o milestone

Frequência típica: 1 commit `chore: update CONTEXT.md after Mx` por milestone fechado.
