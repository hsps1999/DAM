# Prompts Log — MIP-3

Registo dos prompts principais usados com o AntiGravity IDE durante a fase MIP-3 do projeto CatsNDogs. Atualizado após cada interação significativa com o agente.

Formato de cada entrada:

```
## Prompt N — <título curto>

**Step do plano:** <referência a 04_implementation_plan.md, ex: M2.1>
**Data:** YYYY-MM-DD

**Objetivo:**
<o que se queria obter>

**Prompt usado:**
<copiar prompt literal>

**Resultado:**
<descrição breve do código gerado e se foi aceite tal qual ou ajustado>

**Notas / problemas:**
<opcional — bugs encontrados, decisões tomadas>
```

---

## Prompt 1 — Bootstrap e validação do contexto MIP-3

**Step do plano:** 0 (bootstrap, antes do M2.1)
**Data:** 2026-05-03

**Objetivo:**
Garantir que o agente lê toda a documentação MIP-3 (agents.md + CONTEXT.md + docs/MIP3/) e demonstra ter compreendido as regras, a arquitetura multi-módulo e o primeiro step antes de gerar qualquer código.

**Prompt usado:**
Antes de começarmos a implementar o MIP-3, preciso que faças o seguinte por esta ordem:

1. Lê o ficheiro `agents.md` na raiz do projeto.
2. Lê o ficheiro `CONTEXT.md` na raiz do projeto.
3. Lê todos os ficheiros em `docs/MIP3/` (são 6: 00, 01, 02, 03, 04, e prompts_log).
4. Resume-me em 8-12 linhas:
   - O que é este projeto (MIP-3) e como se distingue do MIP-2
   - Qual é a arquitetura alvo (3 módulos, dependências)
   - Que tipo de feature exclusiva tem o `:app-compose`
   - As 3-5 regras mais importantes a que estás vinculado pelo `agents.md`
   - Qual é o Step M2.1 do plano de implementação (o primeiro)
   - Que decisões técnicas globais devo respeitar (versões fixadas, AGP 9.x)

Não geres código nesta resposta. Não toques em ficheiros.
Só quero confirmar que leste e percebeste tudo antes de avançarmos.
Lê literalmente os ficheiros, não inventes conteúdo.

**Resultado:**
O agente fez ligação explícita ao Prompt 7 do MIP-2 — confirma que tem
visibilidade do histórico anterior. Útil para detetar regressões conhecidas.

---

## Prompt 2 — M2.1: Criar módulo :core

**Step do plano:** M2.1
**Data:** 2026-05-03

**Objetivo:**
Criar módulo Gradle :core (Android Library) com build.gradle.kts, AndroidManifest mínimo, e registo em settings.gradle.kts. Validar build standalone.

**Prompt usado:**
Antes de gerar qualquer alteração, confirma:

1. Que ficheiros vais criar ou alterar
2. Conteúdo exato do `core/build.gradle.kts`:
   - Que plugins vais incluir (e quais NÃO vais incluir, à luz da regra AGP 9.x)
   - Que dependências vais declarar e que aliases do version catalog vais usar
   - Se vais adicionar entradas novas ao `gradle/libs.versions.toml` (e quais)
3. Que alteração vais fazer ao `settings.gradle.kts`
4. Como vais validar que o módulo compila standalone

Espera pela minha confirmação antes de aplicar mudanças.

**Resultado:**
android-library adicionado ao libs.versions.toml. core/build.gradle.kts criado com namespace, compileSdk, minSdk 24, schemaDirectory, dependências Retrofit/Room/Coroutines/Lifecycle. AndroidManifest.xml mínimo. settings.gradle.kts atualizado. Build OK (60 tasks).

**Notas / problemas:**
Bug AGP 9.x descoberto: alias() em submodules para plugins do classpath falha com "plugin already on classpath with unknown version". Solução: id("com.android.library") direto sem alias(). Regra adicionada ao agents.md para os próximos módulos (:app-xml, :app-compose).

---

## Prompt 3 — M2.2: Mover modelos e DTOs

**Step do plano:** M2.2
**Data:** 2026-05-03

**Objetivo:**
Mover ImageItem.kt e DogApiResponse.kt de :app para :core, atualizando package declaration.

