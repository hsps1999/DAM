# Prompts Log

Registo dos prompts principais usados com o AntiGravity IDE durante o desenvolvimento. Atualizado após cada interação significativa com o agente.

Formato de cada entrada:

​```
## Prompt N — <título curto>

**Step do plano:** <número do step em 08_implementation_plan.md>
**Data:** YYYY-MM-DD

**Objetivo:**
<o que se queria obter>

**Prompt usado:**
<copiar prompt literal>

**Resultado:**
<descrição breve do código gerado e se foi aceite tal qual ou ajustado>

**Notas / problemas:**
<opcional — bugs encontrados, decisões tomadas>
​```

---

<!-- Adicionar novas entradas abaixo desta linha -->

## Prompt 1 — Bootstrap e validação do contexto

**Step do plano:** 0 (bootstrap, antes do Step 1)
**Data:** 2026-04-15

**Objetivo:**
Garantir que o agente lê toda a documentação do projeto (agents.md + docs/) e demonstra ter compreendido as regras, a arquitetura e o primeiro step antes de gerar qualquer código.

**Prompt usado:**
Antes de começarmos a implementar, preciso que faças o seguinte por esta ordem:

1. Lê o ficheiro `agents.md` na raiz do projeto.
2. Lê todos os ficheiros em `docs/` (são 9: 01 a 08, mais prompts_log).
3. Resume-me em 5-10 linhas:
   - Qual é o projeto
   - Qual é a arquitetura
   - Quais as regras a que estás vinculado
   - Qual é o Step 1 do plano de implementação

Não geres código nesta resposta. Só quero confirmar que leste e percebeste tudo antes de avançarmos. Lê literalmente os ficheiros, não inventes.

**Resultado:**
<Resumo cobriu os pontos pedidos e identificou corretamente o Step 1>

---

## Prompt 2 — Planeamento do Step 2 (dependências)

**Step do plano:** 2
**Data:** 2026-04-15

**Objetivo:**
Obter do agente o plano detalhado para o Step 2 (adicionar dependências de networking, lifecycle e image loading) antes de aplicar qualquer alteração — incluindo ficheiros a modificar, versões específicas das bibliotecas e localização da permissão INTERNET.

**Prompt usado:**
Vamos avançar para o Step 2 do `docs/08_implementation_plan.md`.

Antes de gerar qualquer alteração, confirma:
- Que ficheiro vais alterar
- Que dependências vais adicionar (com versões específicas)
- Onde vais adicionar a permissão INTERNET

Espera pela minha confirmação antes de aplicar mudanças.

**Resultado:**
<Ficheiros identificados: app/build.gradle.kts, AndroidManifest.xml>

---

## Prompt 3 — Refactor: mover MainActivity para ui/main

**Step do plano:** 15 (correção pós-implementação)
**Data:** 2026-04-16

**Objetivo:**
Corrigir um desvio identificado após a conclusão dos Steps 12-15: o agente deixou `MainActivity.kt` na raiz do package em vez de a colocar em `ui/main/`, conforme definido em `docs/06_architecture.md`. Esta entrada documenta o prompt de correção para alinhar a implementação com a arquitetura.

**Prompt usado:**
Move `MainActivity.kt` da raiz do package para `ui/main/`, conforme definido em docs/06_architecture.md. Atualiza o AndroidManifest.xml em conformidade. Confirma o plano antes de aplicar.

**Resultado:**
<Ficheiro movido, manifesto atualizado e app a compilar e a correr>

**Notas / problemas:**
Desvio detetado durante a revisão pós-Step 15. O agente sinalizou correctamente a discrepância no momento da geração ("ficou na raiz porque o manifesto referencia .MainActivity"), mas optou por não mover sem confirmação — comportamento alinhado com agents.md. Decisão: aplicar o move para manter consistência arquitetural com `ui/main/ImageAdapter.kt`.

---

## Prompt 4 — Extensão A: Loading Indicator (planeamento)

**Step do plano:** Extensão A (09_feature_extensions.md)
**Data:** 2026-04-21

**Objetivo:**
Obter plano detalhado do agente para a Extensão A (Loading Indicator) antes de gerar código — ficheiros a alterar, o que adicionar em cada um, e commits previstos.

**Prompt usado:**
Vamos iniciar a Phase 2. Antes de gerar código:

1. Lê `docs/09_feature_extensions.md`
2. Confirma que a Phase 1 está estável (os Steps 1-15 estão implementados)
3. Vamos começar pela Extensão A (Loading Indicator)

Confirma:
- Que ficheiros vais alterar
- O que vais adicionar em cada um
- Espera pela minha confirmação antes de aplicar

**Resultado:**
Agente identificou correctamente os 2 ficheiros (activity_main.xml e MainActivity.kt), detalhou as alterações exactas em cada um, respeitou a exclusividade mútua entre ProgressBar e RecyclerView, e manteve os commits alinhados com o plano. Aprovado sem alterações.

**Notas / problemas:**
Conversa anterior no AntiGravity perdeu-se — foi necessário iniciar nova conversa e re-contextualizar o agente com a Phase 2.

---

## Prompt 5 — Extensão A: Loading Indicator (aprovação e geração)

**Step do plano:** Extensão A (09_feature_extensions.md)
**Data:** 2026-04-21

**Objetivo:**
Autorizar o agente a gerar as alterações planeadas para a Extensão A (Loading Indicator) nos 2 ficheiros identificados.

**Prompt usado:**
Plano aprovado. Avança com o Commit 1 (activity_main.xml) e depois o Commit 2 (MainActivity.kt). Podes gerar os dois em sequência.

