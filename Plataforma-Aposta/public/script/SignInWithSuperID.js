
let timerInterval;

function generateQRCode() {
    const qrCodeElement = document.getElementById('qrcodeCanvas');
    const timerElement = document.getElementById('timer');
    const resetButton = document.getElementById('resetButton');
    const qrCodeContext = qrCodeElement.getContext('2d');
    timerElement.textContent = '';
    resetButton.style.display = 'none'; // Oculta o botão inicialmente

    // Simula um conteúdo único para o QR Code
    const qrCodeContent = `https://superid.app/login?token=${Date.now()}`;
    QRCode.toCanvas(qrCodeElement, qrCodeContent, { width: 200 }, function (error) {
        if (error) console.error(error);
    });

    // Inicia o temporizador de 1 minuto
    let timeLeft = 60;
    timerElement.textContent = `Expira em: ${timeLeft} segundos`;
    clearInterval(timerInterval);
    timerInterval = setInterval(() => {
        timeLeft--;
        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            timerElement.textContent = 'QR Code expirado. Gere um novo.';
            qrCodeContext.clearRect(0, 0, qrCodeElement.width, qrCodeElement.height); // Limpa o canvas
            resetButton.style.display = 'block'; // Exibe o botão após o término do temporizador
        } else {
            timerElement.textContent = `Expira em: ${timeLeft} segundos`;
        }
    }, 1000);
}

function handleButtonClick() {
    const resetButton = document.getElementById('resetButton');
    if (resetButton.style.display === 'block') {
        location.reload(); // Recarrega a página
    }
}

// Gera o QR Code ao carregar a página
window.onload = generateQRCode;
