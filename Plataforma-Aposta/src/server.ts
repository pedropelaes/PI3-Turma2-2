import express from "express"
import {Request, Response, Router} from "express";
import cors from "cors"
import path from "path";

const port = 3000;
const routes = Router();
var app = express();
app.use(cors());
app.use(express.static("public"));

app.use(routes);
routes.get('/', (req: Request, res: Response)=>{
    res.statusCode = 403;
    res.send('Acesso não permitido. Rota default não definida.');
});

routes.post("/loginSuperId", (req: Request, res: Response) => {
  res.sendFile(path.join(__dirname, "../public/pages/signInWithSuperID.html"));
});


app.listen(port, ()=>{
    console.log(`Server is running on: ${port}`)
})