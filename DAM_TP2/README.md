# ISEL — Desenvolvimento de Aplicações Móveis — Trabalho Prático 2

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26

---

## 1. Overview

Pasta com todos os componentes desenvolvidos para o **Trabalho Prático 2 (TP2)** da disciplina de DAM. O TP2 divide-se em dois blocos: exercícios de Kotlin avançado e o desenvolvimento de uma aplicação Android de meteorologia.

---

## 2. Estrutura

```
DAM_TP2/
├── Kotlin_TP2/     → Exercícios de Kotlin avançado (Maven + IntelliJ IDEA)
└── CoolWeatherApp/ → Aplicação Android de previsão meteorológica (Android Studio)
```

---

## 3. Componentes

### 🔷 [Kotlin_TP2](Kotlin_TP2)
Exercícios de sintaxe e paradigmas avançados de Kotlin:

| Exercício | Descrição |
|---|---|
| **1.1 — Event Log Processing** | Processamento de logs com sequências lazy e higher-order functions |
| **1.2 — Generics: In-Memory Cache** | Cache em memória genérica com TTL e política de evicção |
| **1.3 — Functions and Lambdas** | Pipeline de dados configurável com funções de ordem superior |
| **1.4 — Operator Overloading** | Sobrecarga de operadores em classes Kotlin |

### 📱 [CoolWeatherApp](CoolWeatherApp)
Aplicação Android que obtém a previsão meteorológica atual com base nas coordenadas GPS reais do dispositivo:

- Integração com a API pública **Open-Meteo**
- Localização automática via **FusedLocationProviderClient** (Google Play Services)
- Interface adaptada à orientação do ecrã (portrait e landscape)
- 28 ícones vetoriais de condições meteorológicas
- Fundos dinâmicos dia/noite e localização em português (PT)

---

## 4. Tecnologias

| Componente | Tecnologias |
|---|---|
| **Kotlin_TP2** | Kotlin, Apache Maven, IntelliJ IDEA |
| **CoolWeatherApp** | Kotlin, Android Studio, Gson, Google Play Services Location, Open-Meteo API |
