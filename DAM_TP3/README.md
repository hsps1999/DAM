# ISEL — Desenvolvimento de Aplicações Móveis — Trabalho Prático 3

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26

---

## 1. Overview

Pasta com todos os componentes desenvolvidos para o **Trabalho Prático 3 (TP3)** da disciplina de DAM. O TP3 abrange processamento de anotações com KSP, desenvolvimento de aplicações Android com Jetpack Compose e a evolução multi-módulo da app CatsNDogs (MIP-3).

---

## 2. Estrutura

```
DAM_TP3/
├── GreetingProcessorProject/ → Processador de anotações KSP (Maven + IntelliJ IDEA)
├── JetpackWeatherApp/        → App Android de meteorologia com Jetpack Compose
└── CatsNDogs-MIP3/           → App Android multi-módulo XML Views + Compose (MIP-3)
```

---

## 3. Componentes

### ⚙️ [GreetingProcessorProject](GreetingProcessorProject)
Projeto de processamento de anotações em tempo de compilação com **KSP (Kotlin Symbol Processing)**:

| Módulo | Descrição |
|---|---|
| `annotations` | Definição das anotações `@Greeting` e `@Extract` |
| `processor` | `BaseProcessor` (abstrato), `GreetingProcessor` e `RegexProcessor` (KSP) |
| `app` | Classes de exemplo que usam as anotações |

### 📱 [JetpackWeatherApp](JetpackWeatherApp)
Reescrita da CoolWeatherApp (TP2) com tecnologias modernas:

- UI totalmente em **Jetpack Compose** com layouts adaptativos (portrait e landscape)
- Cliente HTTP assíncrono com **Ktor** (coroutines, sem `Thread` manual)
- **StateFlow** para reatividade entre ViewModel e UI
- Métricas adicionais: sensação térmica, humidade, visibilidade e índice UV
- Localizações favoritas persistidas em **SharedPreferences** (JSON)
- Fundo gradiente dinâmico e partículas animadas (chuva, neve) por condição meteorológica

### 📱 [CatsNDogs-MIP3](CatsNDogs-MIP3)
Evolução multi-módulo da app CatsNDogs (MIP-3), com duas implementações de UI partilhando a mesma camada de dados:

- **app-xml** — UI em Views tradicional (RecyclerView, XML layouts)
- **app-compose** — UI em Jetpack Compose com animações de lista e toggle de favorito
- Arquitetura MVVM + Repository com módulos `core`, `data` e `domain`
- Animações de conteúdo de lista, colocação de itens e indicador de loading

---

## 4. Tecnologias

| Componente | Tecnologias |
|---|---|
| **GreetingProcessorProject** | Kotlin, KSP, Apache Maven, IntelliJ IDEA |
| **JetpackWeatherApp** | Kotlin, Android Studio, Jetpack Compose, Ktor, kotlinx.serialization, StateFlow, Open-Meteo API |
| **CatsNDogs-MIP3** | Kotlin, Android Studio, Jetpack Compose, Retrofit, Room, Coroutines, Glide, Coil, Dog CEO API |
