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
        const loginToken = data.loginToken;
        document.getElementById("qrCodeImg").src = base64;
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
            resetButton.disabled = false;
          } else {
            timerElement.textContent = `Expira em: ${timeLeft} segundos`;
          }
        }, 1000);

        startGetLoginStatusPolling(loginToken)
      })
      .catch(err => {
        console.error("Erro:", err.message);
        resetButton.disabled = false;
      });
    }

  
function callGetLoginStatus(base64){
  return fetch("http://localhost:3000/api/get-login-status", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ loginToken: base64 })
  })
  .then(response => response.json())
  .then(data=>{
    return data.uid
  })
  .catch(err =>{
    console.error("Erro ao consultar status do login", err.message);
  })
}

function startGetLoginStatusPolling(loginToken) {
  let attempts = 0;
  const maxAttempts = 3;

  function poll() {
    callGetLoginStatus(loginToken).then(uid => {
      if (uid) {
        console.log("Login confirmado:", uid);
      } else {
        console.log("Tentativa", attempts + 1, "aguardando login");
        attempts++;

        if (attempts > maxAttempts) {
          console.log("Tentativas esgotadas, gerar outro QR Code");
          return;
        }
        setTimeout(poll, 22000); 
      }
    }).catch(err => {
      console.error("Erro ao verificar login:", err);
    });
  }

  setTimeout(poll, 15000); 
}


window.onload = () => {
  callPerformAuth();
};