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
