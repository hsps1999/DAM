# ISEL — Desenvolvimento de Aplicações Móveis — Trabalho Prático 4

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26

---

## 1. Overview

Pasta com todos os componentes desenvolvidos para o **Trabalho Prático 4 (TP4)** da disciplina de DAM. O TP4 abrange programação assíncrona com coroutines e flows, integração com APIs de IA, e desenvolvimento de apps Android com Firebase (Firestore, Auth, Realtime Database).

---

## 2. Estrutura

```
DAM_TP4/
├── intro-coroutines/       → Tutorial JetBrains: Kotlin Coroutines (GitHub Contributors)
├── intro-coroutinesV2/     → Extensão do tutorial com implementação de Kotlin Flows
├── AISimpleCalls/          → App Kotlin: chamadas a APIs de IA (Tasks 1–4)
├── GeminiImageApp/         → App Android: análise de imagens com Gemini AI (Task 5)
├── build-android-start/    → App Android: Firebase FriendlyChat (Auth + Realtime Database)
└── NotesProXMLViews3/      → App Android: notas CRUD com Firebase Auth + Firestore
```

---

## 3. Componentes

### ⚙️ [intro-coroutines](intro-coroutines)
Tutorial oficial da JetBrains sobre **Kotlin Coroutines**, baseado numa app desktop Swing que carrega contribuidores de repositórios GitHub:

- Progressão de implementação bloqueante → callbacks → coroutines → concorrência
- Uso de `async/await`, `Flow`, canais e cancelamento
- Testes unitários incluídos para cada variante de implementação

### ⚙️ [intro-coroutinesV2](intro-coroutinesV2)
Extensão do tutorial com implementação completa de **Kotlin Flows**:

- Operadores `map`, `filter`, `combine`, `flatMapLatest`
- Cold vs. hot flows (StateFlow, SharedFlow)
- Exercícios de processamento de dados assíncrono com flows

### 🤖 [AISimpleCalls](AISimpleCalls)
Aplicação Kotlin (JVM) para explorar chamadas a diferentes **APIs de IA** via HTTP:

| Task | Descrição |
|---|---|
| Task 1–2 | Completações de texto com proxy IAEdu (GPT-4o, Claude 3.5) |
| Task 3 | Streaming de respostas com Ktor + kotlinx.serialization |
| Task 4 | Análise de sentimento: pipeline de classificação com LLM |

Chaves API geridas em `config.properties` (excluído do repositório).

### 📱 [GeminiImageApp](GeminiImageApp)
App Android para **análise de imagens com Gemini AI** (Task 5):

- Seleção de imagem da galeria ou câmara
- Envio para Gemini Vision API com prompt personalizado
- Resposta em tempo real exibida em ecrã
- Firebase configurado (google-services.json excluído do repositório)

### 📱 [build-android-start](build-android-start)
Codelab do Firebase: **FriendlyChat** — app de chat em tempo real:

- Autenticação por email/password com **Firebase Auth**
- Mensagens síncronas em tempo real com **Firebase Realtime Database**
- Upload de imagens para **Firebase Storage**
- UI em XML Views com RecyclerView e FAB

### 📱 [NotesProXMLViews3](NotesProXMLViews3)
App Android de notas CRUD com backend Firebase:

- Autenticação de utilizadores por email/password (**Firebase Auth**) com verificação de email
- Criação, edição e eliminação de notas persistidas em **Firebase Firestore**
- Lista ordenada por timestamp (mais recente primeiro) via `RecyclerView`
- SplashActivity com redirecionamento automático conforme estado de autenticação
- Código misto Kotlin + Java (model e utility em Java)

---

## 4. Tecnologias

| Componente | Tecnologias |
|---|---|
| **intro-coroutines / V2** | Kotlin, JVM, Kotlin Coroutines, Kotlin Flows, JUnit |
| **AISimpleCalls** | Kotlin, Ktor, kotlinx.serialization, OpenAI API, Gemini API, IAEdu proxy |
| **GeminiImageApp** | Kotlin, Android Studio, Firebase, Gemini Vision API |
| **build-android-start** | Kotlin, Android Studio, Firebase Auth, Firebase Realtime Database, Firebase Storage |
| **NotesProXMLViews3** | Kotlin/Java, Android Studio, Firebase Auth, Firebase Firestore, XML Views, RecyclerView |
