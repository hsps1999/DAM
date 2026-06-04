# ISEL — Desenvolvimento de Aplicações Móveis (DAM)

Repositório centralizado contendo todos os projetos académicos e exercícios desenvolvidos na unidade curricular de DAM (LEIM) no Instituto Superior de Engenharia de Lisboa (ISEL).

---

## 📁 Estrutura do Repositório

### 📚 [Trabalho Prático 1 (TP1)](DAM_TP1)
Pasta contentora de todos os componentes desenvolvidos para a primeira avaliação:

- **[Kotlin_TP1](DAM_TP1/Kotlin_TP1):** Exercícios de lógica e sintaxe Kotlin + Secção 6 (Virtual Library).
- **[HelloWorld](DAM_TP1/HelloWorld):** Primeiras apps Android (V1 e V2).
- **[SystemInfo](DAM_TP1/SystemInfo):** Monitorização de recursos e logs.
- **[Sketch2Art](DAM_TP1/Sketch2Art):** MIP-1 (App de IA generativa).

---

### 📚 [Trabalho Prático 2 (TP2)](DAM_TP2)
Pasta contentora de todos os componentes desenvolvidos para a segunda avaliação:

- **[Kotlin_TP2](DAM_TP2/Kotlin_TP2):** Exercícios de Kotlin avançado:
  - **Exercício 1.1 — Event Log Processing:** Processamento de logs de eventos com sequências e higher-order functions.
  - **Exercício 1.2 — Generics: In-Memory Cache:** Implementação de uma cache em memória com suporte a genéricos.
  - **Exercício 1.3 — Functions and Lambdas:** Pipeline de dados configurável com funções de ordem superior.
  - **Exercício 1.4 — Operator Overloading:** Sobrecarga de operadores em classes Kotlin.
- **[CoolWeatherApp](DAM_TP2/CoolWeatherApp):** Aplicação Android de previsão meteorológica com localização GPS real, 28 ícones de condições meteorológicas, layout landscape e localização em português.
- **[CatsNDogs](DAM_TP2/CatsNDogs):** Aplicação Android de galeria de imagens de cães com favoritos persistidos, cache offline (Room), ecrã de detalhes e arquitetura MVVM + Repository.

---

### 📚 [Trabalho Prático 3 (TP3)](DAM_TP3)
Pasta contentora de todos os componentes desenvolvidos para a terceira avaliação:

- **[GreetingProcessorProject](DAM_TP3/GreetingProcessorProject):** Processador de anotações KSP com `@Greeting` e `@Extract` (regex).
- **[JetpackWeatherApp](DAM_TP3/JetpackWeatherApp):** Reescrita da CoolWeatherApp com Jetpack Compose, Ktor e StateFlow — inclui favoritos persistidos e fundos animados.
- **[CatsNDogs-MIP3](DAM_TP3/CatsNDogs-MIP3):** Evolução multi-módulo da CatsNDogs com dois módulos de UI (XML Views + Compose) partilhando a mesma camada de dados.

---

### 📚 [Trabalho Prático 4 (TP4)](DAM_TP4)
Pasta contentora de todos os componentes desenvolvidos para a quarta avaliação:

- **[intro-coroutines](DAM_TP4/intro-coroutines):** Tutorial JetBrains de Kotlin Coroutines — app desktop de contribuidores GitHub com progressão de implementações bloqueante → async/await → canais.
- **[intro-coroutinesV2](DAM_TP4/intro-coroutinesV2):** Extensão do tutorial com implementação completa de Kotlin Flows (cold/hot, operadores, StateFlow).
- **[AISimpleCalls](DAM_TP4/AISimpleCalls):** App Kotlin JVM com chamadas a APIs de IA (IAEdu proxy, GPT-4o, Claude 3.5) — streaming, análise de sentimento (Tasks 1–4).
- **[GeminiImageApp](DAM_TP4/GeminiImageApp):** App Android de análise de imagens com Gemini Vision API (Task 5).
- **[build-android-start](DAM_TP4/build-android-start):** Codelab Firebase FriendlyChat — chat em tempo real com Firebase Auth + Realtime Database + Storage.
- **[NotesProXMLViews3](DAM_TP4/NotesProXMLViews3):** App Android de notas CRUD com Firebase Auth + Firestore e XML Views.

---

## 👤 Autor
- **Nome:** Hugo Spencer Pereira de Sousa
- **Número de Aluno:** `a46104`
- **Instituição:** ISEL — Licenciatura em Engenharia Informática e Multimédia

---

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Kotlin
- **Ambientes:** IntelliJ IDEA & Android Studio
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **APIs:** Fal.ai (Fast SDXL), Open-Meteo, Google Play Services Location, Dog CEO API, OpenAI, Gemini, IAEdu proxy
- **Persistência:** Room (SQLite), SharedPreferences, Firebase Firestore, Firebase Realtime Database
- **Autenticação:** Firebase Auth
- **UI:** XML Views, Jetpack Compose
- **Rede:** Gson (Retrofit), Ktor, kotlinx.serialization
- **Assincronismo:** Kotlin Coroutines, Kotlin Flows, StateFlow
- **Processamento de Anotações:** KSP (Kotlin Symbol Processing)
