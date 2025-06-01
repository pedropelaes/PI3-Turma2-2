# PI3-Turma2-2
Repositório do Projeto Integrador 3, do curso de Engenharia de Software da PUC - Campinas. 
Grupo: 
- [Felipe Corso Pretoni](https://github.com/felipecorsopretoni);
- [Mateus Giroto Mergulhão](https://github.com/Mateusmerg);
- [Pedro Pelaes Malinconico](https://github.com/pedropelaes);
- [Rafael Roveri Pires](https://github.com/RafssRv);
- [Vinicius Davi Zorzetto de Matos](https://github.com/Vzorzettoo);

SuperID
=================================================

---

Descrição do Projeto
---------------------

O *SuperID* é um aplicativo Android desenvolvido com Kotlin que atua como um gerenciador de autenticações pessoais e empresariais, com foco em segurança, praticidade e inovação. Ele permite o armazenamento seguro de credenciais e a realização de login em serviços parceiros por meio da leitura de QR Codes, eliminando a necessidade do uso constante de senhas.

Este projeto foi desenvolvido como parte de um trabalho integrador, com o objetivo de aplicar os conhecimentos adquiridos ao longo do curso na criação de uma solução realista, robusta e tecnicamente alinhada com as práticas modernas de desenvolvimento mobile e segurança da informação.

---

Objetivos
----------

- Criar um aplicativo Android seguro e intuitivo.
- Aplicar conceitos avançados de autenticação e criptografia.
- Utilizar tecnologias modernas como Firebase e Jetpack Compose.
- Desenvolver um sistema de autenticação por QR Code com validação em tempo real.
- Garantir boas práticas de versionamento, organização de equipe e documentação.

---

Funcionalidades Principais
---------------------------

1. Cadastro e Verificação  
- Criação de conta com nome, e-mail e senha mestre.  
- Validação de e-mail via Firebase Authentication.  
- Registro de UID e IMEI no Firestore para segurança adicional.  

2. Gerenciamento de Credenciais  
- Armazenamento seguro de senhas com criptografia.  
- Organização por categorias (sites, apps, dispositivos).  
- Geração automática de accessToken (Base64 com 256 caracteres).  
- Interface intuitiva para adicionar, editar e remover registros.  

3. Login com QR Code  
- Integração com serviços parceiros via Firebase Functions.  
- Geração e leitura de QR Codes com loginToken.  
- Consulta em tempo real para aprovação ou rejeição de login.  
- Validação com expiração automática por tempo ou tentativas.  

4. Recuperação de Acesso  
- Redefinição da senha mestre via Firebase.  
- Controle de status de verificação de conta no aplicativo.  

---

Tecnologias Utilizadas
-----------------------

- Kotlin – Linguagem moderna para Android  
- Jetpack Compose – Framework declarativo para UI  
- Firebase Authentication – Gerenciamento de usuários  
- Firebase Firestore – Banco de dados NoSQL em tempo real  
- Firebase Functions – Backend serverless para validação de login  
- Criptografia (AES ou RSA) – Proteção de dados sensíveis  
- ZXing / ML Kit – Leitura de QR Code  

---

Instalação e Execução
----------------------

(em desenvolvimento)

---

