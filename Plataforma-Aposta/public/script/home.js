const uid = localStorage.getItem("uid");
const login = localStorage.getItem("login");
const senha = localStorage.getItem("senha");
console.log({ uid, login, senha });

function SetUserInfo(uid, login, senha){
    const uidDoc = document.getElementById("uid")
    const senhaDoc = document.getElementById("senha")
    const loginDoc = document.getElementById("login")
    if(uid) uidDoc.textContent ="uid: " + uid
    if(login) loginDoc.textContent = "login: " + login
    if(senha) senhaDoc.textContent = "senha: " + senha
}

window.onload = () => {
  SetUserInfo(uid, login, senha);
};