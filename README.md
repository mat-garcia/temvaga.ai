-----

# 🤖 TemVaga.ai Agent - V1.0.0

Um agente de IA inteligente que encontra automaticamente as melhores oportunidades de emprego para você e as entrega diretamente no seu Telegram.

---

--

## 🧐 Sobre o Projeto

No mercado de trabalho competitivo de hoje, encontrar a oportunidade certa pode ser um trabalho de tempo integral por si só. O **TemVaga.ai Agent** foi criado para automatizar esse processo.

Este agente é executado a cada quatro horas para buscar novas vagas na plataforma [Adzuna](https://www.adzuna.com/). Em seguida, ele utiliza um modelo de IA para analisar e comparar essas oportunidades com um perfil de usuário predefinido, garantindo que apenas as vagas mais relevantes sejam selecionadas. Por fim, as melhores correspondências são formatadas e enviadas como uma notificação para um chat do Telegram, mantendo você atualizado em tempo real.

## ✨ Funcionalidades

- **🤖 Busca Automatizada:** Roda automaticamente a cada 4 horas para encontrar novas postagens de vagas.
- **🧠 Matching com IA:** Compara de forma inteligente as descrições das vagas com suas habilidades e perfil específicos.
- **🔔 Notificações no Telegram:** Entrega uma lista limpa e formatada das melhores oportunidades diretamente para você.
- **🔧 Configuração Fácil:** Todas as definições são gerenciadas em um arquivo `application.properties` simples.
- **🚀 Construído com Spring Boot:** Um backend robusto e escalável utilizando tecnologias Java modernas.

## ⚙️ Como Funciona

O agente segue um fluxo de trabalho simples e eficaz:

1.  **Gatilho Agendado:** Uma tarefa é agendada para rodar a cada 4 horas.
2.  **Busca de Vagas:** O agente chama a API da Adzuna para buscar as últimas vagas com base em critérios de pesquisa predefinidos.
3.  **Análise de IA:** Os resultados são passados para um modelo de IA que compara cada vaga com o perfil do usuário (habilidades, nível de experiência, etc.).
4.  **Filtro e Seleção:** A IA seleciona as oportunidades mais relevantes (score \>= 6).
5.  **Notificação:** A lista curada de vagas é formatada e enviada para o chat do Telegram configurado.

## 🚀 Primeiros Passos

Para obter uma cópia local e colocá-la em funcionamento, siga estes passos simples.

### Pré-requisitos

- Java 21 ou superior
- Maven
- Um Token de Bot do Telegram e Chat ID
- ID e Key da API da Adzuna

### Para ober um bot telegram e chat ID:

1. baixe o telegram app
2. busque pelo @BotFather
3. envie uma msg para ele "/newbot"
4. siga passo a passo e ira obter o token do seu bot

Chat id

1. busque pelo seu bot @NomeQueVoceDeu.
2. envie uma msg para ele.
3. bata no link https://api.telegram.org/botSEUTOKENAQUI/getUpdates

4. o json retonara algo com "chat: { id: 12131, fisrt_name: "seu nome" }

[Documentacao Bots Telegram](https://core.telegram.org/bots)

### Configuração

1.  Clone o repositório:

    ```sh
    git clone
    ```

2.  Navegue até `src/main/resources/` e abra o arquivo `application.properties`.

3.  Preencha as credenciais e configurações necessárias:

    ```properties
    # Configuração da API Adzuna
    adzuna.api.id=SEU_ADZUNA_API_ID
    adzuna.api.key=SEU_ADZUNA_API_KEY

    # Configuração do Bot do Telegram
    telegram.bot.token=SEU_TELEGRAM_BOT_TOKEN
    telegram.chat.id=SEU_TELEGRAM_CHAT_ID

    # Spring AI (OpenAI/Ollama/Etc)
    spring.ai.openai.api-key=SUA_OPENAI_API_KEY

    ```

- ollama local e gemini estao pre configurados

- para mudar o seu perfil de dev e tambem as vagas a serem buscadas:
  - edite perfil no MainAgent.java e urls no AdzunaService
  - atualmente busca por java backend.

4.  Execute a aplicação:

    ```sh
    ./mvnw spring-boot:run
    ```

---

## 📜 Licença

Copyright 2026 - MatGDev

Licensed under the Apache License, Version 2.0 (the "[License]("./LICENSE.md")"");