**Resultado:**
Agente gerou correctamente ambos os ficheiros. ProgressBar adicionada com visibility gone e behavior correcto. MainActivity atualizada com campo progressBar, método setupProgressBar(), método showLoading(Boolean) e observer de UiState completo com os 3 estados (Loading, Success, Error). Aceite sem alterações.

**Notas / problemas:**
O agente adicionou proactivamente um Snackbar no ramo UiState.Error — não estava previsto nesta extensão (é Extensão B), mas é inofensivo e alinha com a implementação futura. Decisão: manter, evita duplicar trabalho na Extensão B.

---

## Prompt 6 — Extensão B: Error Handling (planeamento)

**Step do plano:** Extensão B (09_feature_extensions.md)
**Data:** 2026-04-21

**Objetivo:**
Obter plano detalhado do agente para a Extensão B (Error Handling Gracioso) — ficheiros a alterar, lógica de try/catch tipado no Repository, mapeamento de exceções no ViewModel, e strings de erro.

**Prompt usado:**
Extensão A concluída e testada. Vamos à Extensão B (Error Handling).

Lê a Extensão B em docs/09_feature_extensions.md e confirma:
- Que ficheiros vais alterar ou criar
- O que muda em cada um
- Espera pela minha confirmação antes de aplicar

**Resultado:**
Agente diagnosticou corretamente o estado atual (Repository sem try/catch, ViewModel com catch genérico, MainActivity já com Snackbar funcional). Plano: 3 ficheiros, 3-4 commits. Identificou decisão técnica pendente — como o ViewModel acede a strings de erro — e propôs duas opções: (a) AndroidViewModel com Application context, (b) refactor de UiState.Error para usar @StringRes IDs. Decisão: opção (a), evita refactor desnecessário.

**Notas / problemas:**
O agente concluiu que MainActivity não precisa de alteração porque a Extensão A já adicionou Snackbar com Retry no ramo UiState.Error. Isto confirma que manter o Snackbar na Extensão A foi a decisão certa — poupou trabalho aqui.

---

## Prompt 7 — Extensão D: fix build error (kotlinOptions)

**Step do plano:** Extensão D (09_feature_extensions.md) — correção pós-geração
**Data:** 2026-04-21

**Objetivo:**
Corrigir erro de build "Unresolved reference 'kotlinOptions'" no app/build.gradle.kts após adição do plugin KSP e kotlin-android explícito.

**Prompt usado:**
Erro de build após Extensão D:

e: file:///C:/ISEL/ISEL_25_26/DAM/DAM_TP2/CatsNDogs/app/build.gradle.kts:38:5: Unresolved reference 'kotlinOptions'.

Mostra-me o conteúdo atual do app/build.gradle.kts e propõe a correção. Não apliques sem a minha confirmação.

**Resultado:**
Bloco kotlinOptions {} removido do app/build.gradle.kts. Causa: o plugin kotlin-android não está aplicado explicitamente no módulo (AGP 9.x aplica-o implicitamente), pelo que o DSL kotlinOptions não existe no contexto. Correção aplicada sem outras alterações.

**Notas / problemas:**
Erro previsto — KSP + kotlin-android explícito no AGP 9.x pode causar conflitos na configuração do Kotlin compiler. Confirma a nota no planeamento da Extensão D.

---

## Prompt 8 — Extensão F: fix findById fallback to Room

**Step do plano:** Extensão F (09_feature_extensions.md) — correção pós-implementação
**Data:** 2026-04-21

**Objetivo:**
Corrigir bug onde tocar num favorito após reiniciar a app dava "Erro inesperado" porque o findById() só consultava a lista em memória (vazia após restart), ignorando os dados persistidos em Room.

**Prompt usado:**
Bug encontrado na Extensão F:

Ao reabrir a app e tocar num favorito da barra, aparece "Erro inesperado ao carregar imagens" em vez dos detalhes.

Causa provável: o findById() no Repository procura na lista em memória (cachedImages), que está vazia após reiniciar a app. Os favoritos vêm do Room mas o findById() não consulta nem a tabela de cache nem a de favoritos.

Corrige o findById() para procurar nesta ordem:
1. Lista em memória (cachedImages)
2. Tabela cached_images (Room)
3. Tabela favorites (Room)

Se encontrar em qualquer uma, devolve o ImageItem. Se não encontrar em nenhuma, devolve null.

Confirma o plano antes de aplicar.

**Resultado:**
Agente propôs alterar 4 ficheiros: novas queries por id no CacheDao e FavoriteDao, findById() tornado suspend com fallback em cascata (memória → cache Room → favorites Room), e loadImage() no DetailsViewModel atualizado para coroutine. Plano aprovado sem alterações.

**Notas / problemas:**
Bug descoberto durante testes de persistência da Extensão F. Causa raiz: o cache em memória introduzido na Extensão C não sobrevive ao lifecycle da app. A correção alinha o findById() com a infraestrutura Room da Extensão D.

---

## Prompt 9 — README.md final

**Step do plano:** Checkpoint final Phase 2
**Data:** 2026-04-21

**Objetivo:**
Criar o README.md do projeto com descrição, funcionalidades, screenshots, instruções de execução e stack técnico — último deliverable antes da entrega.

**Prompt usado:**
Phase 2 completa. Cria o README.md na raiz do projeto conforme definido em docs/01_overview.md.

Inclui:
- Nome da app (CatsNDogs) e descrição curta
- API utilizada (Dog CEO)
- Funcionalidades implementadas (Phase 1 + Phase 2)
- Screenshots (referencia os ficheiros em docs/screenshots/ — eu adiciono as imagens depois)
- Instruções para correr o projeto (clone, abrir no Android Studio, min SDK 24, run)
- Stack técnico (Kotlin, XML Views, Retrofit, Room, Glide, MVVM)
- Estrutura do projeto (referencia docs/)

Espera pela minha confirmação antes de aplicar.

---