**Prompt usado:**
Vamos avançar para o Step M2.2 do `docs/MIP3/04_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:

1. Que ficheiros vais mover e para onde (paths exatos)
2. Que mudanças vais fazer ao package declaration de cada ficheiro
3. Que efeitos colaterais esperas (build do :app vai falhar com imports
   partidos? confirmas que isso é esperado e não bloqueia o commit?)
4. Vais tocar em algum ficheiro além dos 2 .kt a mover?

Espera pela minha confirmação antes de aplicar mudanças.

**Resultado:**
Ambos os ficheiros movidos com package core.model. Git detectou rename (95% e 89% similaridade). :core:build OK. :app:assembleDebug falha em 4 ficheiros (esperado — resolvido em M3).

**Notas / problemas:**
Nenhum.

---

## Prompt 4 — M2.3: Mover camada remota

**Step do plano:** M2.3
**Data:** 2026-05-03

**Objetivo:**
Mover DogApiService.kt e RetrofitClient.kt para :core/remote/, atualizando package e imports.

**Prompt usado:**
Vamos avançar para o Step M2.3 do `docs/MIP3/04_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:

1. Que ficheiros vais mover e paths exatos
2. Mudanças ao package declaration
3. Mudanças aos imports dentro dos ficheiros (RetrofitClient deve
   importar DogApiService — confirma se o import precisa de ajuste)
4. Outros ficheiros tocados

Espera pela minha confirmação antes de aplicar.

Após aplicar:
1. `./gradlew :core:build` verde
2. `:app:assembleDebug` continua a falhar (esperado, mais imports partidos)
3. Commit: `refactor(core): move Retrofit client and service to :core`

**Resultado:**
Ambos movidos. DogApiService com import DogApiResponse atualizado para core.model. RetrofitClient sem imports de projeto. :core:build OK. :app:assembleDebug falha em mais ficheiros (esperado).

**Notas / problemas:**
Nenhum.

---

## Prompt 5 — M2.4: Mover camada local Room

**Step do plano:** M2.4
**Data:** 2026-05-03

**Objetivo:**
Mover 5 ficheiros Room (entities, DAOs, AppDatabase) para :core/local/ com package e imports atualizados.

**Prompt usado:**
Vamos avançar para o Step M2.4 do `docs/MIP3/04_implementation_plan.md`.

Este step move 5 ficheiros interligados (entities, DAOs, AppDatabase).
Antes de aplicar, confirma:

1. Os 5 ficheiros e paths exatos (origem → destino)
2. Mudanças ao package declaration de cada um
3. **Mapa de imports cruzados** entre os 5 ficheiros:
   - CacheDao usa CachedImage?
   - FavoriteDao usa FavoriteEntry?
   - AppDatabase usa todas as entities + DAOs?
   - Lista cada import que precisa de ser atualizado e onde
4. Confirmação que o `schemaDirectory` configurado no M2.1 cobre os
   schemas que vão ser gerados aqui
5. Se algum ficheiro tem imports do `model/` (ImageItem) que precisem
   de update para core.model
6. Outros ficheiros tocados

Espera pela minha confirmação antes de aplicar.

Após aplicar:
1. `./gradlew :core:build` verde — Room deve gerar código sem erros
2. `:app:assembleDebug` continua a falhar (esperado)
3. Commit: `refactor(core): move Room database, entities and DAOs to :core`

**Resultado:**
5 ficheiros movidos. Imports core.model.ImageItem atualizados em CachedImage e FavoriteEntry. KSP processou sem erros. FavoriteDao mantém LiveData (conversão reservada para M2.7). :core:build OK.

**Notas / problemas:**
copyRoomSchemas NO-SOURCE (esperado — exportSchema=false).

---

## Prompt 6 — M2.5: Mover UiState e converter para @StringRes

**Step do plano:** M2.5
**Data:** 2026-05-03

**Objetivo:**
Mover UiState.kt para :core/common/ e alterar UiState.Error para receber @StringRes em vez de String.

