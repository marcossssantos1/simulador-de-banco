# 📈 Sistema de Negociação Financeira – Backend

Projeto backend desenvolvido em **Java com Spring Boot**, inspirado em **sistemas bancários e plataformas de negociação eletrônica**, com foco em **consistência de dados, regras de negócio e APIs REST**.

O objetivo do projeto é simular operações financeiras comuns em ambientes de mercado e bancos, como **gestão de contas, ordens, transações e extratos**, aplicando boas práticas de desenvolvimento backend.

---

## 🧠 Visão Geral

A aplicação simula um ecossistema financeiro onde usuários possuem **contas de negociação**, realizam **operações (ordens)** e geram **transações financeiras**, mantendo controle de saldo e histórico.

O projeto foi construído com uma arquitetura organizada em camadas, priorizando clareza, manutenibilidade e aderência a cenários reais do mercado financeiro.

---

## ⚙️ Funcionalidades

* Cadastro e gerenciamento de **contas de negociação**
* Criação e validação de **ordens financeiras** (compra/venda – simulação)
* Processamento de **transações** com atualização de saldo
* Consulta de **extrato e histórico de movimentações**
* Validações de regras de negócio (saldo disponível, status, consistência)
* APIs REST para integração client-server

---

## 🏗️ Arquitetura

* Arquitetura em camadas (**Controller, Service, Repository**)
* Separação clara de responsabilidades
* Uso de **DTOs** para comunicação externa
* Regras de negócio centralizadas na camada de serviço

---

## 🛠️ Tecnologias Utilizadas

* **Java**
* **Spring Boot**
* **Spring Data JPA / Hibernate**
* **REST APIs**
* **MySQL**
* **Maven**
* **Docker**
* **Git**

---

## 🔄 Fluxo Simplificado de Operação

1. Criação de uma conta de negociação
2. Solicitação de uma ordem financeira
3. Validação das regras de negócio
4. Geração da transação
5. Atualização de saldo e extrato
---

## 🚀 Como Executar

```bash
# clonar o repositório
git clone https://github.com/seu-usuario/seu-repositorio.git

# acessar o projeto
cd projeto

# executar a aplicação
mvn spring-boot:run
```

Configure o banco de dados no arquivo `application.yml` ou `application.properties` antes de executar.

---

## 📌 Considerações Finais

Este projeto não representa um sistema real de trading, mas sim uma **simulação educacional**, desenvolvida com foco em aprendizado, domínio de backend e aproximação com sistemas financeiros utilizados por bancos e corretoras.

---

👨‍💻 Desenvolvido por **Marcos Santos**
