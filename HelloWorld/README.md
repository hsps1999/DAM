# HelloWorld – First Android App

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26  
**Prazo de Entrega:** 8 de março de 2026

---

## 1. Overview

Este repositório contém a **Parte 2 do Tutorial 1** da disciplina de DAM: a primeira aplicação Android nativa, desenvolvida em Android Studio com Kotlin e XML Views.

O projeto evoluiu em duas versões:
- **Hello World V1** — Configuração base, layout simples, extração de strings
- **Hello World V2** — Layout multi-view, imagem, calendário, variante landscape e ícone personalizado

---

## 2. System Architecture

**Linguagem:** Kotlin  
**Min SDK:** API 24 (Android 7.0 Nougat)  
**Build System:** Gradle (Kotlin DSL)  
**Package:** `dam_a46104.helloworld`  
**Layout Engine:** ConstraintLayout

```
HelloWorld/
└── app/src/main/
    ├── java/dam_a46104/helloworld/
    │   └── MainActivity.kt          → Activity principal
    ├── res/
    │   ├── layout/
    │   │   └── activity_main.xml    → Layout portrait
    │   ├── layout-land/
    │   │   └── activity_main.xml    → Layout landscape (variante)
    │   ├── drawable/
    │   │   └── cartoon_2.png        → Imagem personalizada
    │   ├── mipmap-*/                → Ícone personalizado (todas as densidades)
    │   └── values/
    │       ├── strings.xml          → Todos os textos externalizados
    │       ├── colors.xml           → Paleta de cores
    │       └── themes.xml           → Tema da aplicação
    └── AndroidManifest.xml
```

---

## 3. Implementation

### Hello World V1
- Projeto criado com template **Empty Views Activity**
- Texto "Hello Android World" extraído para `strings.xml` via `hello_string`
- Nome da app alterado para `"Hello World V1"` em `strings.xml`

### Hello World V2
- **1º TextView** (`tv_hello`): cor `@color/purple_500`, `24sp`, `bold`, constraint ao topo
- **2º TextView** (`tv_my_first_app`): `layout_width=0dp`, fundo `teal_200`, texto centrado
- **ImageView** (`img_smile`): imagem `cartoon_2.png`, `contentDescription="Just smile"` (acessibilidade obrigatória)
- **CalendarView** (`calendar_view`): debaixo da imagem, centrado
- **Layout Landscape**: variante em `res/layout-land/` para orientação horizontal
- **Ícone personalizado**: gerado via Image Asset Studio em todas as densidades `mipmap-*`
- Todos os textos externalizados para `strings.xml` (regra de ouro de internacionalização)

---

## 4. Testing

A aplicação foi testada num dispositivo físico via **USB Debugging (ADB)**. O build e deploy foram efetuados diretamente pelo Android Studio (botão Run ▶). Testada em modo portrait e landscape com rotação do dispositivo.

---

## 5. Prompting Strategy & AI Disclosure

> **Política aplicada:** `[AC YES, AI NO]` — O código XML e Kotlin foi escrito manualmente no Android Studio, usando apenas o autocompletar nativo. Nenhuma IA gerou código de UI ou lógica.

> **Ferramentas de IA usadas na documentação:** Antigravity (Google DeepMind) foi utilizado para auxiliar a geração deste relatório (`[AC YES, AI YES]`).
