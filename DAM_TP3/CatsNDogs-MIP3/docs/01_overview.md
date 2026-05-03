# 01 — Overview

## Propósito

Aplicação Android que recolhe imagens de cães a partir de uma API pública (Dog CEO API) e as apresenta numa interface construída com XML Views. O projeto serve como exercício prático de desenvolvimento assistido por IA (AntiGravity IDE), seguindo uma abordagem **planning-first** baseada em documentação Markdown.

## Utilizadores-alvo

Utilizadores casuais que pretendem navegar por imagens aleatórias de cães, marcar favoritos e consultar detalhes adicionais sobre cada imagem. Em termos pedagógicos, o público real é o próprio aluno e o docente avaliador.

## Ideia geral de funcionamento

A aplicação arranca na `MainActivity`, faz um pedido HTTP à Dog CEO API e apresenta as imagens recebidas numa `RecyclerView`. O utilizador pode:

- Atualizar a lista (botão refresh ou swipe)
- Tocar numa imagem para ver detalhes
- Marcar imagens como favoritas (até 5, FIFO)
- Aceder à galeria de favoritos a partir de qualquer ecrã
- Consultar imagens em modo offline (a partir de cache local)

A arquitetura segue o padrão **MVVM**, com separação clara entre UI, lógica de negócio e acesso a dados.

## Stack técnico

- **Linguagem:** Kotlin
- **UI:** XML Views (sem Jetpack Compose)
- **Networking:** Retrofit + Gson
- **Concorrência:** Kotlin Coroutines
- **Carregamento de imagens:** Glide
- **Persistência:** Room (cache + favoritos)
- **Arquitetura:** MVVM com Repository pattern
