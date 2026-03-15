# System Info — Android Device Information App

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26  
**Prazo de Entrega:** 8 de março de 2026

---

## 1. Overview

Aplicação Android que lê e exibe as propriedades do sistema do dispositivo através do objeto `android.os.Build`. Desenvolvida no âmbito da **Secção 5.3** do Tutorial 1 de DAM.

---

## 2. System Architecture

**Linguagem:** Kotlin | **Min SDK:** API 24 | **Package:** `dam_a46104.systeminfo`

```
SystemInfo/
└── app/src/main/
    ├── java/dam_a46104/systeminfo/
    │   └── MainActivity.kt     → lê Build properties e popula o TextView
    └── res/
        ├── layout/
        │   └── activity_main.xml  → ScrollView + multiline TextView (fonte monospace)
        └── values/
            └── strings.xml        → strings externalizadas
```

---

## 3. Implementation

O `MainActivity.kt` usa `buildString { appendLine(...) }` para compor uma string com as seguintes propriedades do `android.os.Build`:

| Categoria | Propriedades |
|---|---|
| **Dispositivo** | `MANUFACTURER`, `BRAND`, `MODEL`, `DEVICE`, `PRODUCT`, `HARDWARE` |
| **Software** | `VERSION.RELEASE`, `VERSION.SDK_INT`, `VERSION.CODENAME`, `ID`, `FINGERPRINT` |

O texto é exibido num `TextView` dentro de um `ScrollView`, com fonte `monospace` para melhor legibilidade.

---

## 4. Testing

Testado em dispositivo físico via USB Debugging (ADB). O output exibe os dados reais do dispositivo em tempo de execução.

---

## 5. Prompting Strategy & AI Disclosure

> **Política aplicada:** `[AC YES, AI NO]` — O código Kotlin e XML foi escrito manualmente.

> **IA usada na documentação:** Antigravity (Google DeepMind) auxiliou na geração deste relatório `[AC YES, AI YES]`.
