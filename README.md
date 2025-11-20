# 🐉 Old Dragon 2 Creator & Battle Simulator

Este projeto é um aplicativo Android nativo desenvolvido como parte de uma atividade acadêmica. O objetivo é implementar um criador de fichas para o sistema de RPG **Old Dragon 2** e um simulador de batalhas automatizado que executa em segundo plano.

## 📱 Funcionalidades

### 1. Criação de Personagens
Implementação fiel às regras do Livro Básico do Old Dragon 2:
* **Geração de Atributos:** Suporte aos três modos oficiais:
    * *Clássico:* 3d6 na ordem estrita.
    * *Aventureiro:* 3d6 com distribuição por prioridade da classe.
    * *Heróico:* 4d6 (descarta o menor) com distribuição por prioridade.
* **Raças:** Humano, Anão e Elfo, com seus respectivos modificadores e habilidades.
* **Classes:** Guerreiro, Mago e Ladrão, com cálculo automático de PV (Pontos de Vida) e CA (Classe de Armadura).

### 2. Simulador de Batalha (Background)
* **Service:** O combate ocorre em um `Foreground Service`, permitindo que a batalha continue mesmo se o usuário fechar o aplicativo.
* **Lógica de Combate:** Simulação round-a-round (Ataque vs CA, Dano, Iniciativa).
* **Notificações:** O usuário acompanha o progresso da batalha (PV do Herói vs PV do Monstro) pela barra de notificações do Android.
* **Morte:** Notificação de alta prioridade caso o personagem chegue a 0 PV.

## 🛠️ Tecnologias e Arquitetura

O projeto foi construído utilizando as tecnologias mais modernas de desenvolvimento Android, respeitando os requisitos da atividade:

* **Linguagem:** Kotlin.
* **Interface (UI):** Jetpack Compose (100% declarativa, sem XML de layout).
* **Arquitetura:** MVC (Model-View-Controller).
* **Assincronismo:** Kotlin Coroutines.
* **Android Components:**
    * `Foreground Service` para tarefas longas.
    * `NotificationManager` para feedback ao usuário.
    * `Parcelable` para transferência de objetos complexos entre Activity e Service.

## 📂 Estrutura do Projeto

O código está organizado em pacotes lógicos para facilitar a manutenção e a separação de responsabilidades:

```text
com.example.olddragon
├── controller       # Camada de Controle (MVC)
│   └── CharacterController.kt  # Gerencia o estado da tela e regras de criação
├── model            # Camada de Dados e Regras de Negócio
│   └── GameModel.kt            # Classes (Race, CharClass), Dados (Dice) e Lógica
├── view             # Camada de Visualização
│   └── CharacterScreen.kt      # Interface gráfica em Jetpack Compose
├── service          # Serviços do Android
│   └── BattleService.kt        # Motor de batalha em background
└── MainActivity.kt  # Ponto de entrada e configuração de permissões