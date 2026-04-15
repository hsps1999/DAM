# 09 — Feature Extensions (Phase 2)

Extensões a implementar **após** a Phase 1 estar concluída, testada e em commit. Cada extensão segue o mesmo workflow disciplinado dos steps base: documentação → confirmação do agente → geração ficheiro a ficheiro → commit atómico.

A ordem abaixo está organizada da **menor para maior risco/complexidade**. Não saltar extensões. Não combinar extensões num só commit.

---

## Pré-requisitos

- [ ] Phase 1 completa (Steps 1-15 do plano base + refactor da MainActivity)
- [ ] App estável no emulador
- [ ] Screenshots capturados (`docs/screenshots/`)
- [ ] Checkpoint do `08_implementation_plan.md` validado

---

## Extensão A — Loading Indicator

**Descrição:**
Mostrar `ProgressBar` indeterminada centrada enquanto há um pedido à API em curso. Esconder quando os dados chegam ou quando há erro.

**Justificação:**
Feedback visual obrigatório segundo `03_screens.md` (estado "Loading"). Atualmente, o utilizador não sabe se a app está a carregar ou se está bloqueada.

**Tarefas:**
1. Adicionar `ProgressBar` em `activity_main.xml` (centrada, inicialmente `visibility="gone"`)
2. Atualizar `MainActivity` para observar `UiState.Loading` e alternar visibilidade
3. Garantir que ProgressBar e RecyclerView são mutuamente exclusivos
4. Testar: ver indicator durante o primeiro fetch e ao premir refresh

**Alterações de UI:** ProgressBar central no topo do RecyclerView (mesmo `CoordinatorLayout`).

**Commits:**
- `feat(ui): add progress bar to main layout`
- `feat(ui): toggle progress bar based on UiState in MainActivity`

---

## Extensão B — Error Handling Gracioso

**Descrição:**
Capturar falhas da API (sem rede, timeout, HTTP error, JSON inválido) e apresentar feedback via Snackbar com botão "Retry". Nunca crashar.

**Justificação:**
Requisito explícito do enunciado (secção 3.10.2). Fundação para a Extensão E (Offline Access).

**Tarefas:**
1. Envolver chamada Retrofit no `ImageRepository` em `try/catch` (capturar `IOException`, `HttpException`)
2. Mapear exceções para mensagens user-friendly em `strings.xml` (`error_no_network`, `error_server`, `error_unknown`)
3. `MainViewModel` propaga `UiState.Error(message)` em vez de deixar exceção subir
4. `MainActivity` observa `UiState.Error` e mostra `Snackbar` com action "Retry" → `viewModel.loadImages()`
5. Testar: WiFi off → tentar refresh → ver Snackbar → ligar WiFi → premir Retry → sucesso

**Alterações de UI:** Snackbar (componente Material já disponível).

**Commits:**
- `feat(repository): catch and rethrow API errors with typed exceptions`
- `feat(viewmodel): propagate errors via UiState.Error`
- `feat(ui): show snackbar with retry on error state`
- `feat(resources): add error message strings`

---

## Extensão C — Image Details Screen

**Descrição:**
Nova `Activity` ativada ao tocar numa imagem. Mostra a imagem em tamanho grande e metadados (raça, subraça, ID, URL).

**Justificação:**
Requisito do enunciado. Base para o toggle de favorito (Extensão F).

**Tarefas:**
1. Criar `ImageDetailsActivity.kt` em `ui/details/`
2. Criar layout `activity_image_details.xml` conforme `03_screens.md`
3. Criar `DetailsViewModel.kt` em `viewmodel/`
4. Adicionar entry no `AndroidManifest.xml` com `parentActivityName=".ui.main.MainActivity"`
5. Adicionar click listener no `ImageAdapter` (callback `(ImageItem) -> Unit` no construtor)
6. Em `MainActivity`, no callback do adapter: lançar `Intent` com `imageId` como extra
7. Em `ImageDetailsActivity`, ler o extra e pedir ao `DetailsViewModel` para resolver o item
8. Testar: tocar numa imagem → abre details → back → volta a Main com estado preservado

**Alterações de UI:** Nova Activity com toolbar (back arrow), imagem grande, painel de metadados.

**Decisão técnica pendente:**
Como é que o `DetailsViewModel` resolve um `ImageItem` a partir do `id`? Opções:
- (a) Passar o `ImageItem` inteiro como `Parcelable` no Intent (mais simples, mas acopla)
- (b) Manter o último resultado do Repository em memória e queriar por id (mais limpo, prepara cache)

**Recomendação:** (b), porque alinha com a Extensão D (cache).

**Commits:**
- `feat(ui): add ImageDetailsActivity layout`
- `feat(viewmodel): add DetailsViewModel`
- `feat(ui): add ImageDetailsActivity with metadata display`
- `feat(ui): wire item click in ImageAdapter to launch details`
- `chore(manifest): register ImageDetailsActivity with parent`

---

## Extensão D — Cache de até 50 itens

**Descrição:**
Persistir até 50 imagens em Room. Política LRU: ao inserir o 51.º, remover o de `cachedAt` mais antigo. Favoritos não contam para o limite.

**Justificação:**
Pré-requisito para Extensão E (offline) e para a regra de "10 antes / 10 depois" durante navegação.

