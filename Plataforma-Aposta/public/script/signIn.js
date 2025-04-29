import QRCode from 'qrcode';

// Primeiro, certifique-se de instalar a biblioteca qrcode via npm:
// npm install qrcode


function generateQRCode(text, canvasId) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) {
        console.error('Canvas element not found');
        return;
    }

    QRCode.toCanvas(canvas, text, function (error) {
        if (error) {
            console.error('Error generating QR Code:', error);
        } else {
            console.log('QR Code generated successfully');
        }
    });
}

// Exemplo de uso:
// Adicione um elemento <canvas id="qrcodeCanvas"></canvas> no HTML
// E chame a função assim:
// generateQRCode('https://example.com', 'qrcodeCanvas');