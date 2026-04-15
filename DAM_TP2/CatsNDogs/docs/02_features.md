# 02 — Features

## Phase 1 — Funcionalidades base (plano inicial)

1. **Obter imagens da API**
   Pedido HTTP à Dog CEO API para obter URLs de imagens aleatórias de cães.

2. **Apresentar imagens em lista**
   `RecyclerView` vertical com cada imagem dentro de um `CardView`.

3. **Atualizar lista**
   Botão de refresh (FAB ou na toolbar) que dispara novo pedido à API e substitui o conteúdo atual.

4. **Permissão de Internet**
   Declarada no `AndroidManifest.xml`.

## Phase 2 — Extensões pós-plano (ver `09_feature_extensions.md`)

As funcionalidades abaixo só são implementadas **depois** do plano base estar concluído, testado e em commit. Cada uma é adicionada formalmente ao `09_feature_extensions.md` antes de qualquer geração de código.

5. **Padrão MVVM**
   Refactor (se necessário) para garantir separação UI / ViewModel / Repository / API Service.

6. **Indicador de loading**
   `ProgressBar` visível enquanto há pedidos em curso, relativo às imagens efetivamente em carregamento.

7. **Ecrã de detalhes da imagem**
   Nova `Activity` com imagem em tamanho maior e metadados (raça, ID, URL).

8. **Favoritos (FIFO, máx. 5)**
   Marcar imagens como favoritas. Quando a lista ultrapassa 5, o item mais antigo é removido. Acesso direto aos 5 favoritos a partir de qualquer ecrã (barra ou menu lateral com miniaturas).

9. **Cache de até 50 itens**
   Mantém pelo menos 10 itens à frente e 10 atrás da posição atual durante navegação. Favoritos não contam para o limite de 50.

10. **Acesso offline**
    Quando não há rede, a app apresenta os itens em cache (incluindo favoritos) sem rebentar.

11. **Tratamento gracioso de erros da API**
    Falhas de rede, respostas inválidas e timeouts não devem crashar a app — devem mostrar feedback (Snackbar ou estado vazio).
