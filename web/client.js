class DisplayStreamerClient {
	constructor({host, port}) {
		host = host || "ws://localhost"
		port = port || 9000
		this.socketUrl = `${host}:${port}/display`
		this.connected = false
		this.attemptConnection()
	}

	attemptConnection() {
		try{
			// this.socketClient = new WebSocket(this.socketUrl)
			this.socketClient = new io(this.socketUrl, {transports: ['websocket'], upgrade: false})
			this.attachSocketEvents()
			this.targetCanvas = document.getElementById("streamBody")
		} catch (e) {
			debugger
		}
	}

	attachSocketEvents() {
		const self = this
		this.socketClient.onevent = function (packet) {
			const eventName = packet.data?.[0]
			const eventData = packet.data?.[1]
			if(eventName == "image") {
				self.messageEvent.bind(self)(eventData)
			}
        };
		this.socketClient.on("connect", this.openSocketEvent.bind(this))
		// this.socketClient.on("image", this.messageEvent.bind(this))
		this.socketClient.on("close", this.closeSocketEvent.bind(this))
		this.socketClient.on("error", this.socketErrorEvent.bind(this))
	}

	openSocketEvent(e) {
		console.log("Connected to server")
		this.connected = true
	}

	messageEvent(data) {
		// TODO: Render the pixel array to targetCanvas
		console.log("rendering")
		const ctx = this.targetCanvas.getContext('2d')
		const image = new Image()
		image.src = `data:image/png;base64,${data}`
		ctx.drawImage(image, 0, 0)
	}

	closeSocketEvent(e) {
		if(!e.wasClean) {
			console.log("Connection Failed")
			setTimeout(() => {
				this.attemptConnection()
			}, 2000);
		}
	}

	socketErrorEvent(e) {
		console.log(e)
	}
}