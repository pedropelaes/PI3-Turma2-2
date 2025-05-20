let timerInterval;

function callPerformAuth() {

  const timerElement = document.getElementById('timer');
  const resetButton = document.getElementById('resetButton');
  resetButton.disabled = true;
      fetch("http://localhost:3000/api/perform-auth", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ site: "www.donabet.com.br" })
      })
      .then(response => response.json())
      .then(data => {
        const base64 = data.qrCodeImage;
        document.getElementById("qrCodeImg").src = base64;

        resetButton.style.display = 'none';
        let timeLeft = 10;
        timerElement.textContent = `Expira em: ${timeLeft} segundos`;

        clearInterval(timerInterval);
        timerInterval = setInterval(() => {
          timeLeft--;
          if (timeLeft <= 0) {
            clearInterval(timerInterval);
            timerElement.textContent = 'QR Code expirado. Gere um novo.';
            document.getElementById("qrCodeImg").src = "";
            resetButton.style.display = 'block';
            resetButton.disabled = false;
          } else {
            timerElement.textContent = `Expira em: ${timeLeft} segundos`;
          }
        }, 1000);
      })
      .catch(err => {
        console.error("Erro:", err.message);
        resetButton.disabled = false;
      });
    }

    window.onload = callPerformAuth;
