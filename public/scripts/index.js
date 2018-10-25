
const webSocket = new WebSocket("ws://localhost:9000/live-messages")

webSocket.addEventListener("open", () => console.log("Opened web socket connection"))
webSocket.addEventListener("message", ({ data }) => console.log(JSON.parse(data).offset))