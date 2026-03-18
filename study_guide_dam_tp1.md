# DAM TP1 

Este documento sintetiza os objetivos técnicos, decisões de arquitetura e conceitos fundamentais aplicados no **Trabalho Prático 1** de Desenvolvimento de Aplicações Móveis (DAM).

---

## 1. Kotlin Fundamentals (Parte 1)

### Exercícios de Sintaxe
- **Squares (Arrays):** Utilização de `IntArray(50) { it * it }` para performance (evita boxing de tipos primitivos) vs `Array<Int>(50)` (objetos).
- **Consola Calculator:** Uso intensivo da expressão `when` (que é exaustiva e mais poderosa que o `switch` tradicional) e de **String Templates** (`$var`) para formatação limpa.
- **Ressaltos (Functional):** Paradigma funcional com `generateSequence` (lazy evaluation). A sequência só é processada à medida que os items são necessários, otimizando memória.

### Conceitos OOP em Kotlin
- **Encapsulamento (Book.kt):** 
    - **Custom Setters:** Implementação de lógica de validação diretamente no `set`. Ex: Impedir que `availableCopies` seja negativo.
    - **Custom Getters:** Propriedade calculada `era` (Classic/Modern/Contemporary) que não ocupa memória extra, sendo avaliada apenas quando lida.
- **Singleton & Shared State (Library.kt):** 
    - Uso de `companion object` para gerir o `totalBooksAdded`, um contador partilhado por todas as instâncias da classe.
- **Data Classes (LibraryMember):** 
    - Explicar ao professor que usamos `data class` para classes cujo propósito principal é armazenar dados. O compilador gera automaticamente `equals()`, `hashCode()`, `toString()` e `copy()`.
- **Inheritance (Hierarquia de Livros):**
    - `Book` como `abstract class` (não instanciável diretamente) com o método abstrato `getStorageInfo()`, forçando `DigitalBook` e `PhysicalBook` a fornecerem as suas implementações específicas (MB vs Peso).

---

## 2. Android Basics (System Info)

- **Acesso ao Hardware:** Uso do objeto `android.os.Build` para extrair metadados do dispositivo (`MANUFACTURER`, `SDK_INT`, etc.).
- **UI Simples:** Implementação de um `TextView` dentro de um `ScrollView` para lidar com longas listas de strings sem cortar o conteúdo.
- **Package Naming:** Seguimento rigoroso da regra de nomenclatura `dam_a46104.systeminfo` para conformidade académica.

---

## 3. Advanced Android (Sketch2Art – MIP-1)

Esta é a parte mais complexa e "profissional" do trabalho.

### Arquitetura MVVM (Model-View-ViewModel)
- **ViewModel (`MainViewModel`):** Atua como ponte entre os dados e a UI. Sobrevive a mudanças de configuração (ex: rodar o ecrã).
- **Repository Pattern:** O `DrawingRepository` isola a lógica da API (Fal.ai), permitindo que a UI não precise de saber se os dados vêm da rede ou do disco.

### Programação Reativa e Assíncrona
- **StateFlow:** Uso de `MutableStateFlow` para gerir o estado da UI (`Idle`, `Loading`, `Success`, `Error`). A View (Activity) "observa" este fluxo e reage automaticamente a mudanças.
- **Coroutines:** Execução da chamada de API em segundo plano via `viewModelScope.launch` para evitar o erro "Application Not Responding" (ANR).

### Componentes Técnicos
- **Retrofit 2:** Utilizado como cliente HTTP para comunicação com a API REST da Fal.ai.
- **Coil:** Biblioteca moderna de carregamento de imagens que trata do cache e renderização eficiente de bitmaps.
- **Custom View (`DrawingView`):** Criação de um Canvas interativo onde o utilizador desenha `Path`s com um objeto `Paint`. A conversão do desenho para `Bitmap` é o que serve de "Input" para a IA.

---

## 4. Decisões Técnicas & Soluções de Problemas

- **Segurança:** Gestão da `FAL_API_KEY` através do ficheiro `local.properties` para garantir que segredos não são expostos no GitHub.
- **Estabilidade do AVD:** Ajuste do modo GPU (`software`) para garantir que o emulador corre estavelmente em máquinas com drivers gráficos complexos.
- **AI Tooling:** Uso do **Antigravity** como copiloto para ajudar na estruturação da arquitetura MVVM, mas com revisão manual absoluta do código para garantir compreensão total (`[AC YES, AI YES]`).

---
