const uidRecebido = localStorage.getItem("uid");
const accessTokenRecebido = localStorage.getItem("accessToken");
console.log({ uidRecebido, accessTokenRecebido});

function SetUserInfo(uidRecebido, accessTokenRecebido){
    const uidDoc = document.getElementById("uid")
    const accessTokenDoc = document.getElementById("accessToken")
    if(uidRecebido) uidDoc.textContent ="uid: " + uidRecebido
    if(accessTokenRecebido) accessTokenDoc.textContent = "accessToken: " + accessTokenRecebido
}

window.onload = () => {
  SetUserInfo(uidRecebido, accessTokenRecebido);
};