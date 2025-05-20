let timerInterval;

function callPerformAuth() {
      fetch("http://localhost:3000/api/perform-auth", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ site: "www.donabet.com.br" })
      })
      .then(response => response.json())
      .then(data => {
        const base64 = data.qrCodeImage;
        document.getElementById("qrCodeImg").src = base64;

        const timerElement = document.getElementById('timer');
        const resetButton = document.getElementById('resetButton');
        resetButton.style.display = 'none';
        let timeLeft = 60;
        timerElement.textContent = `Expira em: ${timeLeft} segundos`;

        clearInterval(timerInterval);
        timerInterval = setInterval(() => {
          timeLeft--;
          if (timeLeft <= 0) {
            clearInterval(timerInterval);
            timerElement.textContent = 'QR Code expirado. Gere um novo.';
            document.getElementById("qrCodeImg").src = "";
            resetButton.style.display = 'block';
          } else {
            timerElement.textContent = `Expira em: ${timeLeft} segundos`;
          }
        }, 1000);
      })
      .catch(err => {
        console.error("Erro:", err.message);
      });
    }

    window.onload = callPerformAuth;
