const express = require("express")
const app = express()
const os = require('os');
const my_name = "Son Nguyen"
let interfaces = os.networkInterfaces();
let addresses = [];
for (var k in interfaces) {
    for (var k2 in interfaces[k]) {
        var address = interfaces[k][k2];
        if (address.family === 'IPv4' && !address.internal) {
            addresses.push(address.address);
        }
    }
}
console.log(addresses)

app.use(express.json());       // to support JSON-encoded bodies
app.use(express.urlencoded({
    extended: true
  }));

app.get("/", (req, res) => {
    res.status(200).send("LMAO")
})

app.get("/name", (req, res) => {
    res.status(200).send(my_name)
})

app.get("/ipaddress", (req, res) => {
    res.send(addresses[0])
})

app.get("/time", (req, res) => {
    let now = new Date()
    let hoursString = now.getHours() >= 10 ? now.getHours() : "0" + now.getHours().toString()
    let minutesString = now.getMinutes() >= 10 ? now.getMinutes() : "0" + now.getMinutes().toString()
    let secondsString = now.getSeconds() >= 10 ? now.getSeconds() : "0" + now.getSeconds().toString()
    res.send(hoursString + ":" + minutesString + ":" + secondsString)
})
async function runServer() {
    try {
        let server = app.listen(8088, (req, res) => {
            console.log("Server listening on %s:%s", server.address().address, server.address().port)
        })
    } catch (error) {
        console.log("Server failed", error)
    }
}

runServer()
