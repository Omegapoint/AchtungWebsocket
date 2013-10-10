var GameSocketHandler = function(game, uri, name)
{
    var self = this;

    this.game = game;
    this.name = name;
    this.socket = new WebSocket(uri + "/" + name);

    this.socket.onopen = function(event) { self.onOpen.call(self, event) };
    this.socket.onclose = function(event) { self.onClose.call(self, event) };
    this.socket.onerror = function(event) { self.onError.call(self, event) };
    this.socket.onmessage = function(event) { self.onMessage.call(self, event) };
};

GameSocketHandler.prototype.onOpen = function(event)
{
    console.log("Recieved Open");
    //this.game.doReady();
};

GameSocketHandler.prototype.onClose = function(event)
{
    console.log("Recieved Close");
};

GameSocketHandler.prototype.onError = function(event)
{
    console.log("Recieved Error");
};

GameSocketHandler.prototype.doMessage = function(id, action, message)
{
    console.log("Sending " + action);
    this.socket.send(JSON.stringify({"id": id, "action": action, "name": this.name, "message": message}));
};

GameSocketHandler.prototype.onMessage = function(event)
{
    var data = JSON.parse(event.data);
    var id = data.id;
    var time = data.time;
    var action = data.action;
    var message = data.message;

    this.game.latency = Date.now() - data.time;

    console.log("Recieved " + action);

    switch(action)
    {
        case "Ping":
            this.game.onPing(message, id, time); break;
        case "Pong":
            this.game.onPong(message, id, time); break;
        case "Join":
            this.game.onJoin(message, id, time); break;
        case "Welcome":
            this.game.onWelcome(message, id, time); break;
        case "Ready":
            this.game.onReady(message, id, time); break;
        case "Start":
            this.game.onStart(message, id, time); break;
        case "Direction":
            this.game.onTurn(message, id, time); break;
        case "Leave":
            this.game.onLeave(message, id, time); break;
        case "Death":
            this.game.onDeath(message, id, time); break;
        case "Update":
            this.game.onUpdate(message, id, time); break;
        default:
            console.log(data);
    }
};