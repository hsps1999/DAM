# 08 — Implementation Plan

Plano sequencial para a Phase 1. Cada step corresponde idealmente a **um prompt ao AntiGravity** e **um commit Git**. Não saltar steps. Não combinar steps no mesmo commit (exceto quando explicitamente marcado).

Extensões da Phase 2 estão em `09_feature_extensions.md` (criado após Phase 1 concluída).

---

## Step 1 — Criar projeto Android

**Objetivo:** Projeto base compilável.

**Tarefas:**
- Android Studio / AntiGravity → New Project → **Empty Views Activity**
- Nome: `CatsNDogs`
- Package: `dam_a46104.catsndogs`
- Linguagem: Kotlin
- Min SDK: 24 (Android 7.0)
- Build configuration: Kotlin DSL (`build.gradle.kts`)

**Verificação:** Build limpo + run no emulador mostra "Hello World".

**Commit:** `chore: initial android project setup`

---

## Step 2 — Adicionar dependências

**Objetivo:** Stack de bibliotecas pronta.

**Ficheiro:** `app/build.gradle.kts`

**Adicionar:**
- `retrofit2` + `converter-gson`
- `kotlinx-coroutines-android`
- `lifecycle-viewmodel-ktx` + `lifecycle-livedata-ktx`
- `recyclerview`
- `glide`
- `material` (Material Components)

**Adicionar ao `AndroidManifest.xml`:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

**Verificação:** `./gradlew build` com sucesso.

**Commit:** `chore: add networking, lifecycle and image loading dependencies`

---

## Step 3 — Criar estrutura de pastas

**Objetivo:** Organização do código conforme `06_architecture.md`.

**Tarefas:** Criar pacotes vazios:
- `data/model/`
- `data/remote/`
- `data/repository/`
- `ui/main/`
- `ui/common/`
- `viewmodel/`

**Commit:** `chore: scaffold package structure`

---

## Step 4 — Modelo de domínio: `ImageItem`

**Ficheiro:** `data/model/ImageItem.kt`

**Conteúdo:** data class conforme `04_data_model.md`.

**Commit:** `feat(model): add ImageItem domain model`

---

## Step 5 — DTO da API: `DogApiResponse`

**Ficheiro:** `data/model/DogApiResponse.kt`

**Conteúdo:** data class para o endpoint `/breeds/image/random/{count}` (`message: List<String>`, `status: String`).

**Commit:** `feat(model): add DogApiResponse DTO`

---

## Step 6 — `DogApiService` (Retrofit interface)

**Ficheiro:** `data/remote/DogApiService.kt`

**Conteúdo:** interface com método `suspend fun getRandomImages(count: Int): DogApiResponse`.

**Commit:** `feat(remote): add Retrofit service interface`

---

## Step 7 — `RetrofitClient`

**Ficheiro:** `data/remote/RetrofitClient.kt`

**Conteúdo:** singleton com builder Retrofit configurado para `https://dog.ceo/`. Expõe instância de `DogApiService`.

**Commit:** `feat(remote): add Retrofit client singleton`

---

## Step 8 — `UiState` selado

**Ficheiro:** `ui/common/UiState.kt`

**Conteúdo:** sealed class com `Loading`, `Success<T>`, `Error` (ver `06_architecture.md`).

**Commit:** `feat(ui): add UiState sealed class for state representation`

---

## Step 9 — `ImageRepository`

**Ficheiro:** `data/repository/ImageRepository.kt`

**Conteúdo:**
- Singleton (companion object com `getInstance(api: DogApiService)`)
- Método `suspend fun fetchRandomImages(count: Int): List<ImageItem>`
- Faz chamada à API, mapeia `DogApiResponse` → `List<ImageItem>` (parsing de raça e id a partir do URL)
- Sem cache nesta fase (vem na Phase 2)

**Commit:** `feat(repository): add ImageRepository with API mapping`

---

## Step 10 — `MainViewModel`

**Ficheiro:** `viewmodel/MainViewModel.kt`

**Conteúdo:**
- Construtor recebe `ImageRepository`
- Expõe `LiveData<UiState<List<ImageItem>>>`
- Método `fun loadImages(count: Int = 20)` que lança coroutine no `viewModelScope`

**Commit:** `feat(viewmodel): add MainViewModel with image loading`

---

## Step 11 — Layout `item_image.xml`

**Ficheiro:** `res/layout/item_image.xml`

**Conteúdo:** `CardView` com `ImageView` (altura ~200dp, scale type `centerCrop`), margens e cantos arredondados.

**Commit:** `feat(ui): add item_image layout for RecyclerView cells`

---

## Step 12 — `ImageAdapter`

**Ficheiro:** `ui/main/ImageAdapter.kt`

**Conteúdo:**
- `RecyclerView.Adapter<ImageAdapter.ViewHolder>`
- Usa `ListAdapter` + `DiffUtil` para performance
- Carrega imagens com Glide
- Sem click listeners ainda (vem na Phase 2)

**Commit:** `feat(ui): add ImageAdapter with DiffUtil and Glide`

---

## Step 13 — Layout `activity_main.xml`

**Ficheiro:** `res/layout/activity_main.xml`

**Conteúdo:**
- `CoordinatorLayout` root
- `MaterialToolbar` com título "CatsNDogs"
- `RecyclerView` com `LinearLayoutManager`
- `FloatingActionButton` com ícone refresh ancorado bottom|end

**Commit:** `feat(ui): add main activity layout`

---

## Step 14 — Strings, cores e ícones

**Ficheiros:** `res/values/strings.xml`, `res/values/colors.xml`, `res/drawable/ic_refresh.xml`

**Conteúdo:**
- Strings: `app_name`, `refresh`, `error_loading`, `retry`
- Cores: paleta da app
- Ícone refresh (Vector Asset)

**Commit:** `feat(resources): add strings, colors and icons`

---

## Step 15 — `MainActivity`

**Ficheiro:** `ui/main/MainActivity.kt`

**Conteúdo:**
- Inflate do layout
- Setup da Toolbar
- Setup do RecyclerView com `ImageAdapter`
- Inicializar `MainViewModel` com factory manual
- Observar `LiveData` e atualizar UI conforme `UiState`
- FAB → `viewModel.loadImages()`
- `onCreate` chama `loadImages()` se estado for inicial

**Commit:** `feat(ui): wire MainActivity with ViewModel and adapter`

---

## Step 16 — Build, deploy e teste manual

**Tarefas:**
- Build via AntiGravity
- Deploy no emulador (Pixel 3 AVD) via AntiGravity
- Testes manuais:
  - App arranca sem crash
  - Imagens carregam da API
  - FAB recarrega novas imagens
  - Sem rede → app não crasha (ainda sem fallback, mas erro deve ser silencioso ou em log)
- Capturar 2 screenshots (portrait + landscape) para o README futuro

**Sem commit de código** — apenas verificação. Se houver bugs, criar steps 16a, 16b, etc., para correções.

---

## Checkpoint Phase 1

Antes de avançar para Phase 2, garantir que:

- [ ] Todos os 15 steps estão em commits separados
- [ ] App corre estável no emulador
- [ ] `prompts_log.md` tem entrada para cada step
- [ ] Código segue arquitetura definida em `06_architecture.md`
- [ ] Nenhuma regra de `agents.md` foi violada

Só então criar `09_feature_extensions.md` e iniciar Phase 2.
