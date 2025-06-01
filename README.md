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

[Visualizar Documento do Projeto (PDF)](./PI3-SuperID.pdf)

[Prototipo](https://www.figma.com/design/euW5snPDW1cQiOjDelOosU/PI3?node-id=0-1&t=epSWZjk3YpgZP90X-1)

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
1. Instalação do app:
Para instalar o nosso aplicativo, é necessário abrir em um dispositivo celular Android
o arquivo SuperId.apk, disponibilizado em um de nossos releases.

2. Login por QR code
Para usar a nossa simulação de login por QR Code, é necessário rodar nossa plataforma node
- 1: Tenha o node instalado
- 2: Clone o nosso repositório,
- 3: No terminal, entre no diretorio Plataforma_aposta
- 4: Rode os comandos: npm i, npm run dev
- 5: No seu navegador, abra a página localhost:3000/login
- 6: Selecione a opção SuperId
- 7: Escaneie o QR Code pelo aplicativo do SuperId
- 8: Aguarde um pouco, e se tudo correr corretamente, o login será realizado e você será redirecionado
---

