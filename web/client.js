class DisplayStreamerClient {
	constructor({host, port}) {
		host = host || "ws://localhost"
		port = port || 9000
		this.socketUrl = `${host}:${port}/app/display`
		this.connected = false
		this.attemptConnection()
	}

	attemptConnection() {
		try{
			this.socketClient = new WebSocket(this.socketUrl)
			this.attachSocketEvents()
			this.targetCanvas = document.getElementById("streamBody")
		} catch (e) {
			debugger
		}
	}

	attachSocketEvents() {
		this.socketClient.onopen = this.openSocketEvent.bind(this)
		this.socketClient.onmessage = this.messageEvent.bind(this)
		this.socketClient.onclose = this.closeSocketEvent.bind(this)
		this.socketClient.onerror = this.socketErrorEvent.bind(this)
	}

	openSocketEvent(e) {
		console.log("Connected to server")
		this.connected = true
	}

	messageEvent({data}) {
		// TODO: Render the pixel array to targetCanvas
		console.log(data);
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