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

        const createdAt = data.createdAt;
        const drift = Date.now() - createdAt;
        let timeLeft = Math.max(60 - Math.floor(drift / 1000), 0);
        timerElement.textContent = `Expira em: ${timeLeft} segundos`;

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

        setTimeout(() => {
        const inicio = performance.now(); 
        startGetLoginStatusPolling(loginToken, inicio);
      }, 1000);
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
    console.log("uid:", data.uid)
    return data.uid
  })
  .catch(err =>{
    console.error("Erro ao consultar status do login", err.message);
  })
}

function startGetLoginStatusPolling(loginToken, inicio) {
  const tentativas = [15, 35, 59]; // segundos
  let loginConfirmado = false;

  function agendarTentativa(segundos, index) {
    const delay = segundos * 1000 - (performance.now() - inicio);
    setTimeout(() => {
      if (loginConfirmado) return;

      callGetLoginStatus(loginToken).then(uid => {
        if (uid) {
          loginConfirmado = true;
          clearInterval(timerInterval);
          const successText = document.getElementById("timer");
          successText.className = "texto-verde";
          successText.textContent = "Login confirmado! Redirecionando...";
          document.getElementById("qrCodeImg").src = "";
          console.log("Login confirmado:", uid);
          localStorage.setItem("uid", uid);        
          //localStorage.setItem("login", login);
          //localStorage.setItem("senha", senha);
          setTimeout(() => {
            window.location.href = "/home";
          }, 3000);
        } else {
          console.log(`Tentativa ${index + 1} (${segundos}s): aguardando login...`);
          if (index === tentativas.length - 1) {
            console.log("Tentativas esgotadas. QR Code precisa ser renovado.");
          }
        }
      }).catch(err => {
        console.error("Erro ao verificar login:", err);
      });

    }, Math.max(delay, 0)); // evita delay negativo caso o tempo tenha passado
  }

  tentativas.forEach((segundos, index) => {
    agendarTentativa(segundos, index);
  });
}


window.onload = () => {
  callPerformAuth();
};