# 🧵 Ponto Gestor

Aplicativo mobile desenvolvido para auxiliar **costureiras** no gerenciamento de clientes e pedidos de costura, facilitando o controle do dia a dia do ateliê.

---

## 📱 Funcionalidades

- **Tela de Login e Registro** — autenticação segura via Firebase
- **Hub de Pedidos** — visão geral de todos os pedidos em andamento
- **Criação de Pedidos** — registro simplificado com:
  - Tipo de peça e tecido
  - Preço e data de entrega
  - Fotos de referência enviadas pelo cliente
  - Contato do cliente
- **Detalhamento do Pedido** — informações completas como tecido, tipo de botão e especificações
- **Edição de Pedido** — atualização de qualquer informação registrada
- **Exclusão de Pedido** — confirmação via senha do usuário para evitar exclusões acidentais

---

## 🛠️ Tecnologias Utilizadas

| Camada | Tecnologia |
|---|---|
| Back-end | Java 17 + Spring Boot |
| Banco de Dados | Firebase Firestore |
| Autenticação | Firebase Auth |
| Containerização | Docker |

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

- Java 17
- Maven
- Conta no Firebase com projeto criado

### Configuração do Firebase

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Vá em **Configurações do Projeto → Contas de Serviço**
3. Clique em **Gerar nova chave privada** e baixe o arquivo `.json`
4. Renomeie para `firebase-service-account.json` e coloque em `src/main/resources/`

### Executando

```bash
# Clone o repositório
git clone https://github.com/LuizEduPires/Ponto-gestor.git

# Entre na pasta
cd Ponto-gestor

# Execute o projeto
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`

---
## 👨‍💻 Desenvolvedores

| Nome | Função |
|---|---|
| Luiz Eduardo Madeira Pires | Back-end & Banco de Dados |
| Igor Porto de Matos | Back-end & Banco de Dados |