**Prompt usado:**
Vamos avançar para o Step M2.5 do `docs/MIP3/04_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:

1. Ficheiro a mover: `app/src/main/java/.../ui/common/UiState.kt` → `core/src/main/java/.../core/common/UiState.kt`
2. Mudança de package: `ui.common` → `core.common`
3. Mudança de assinatura:
   - `data class Error(val message: String)` → `data class Error(@StringRes val messageResId: Int)`
   - `Success` mantém-se igual
4. Se `Success` tem campos extras (como `isFromCache` da Extensão E) — confirma quais e mantém
5. Outros ficheiros tocados

Espera pela minha confirmação antes de aplicar.

Após aplicar:
1. `./gradlew :core:build` verde
2. `:app:assembleDebug` continua a falhar (esperado)
3. Commit: `refactor(core): move UiState to :core and use StringRes for errors`

**Resultado:**
Ficheiro movido e editado conforme especificado. isFromCache preservado no Success. :core:build OK. :app:assembleDebug falha (esperado).

**Notas / problemas:**
Nenhum.

---

## Prompt 7 — M2.6: Mover strings de erro para resources do :core

**Step do plano:** M2.6
**Data:** 2026-05-03

**Objetivo:**
Criar `core/src/main/res/values/strings.xml` e `core/src/main/res/values-pt/strings.xml` com as strings de erro e offline, e remover estas strings do :app.

**Prompt usado:**
Vamos avançar para o Step M2.6 do `docs/MIP3/04_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:

1. Que recursos vais criar no :core:
   - `core/src/main/res/values/strings.xml`
   - `core/src/main/res/values-pt/strings.xml`
2. Conteúdo exato de cada um:
   - error_no_network
   - error_server
   - error_unknown
   - info_offline_cache
   - traduções em -pt
3. Que ficheiros vais editar ou remover no :app (as strings a apagar)
4. Confirmação que o resource merging vai expor estas strings a quem
   depender de :core (via R.string.xxx)

Espera pela minha confirmação antes de aplicar.

Após aplicar:
1. `./gradlew :core:build` verde
2. `:app:assembleDebug` continua a falhar (esperado)
3. Commit: `refactor(core): move error and offline strings to :core resources`

**Resultado:**
Recursos criados no :core com traduções. Strings apagadas do :app. :core:build OK. :app falha (esperado).

**Notas / problemas:**
Nenhum.

---

## Prompt 8 — M2.7: Mover Repository e converter LiveData para Flow

**Step do plano:** M2.7
**Data:** 2026-05-03

**Objetivo:**
Mover ImageRepository.kt para :core/repository/ e converter todas as suas LiveData para Flow.

**Prompt usado:**
Vamos avançar para o Step M2.7 do `docs/MIP3/04_implementation_plan.md`.

Este step muda assinatura do Repository, converte LiveData→Flow nos DAOs
e mapeia exceções para UiState.Error com @StringRes. É o step mais
complexo do M2. Antes de aplicar, confirma em detalhe:

1. Ficheiros a mover/modificar (paths exatos)
2. FavoriteDao: que métodos devolvem LiveData hoje? Qual o plano de
   conversão para Flow? Há métodos que devem manter-se síncronos
   (não-Flow)?
3. CacheDao: alguma alteração necessária?
4. ImageRepository — assinaturas novas:
   - getFavorites(): Flow<List<ImageItem>> (era LiveData)
   - isFavorite(id): Flow<Boolean> (era LiveData)
   - fetchRandomImages(count): UiState<List<ImageItem>> (era
     Pair<List<ImageItem>, Boolean> + lançava exceção)
   - findById(id): mantém suspend, sem mudanças
   - toggleFavorite(item): mantém suspend, sem mudanças
   Confirma todas estas assinaturas e mostra-me o esqueleto do
   try/catch dentro de fetchRandomImages com o mapeamento:
   - sucesso → UiState.Success(list, isFromCache=false)
   - IOException + cache não vazia → UiState.Success(cached, isFromCache=true)
   - IOException + cache vazia → UiState.Error(R.string.error_no_network)
   - HttpException → UiState.Error(R.string.error_server)
   - outras → UiState.Error(R.string.error_unknown)
5. Confirma que :app vai ter mais imports partidos (esperado, M3 corrige)

Espera pela minha confirmação antes de aplicar.

**Resultado:**
FavoriteDao actualizado: getAll() e isFavorite() convertidos de LiveData para Flow (CacheDao não precisou de alterações). ImageRepository criado em :core/repository/ com: fetchRandomImages a devolver UiState em vez de lançar exceções; getFavorites() e isFavorite() a devolver Flow; import dam_a46104.catsndogs.core.R para R.string.*. :core:build BUILD SUCCESSFUL. :app:assembleDebug falha (esperado — imports partidos nos ViewModels e Activities, resolvidos no M3).

**Notas / problemas:**
Warning KT-73255 sobre @StringRes em data class mantém-se (não-blocking, registado desde M2.5). getCachedImages() foi tornada private no novo Repository (era pública no :app — não há usos externos agora que está em :core).

---

