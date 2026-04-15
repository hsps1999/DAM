# AI Agent Guidelines

Estas regras governam o comportamento do agente de IA do AntiGravity IDE durante o desenvolvimento deste projeto. O agente deve ler este ficheiro **antes de qualquer geração de código** e referenciá-lo em caso de dúvida.

## Princípio fundamental

**Planning-first.** Nenhum código é gerado sem que exista, em `/docs`, especificação que o sustente. Se a especificação for ambígua ou incompleta, o agente **pergunta** em vez de improvisar.

## Workflow obrigatório

1. Ler `/docs/06_architecture.md` para entender as camadas e estrutura de pastas.
2. Ler `/docs/08_implementation_plan.md` para identificar o **step atual**.
3. Implementar **apenas o step pedido** — não antecipar steps futuros.
4. Após cada step, parar e aguardar validação humana antes de continuar.

## Regras de geração de código

### Linguagem e UI
- **Kotlin** apenas. Sem Java.
- **XML Views** para UI. **Não usar Jetpack Compose** sob nenhuma circunstância.
- Material Components 3 quando aplicável.

### Granularidade
- **Um ficheiro por geração.** Não criar múltiplos ficheiros numa só resposta.
- Se um step do plano implicar vários ficheiros, gerar um de cada vez e aguardar confirmação.

### Dependências
- **Não adicionar bibliotecas sem perguntar.** Stack aprovada: Retrofit, Gson, Coroutines, Glide, Lifecycle, RecyclerView, Material, Room (Phase 2).
- Qualquer outra dependência exige justificação e aprovação explícita.

### Estilo
- Seguir convenções Kotlin oficiais (kotlinlang.org/docs/coding-conventions.html).
- Usar `val` por defeito; `var` só quando necessário.
- Funções `suspend` para operações de I/O.
- Nunca chamar API ou DB no UI thread.
- Comentários KDoc em classes públicas e funções não triviais.

### Arquitetura
- Respeitar a estrutura de pastas definida em `06_architecture.md`.
- ViewModel **não** importa nada de `android.view`, `android.widget` ou similar.
- Activity **não** chama Repository diretamente — só via ViewModel.
- Repository é a única classe que conhece API e DB.

### Tratamento de erros
- Toda chamada de rede dentro de `try/catch`.
- Erros propagados via `UiState.Error`, nunca via exceções não capturadas.
- Sem `!!` (non-null assertion) exceto em casos justificados por comentário.

## O que o agente NÃO deve fazer

- Gerar código sem ler `/docs` primeiro.
- Adicionar funcionalidades não pedidas ("achei que ias precisar de...").
- Inventar endpoints da API. Se não estiver em `07_api_usage.md`, perguntar.
- Refactor não solicitado de código já existente.
- Misturar Phase 1 e Phase 2 — features de Phase 2 só após o plano base estar concluído e em commit.
- Gerar testes unitários sem serem pedidos (são opcionais neste projeto).
- Inicializar Git, fazer commits ou push — isso é responsabilidade do humano.

## Logging

Após cada interação significativa, o humano regista o prompt em `/docs/prompts_log.md` com:
- Objetivo
- Prompt utilizado
- Descrição breve do resultado

O agente pode lembrar o humano se este se esquecer.

## Validação

O humano é responsável por:
- Rever todo o código gerado antes de aceitar.
- Testar cada step antes de avançar.
- Decidir quando um step está concluído.

O agente é uma ferramenta de apoio, **não substitui** o juízo do programador.
