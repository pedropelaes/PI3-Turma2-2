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
        //callGetLoginStatus(base64)
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
      })
      .catch(err => {
        console.error("Erro:", err.message);
        resetButton.disabled = false;
      });
    }

  
function callGetLoginStatus(base64){
  const loginTokenTeste = "2hahjlbumzjn31xy326tapom8npxz80096fvsjcfasqdptql6ruiexqlilu6tdr2owl48ys8fvy083zddenpj1xzojcyfiu1d3uvc72wdybxvvbhc4h0mchek0xeynzv0w5mh6mrgyp1kdp88mrk6nxptwbi0vu2wdehznqbyykm0bpw45rkabx2t3mb67pofij4bkgo68a1m2c84wv1ndytml1vjplab0gnnnbvr2rjvjh9axoh16kz44j4ea6u"
  fetch("http://localhost:3000/api/get-login-status", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ loginToken: loginTokenTeste })
  })
  .then(response => response.json())
  .then(data=>{
    console.log(data.uid)
  })
}

window.onload = () => {
  callPerformAuth();
  callGetLoginStatus(); 
};
   
