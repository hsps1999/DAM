# Sketch2Art — IA Creative App (MIP-1)

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26  
**Fase:** MIP-1 (Mission Impossible Possible)

---

## 1. Overview
A **Sketch2Art** é uma aplicação Android experimental que permite transformar desenhos manuais (esquemas) em arte digital de alta qualidade, utilizando Inteligência Artificial generativa. 

O utilizador desenha livremente num "Canvas" digital e, através de um prompt interpretado pela API da **Fal.ai**, o desenho serve de base estrutural para a geração de uma nova imagem.

---

## 2. Main Features
- **Custom Drawing Canvas:** Superfície de desenho otimizada com suporte para limpeza total e captura de bitmap.
- **AI Art Generation:** Integração com o modelo **Fast SDXL** via Fal.ai.
- **Dynamic UI States:** Gestão de estados (Idle, Loading, Success, Error) com feedback visual ao utilizador.
- **Image Processing:** Carregamento eficiente de imagens via **Coil**, com suporte para Base64 e URIs.

---

## 3. Technical Architecture
A aplicação segue os padrões modernos de desenvolvimento Android recomendados em DAM:

### UI & Layout
- **Material Design 3:** Utilização de componentes modernos e temas dinâmicos.
- **View Binding:** Acesso seguro e tipado aos elementos do layout XML.
- **Custom View:** A classe `DrawingView` gere toda a lógica de desenho (Path, Paint, Canvas).

### Logic & Data
- **MVVM (Model-View-ViewModel):** Separação clara entre a lógica de interface (`MainActivity`) e a lógica de negócio (`MainViewModel`).
- **Repository Pattern:** O `DrawingRepository` centraliza as chamadas à API, abstraindo a fonte dos dados.
- **Retrofit 2:** Cliente HTTP para comunicação com os endpoints da Fal.ai.
- **StateFlow & Coroutines:** Gestão de estado assíncrona e reativa para garantir uma UI fluida sem bloquear a Main Thread.

---

## 4. AI & Tools Usage Disclosure
Este projeto foi desenvolvido como parte do desafio **MIP-1**, onde o uso de ferramentas de IA é incentivado e obrigatório.

- **AI Model:** Fal.ai (Fast SDXL) para a geração de imagem.
- **AI Assistant:** Antigravity (Google DeepMind) foi utilizado como copiloto para o design da arquitetura, geração de código base e documentação ([AC YES, AI YES]).
- **Transparency:** Todo o código gerado foi revisto, testado e compreendido pelo autor para efeitos de discussão oral.

---

## 5. Setup Required
Para correr esta aplicação, é necessário configurar uma API Key da Fal.ai:
1. Adicionar `FAL_API_KEY=your_key_here` no ficheiro `local.properties` ou como variável de ambiente no Gradle.
2. Sincronizar o projeto e fazer Deploy no Android Studio.
