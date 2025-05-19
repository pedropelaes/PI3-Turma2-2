import { initializeApp } from "https://www.gstatic.com/firebasejs/9.23.0/firebase-app.js";
import { getFunctions, httpsCallable } from "https://www.gstatic.com/firebasejs/9.23.0/firebase-functions.js";

let timerInterval;

const firebaseConfig = {
  apiKey: "AIzaSyAsdNeVhKB-kYlwmnry-XPJfojHbs-5zIc",
  authDomain: "superid-e53fb.firebaseapp.com",
  projectId: "superid-e53fb",
  appId: "1:459214844582:web:63d89c9f5692be2533ba67",
};

const app = initializeApp(firebaseConfig);
const functions = getFunctions(app);

const performAuth = httpsCallable(functions, "performAuth");

window.callPerformAuth = () =>{
    performAuth({
        site: "www.donabet.com.br",
        apiKey: "APIKEYTESTE"
    }).then(result => {
        const base64 = result.data.qrCodeImage;
        document.getElementById("qrCodeImg").src = base64;

        const timerElement = document.getElementById('timer');
        const resetButton = document.getElementById('resetButton');
        resetButton.style.display = 'none'; // Oculta o botão inicialmente
        // Inicia o temporizador de 1 minuto
        let timeLeft = 60;
        timerElement.textContent = `Expira em: ${timeLeft} segundos`;
        timerElement.textContent = '';
        clearInterval(timerInterval);
        timerInterval = setInterval(() => {
            timeLeft--;
            if (timeLeft <= 0) {
                clearInterval(timerInterval);
                timerElement.textContent = 'QR Code expirado. Gere um novo.';
                document.getElementById("qrCodeImg").src = ""
                resetButton.style.display = 'block'; // Exibe o botão após o término do temporizador
            } else {
                timerElement.textContent = `Expira em: ${timeLeft} segundos`;
            }
        }, 1000);
    }).catch(err => {
        console.error("Erro:", err.message);
    });
};

window.onload = window.callPerformAuth
