# DAM_TP1 – Getting Started with Kotlin

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26  
**Prazo de Entrega:** 8 de março de 2026

---

## 1. Overview

Este repositório contém o Trabalho Prático 1 (TP1) da disciplina de DAM, especificamente a **Parte 1: Getting Started with Kotlin**, desenvolvido no IntelliJ IDEA.

O objetivo desta fase é dominar a sintaxe e os paradigmas idiomáticos de Kotlin, nomeadamente a imutabilidade, expressões funcionais e Programação Orientada a Objetos.

---

## 2. System Architecture

**Build Tool:** Apache Maven  
**Estrutura de Código:** `src/main/kotlin/`

```
DAM_TP1/
├── src/
│   └── main/
│       └── kotlin/
│           └── dam/
│               ├── exer_1/            → Arrays e quadrados perfeitos
│               ├── exer_2/            → Calculadora de consola
│               ├── exer_3/            → Sequência funcional (generateSequence)
│               └── virtual_library/   → OOP: Book, DigitalBook, PhysicalBook, Library
└── pom.xml
```

---

## 3. Implementation

### Exercício 1 — Arrays
Geração dos primeiros 50 quadrados perfeitos usando 3 abordagens:
- Construtor `IntArray(50) { ... }`
- Range com `.map { it * it }`
- Construtor genérico `Array(50) { ... }`

### Exercício 2 — Calculadora de Consola
Calculadora interativa com:
- Expressão `when` para routing de operações (aritméticas, booleanas e bitwise `shl`/`shr`)
- String templates para output formatado
- Tratamento de exceções (divisão por zero)

### Exercício 3 — Programação Funcional
Simulação de ressaltos de uma bola (altura inicial: 100m, fator: 0.6) usando:
- `generateSequence(100.0) { it * 0.6 }` — sequência lazy infinita
- `.filter { it >= 1.0 }` — apenas ressaltos ≥ 1 metro
- `.take(15)` — primeiros 15 ressaltos válidos
- Output formatado a 2 casas decimais

### Secção 6 — Virtual Library (OOP)
Hierarquia de classes para uma biblioteca virtual:
- `Book` — classe base com getters/setters customizados (impede cópias negativas), bloco `init` de validação e `companion object` com factory method
- `DigitalBook` — subclasse com formato e tamanho em MB
- `PhysicalBook` — subclasse com número de páginas e peso
- `Library` — Singleton implementado com `object`, com operações de add, remove, search, lend e return
- `data class` — `SearchResult` com `equals()`, `hashCode()`, `copy()` automáticos

---

## 4. Testing

Cada exercício é executado individualmente pelo botão de Run (▶) no IntelliJ IDEA, ao lado de cada `fun main()`. O output é validado visualmente na consola.

---

## 5. Prompting Strategy & AI Disclosure

> **Política aplicada:** `[AC NO, AI NO]` — Esta secção foi desenvolvida sem recurso a IA generativa ou autocompletar avançado. Todo o código foi escrito manualmente para consolidação da sintaxe Kotlin.

> **Ferramentas de IA usadas na documentação:** Antigravity (Google DeepMind) foi utilizado para auxiliar a geração deste relatório (`[AC YES, AI YES]`).