**Tarefas:**
1. Adicionar dependências Room no `build.gradle.kts` (`room-runtime`, `room-ktx`, `room-compiler` via `ksp`)
2. Configurar plugin KSP no projeto
3. Criar `CachedImage.kt` (entity Room) em `data/local/`
4. Criar `CacheDao.kt` com `@Insert`, `@Query("SELECT * ORDER BY cachedAt DESC LIMIT 50")`, `@Query("DELETE WHERE id NOT IN (...)")` para enforcement do limite
5. Criar `AppDatabase.kt` (RoomDatabase abstract com singleton via companion object)
6. Inicializar `AppDatabase` na `Application` class (`CatsNDogsApp.kt`) — criar esta classe agora
7. Registar `CatsNDogsApp` no manifesto (`android:name`)
8. Atualizar `ImageRepository`: após cada fetch, persistir resultado e fazer pruning para 50 itens
9. Adicionar método `getCachedImages(): List<ImageItem>` ao Repository
10. Testar: fetch → fechar app → reabrir → confirmar via Logcat que cache tem itens

**Alterações de UI:** nenhuma direta nesta extensão (a Extensão E é que vai usar o cache visualmente).

**Commits:**
- `chore: add Room and KSP dependencies`
- `feat(local): add CachedImage entity`
- `feat(local): add CacheDao with LRU pruning`
- `feat(local): add AppDatabase singleton`
- `feat(app): add Application class and register in manifest`
- `feat(repository): persist fetched images to cache`

---

## Extensão E — Acesso Offline

**Descrição:**
Quando o fetch da API falha (sem rede), o Repository devolve os itens em cache em vez de propagar erro. Combinar com Extensão B: erro só aparece se cache também estiver vazia.

**Justificação:**
Requisito do enunciado. Melhora drasticamente a experiência em modo offline.

**Tarefas:**
1. Atualizar lógica do `ImageRepository.fetchRandomImages()`:
   - Tentar API
   - Em `IOException` ou similar: ler cache; se cache vazia, relançar exceção
   - Em sucesso: persistir e devolver
2. Adicionar flag `isFromCache: Boolean` ao resultado (ou novo `UiState.SuccessFromCache`)
3. Mostrar Snackbar informativa "A mostrar conteúdo offline" quando vier do cache
4. Testar: WiFi off → app mostra cache + Snackbar; cache vazia + WiFi off → erro normal

**Alterações de UI:** Snackbar adicional (informativa, não erro).

**Commits:**
- `feat(repository): fallback to cache when API fails`
- `feat(ui): show offline indicator when displaying cached content`

---

## Extensão F — Favoritos FIFO (máx. 5)

**Descrição:**
Utilizador marca/desmarca uma imagem como favorita no `ImageDetailsActivity`. Lista de favoritos é persistida em Room. Ao adicionar o 6.º, o mais antigo é removido. Os 5 favoritos atuais são acessíveis a partir de qualquer ecrã via barra horizontal de miniaturas.

**Justificação:**
Requisito do enunciado. Funcionalidade mais visível para o utilizador final.

**Tarefas:**
1. Criar `FavoriteEntry.kt` (entity Room) e `FavoriteDao.kt` (com query `INSERT` + lógica FIFO no DAO ou no Repository)
2. Atualizar `AppDatabase` para incluir `FavoriteDao`
3. Adicionar métodos ao `ImageRepository`:
   - `toggleFavorite(item: ImageItem)`
   - `getFavorites(): LiveData<List<ImageItem>>`
   - `isFavorite(id: String): LiveData<Boolean>`
4. Adicionar botão favorito (toggle estrela cheia/vazia) em `activity_image_details.xml`
5. `DetailsViewModel` expõe `isFavorite: LiveData<Boolean>` e método `toggleFavorite()`
6. `ImageDetailsActivity` observa estado e atualiza ícone
7. Criar layout partilhado `view_favorites_bar.xml` (LinearLayout horizontal com 5 ImageView circulares)
8. Incluir `view_favorites_bar.xml` em `activity_main.xml` e `activity_image_details.xml` via `<include>`
9. Criar helper `FavoritesBarController` que carrega miniaturas via Glide e regista click listeners (lança Details com o id correspondente)
10. Testar: marcar 6 imagens como favoritas → confirmar que a 1.ª desaparece; tocar em miniatura → abre details certo; rodar ecrã → barra mantém-se

**Alterações de UI:** Botão favorito no Details, FavoritesBar fixa em todos os ecrãs.

**Decisão técnica pendente:**
A FavoritesBar deve estar em `Toolbar` ou em `BottomAppBar`? Recomendação: barra horizontal entre a Toolbar e o conteúdo principal — mais visível e não compete com o FAB de refresh.

**Commits:**
- `feat(local): add FavoriteEntry and FavoriteDao`
- `feat(repository): add favorites management with FIFO policy`
- `feat(ui): add favorite toggle button in details screen`
- `feat(viewmodel): expose favorite state in DetailsViewModel`
- `feat(ui): add favorites bar layout`
- `feat(ui): wire FavoritesBarController in MainActivity and DetailsActivity`

---

## Checkpoint final da Phase 2

Antes de gerar o README final e capturar screenshots definitivos:

- [ ] Todas as 6 extensões implementadas e testadas
- [ ] Cada extensão em commits atómicos (não combinar A com B, etc.)
- [ ] `prompts_log.md` atualizado com prompts de cada extensão
- [ ] Sem regressões: funcionalidades base da Phase 1 continuam a funcionar
- [ ] Testes manuais cobrem caminhos felizes E falhas (WiFi off, cache vazia, 6.º favorito, etc.)
- [ ] Screenshots atualizados (Main, Details, Favoritos cheios, Snackbar de erro, modo offline)

Após este checkpoint:
- Atualizar `README.md` com descrição final, screenshots e instruções
- Commit final: `docs: final README with screenshots and project notes`
