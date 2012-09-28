var GameHandler = function()
{
    var self = this;
    var body = document.getElementsByTagName("body")[0];

    this.name = "superdupertestplayer";//window.prompt("Give me your name!");
    this.canvas = document.getElementById("canvas");

    if (this.canvas.getContext)
    {
        //this.canvas.setAttribute("style", "width:" + window.innerWidth + "px;height:" + window.innerHeight + "px;");
        //this.canvas.appendChild(document.createTextNode("HTML5 unsupported in browser."));

        //body.appendChild(this.canvas);

        this.context = this.canvas.getContext("2d");
        this.context.canvas.width  = window.innerWidth;
        this.context.canvas.height = window.innerHeight;
        this.socket = new GameSocketHandler(this, "ws://localhost:9000/game/connect", this.name);
        this.players = [];

        window.onkeyup = function(event){ if ((event.keyCode == 37) || (event.keyCode == 39)) { self.doTurn.call(self, null, 0); } };
        window.onkeydown = function(event){
            if ((event.keyCode == 37) || (event.keyCode == 39))
            {
                event.stopPropagation();
                self.doTurn.call(self, null, ((event.keyCode == 37) ? -1 : 1));
            }
        };
        window.setInterval(function(){ self.draw.call(self); }, 20); // 50Hz
    }
    else
    {
        body.appendChild(document.createTextNode("HTML5 unsupported in browser."));
    }
};

GameHandler.prototype.findPlayer = function(player)
{
    var name = (player !== undefined) ? player.name : this.name;

    for (var i in this.players)
    {
        if (this.players[i].name == name)
        {
            return this.players[i];
        }
    }

    return null;
};

GameHandler.prototype.draw = function()
{
    this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
    this.drawPlayers();
};

GameHandler.prototype.drawBoard = function()
{

};

GameHandler.prototype.drawPlayers = function()
{
    for (var i in this.players)
    {
        var player = this.players[i];

        if (!player.alive)
        {
            continue;
        }

        var position = this.calculatePosition(player);

        var ctx = this.context;
        player.arcs.forEach(function(arc) {
            ctx.beginPath();
            ctx.moveTo(arc.x, arc.y);
            ctx.lineTo(arc.dy, arc.dy);
            ctx.closePath();
        });

        this.context.beginPath();
        this.context.arc(position.x, position.y, 5, 0, Math.PI*2, false);
        //this.context.moveTo(position.x, position.y);
        this.context.fillStyle = player.color;
        this.context.fill();
        this.context.closePath();

    }
};

/**
 * Returns the new x, y coordinates for the given player
 * @param player
 * @return {Object} object containing {x: number, y: number}
 */
GameHandler.prototype.calculatePosition = function(player)
{
    var now = Date.now();
    var dT = now - player.time;

    var x, y, a;
    if(true || player.direction === 0) {
        x = player.x + Math.cos(player.a) * player.v * dT;
        y = player.y + Math.sin(player.a) * player.v * dT;
        a = player.a;
    } else {
        
    }
    return {"x": x, "y": y};
};

GameHandler.prototype.onWelcome = function(message)
{
    for (var player in message.players)
    {
        this.players.push(new Player(message.players[player]))
    }
};

GameHandler.prototype.onJoin = function(message)
{
    this.players.push(new Player(message.player))
};

GameHandler.prototype.onPing = function(message, id)
{
    this.doPong(id);
};

GameHandler.prototype.doPing = function(id)
{
    this.socket.doMessage(id, "Ping", {});
};

GameHandler.prototype.onPong = function(message)
{
    console.log(JSON.stringify(message));
};

GameHandler.prototype.doPong = function(id)
{
    this.socket.doMessage(id, "Pong", {});
};

GameHandler.prototype.onTurn = function(message, id, time)
{
    var him = this.findPlayer(message.player);
    him.update(message.player, message.part, time);
};

GameHandler.prototype.doTurn = function(id, direction)
{
    var me = this.findPlayer();

    if (me.direction === direction)
    {
        return;
    }

    me.direction = direction;

    this.socket.doMessage(id, "Direction", {"direction": direction});
};

GameHandler.prototype.onLeave = function(message)
{
    for (var i in this.players)
    {
        if (this.players[i].name == message.player.name)
        {
            delete this.players[i];
        }
    }
};

GameHandler.prototype.doReady = function(id)
{
    this.socket.doMessage(id, "Ready", {});
};

GameHandler.prototype.onDeath = function(message)
{
    for (var i in message.players)
    {
        this.findPlayer(message.players[i]).alive = false;
    }
};