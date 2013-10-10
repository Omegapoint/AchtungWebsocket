var GameHandler = function()
{
    var self = this;
    var body = document.getElementsByTagName("body")[0];

    this.name = window.prompt("Give me your name!");
    this.canvas = document.getElementById("canvas");

    if (this.canvas.getContext)
    {
        this.context = this.canvas.getContext("2d");
        this.socket = new GameSocketHandler(this, "ws://" + window.location.hostname + ":9000/game/connect", this.name);
        this.players = [];
    }
    else
    {
        body.appendChild(document.createTextNode("HTML5 unsupported in browser."));
    }

    this.templates = {};

    this.registerTemplate("player-list-template");
};

GameHandler.prototype.registerTemplate = function (name) {
    var source   = $("#" + name).html();
    this.templates[name] = Handlebars.compile(source);
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

        var ctx = this.context;
        ctx.fillStyle = player.color;
        ctx.strokeStyle = player.color;
        ctx.lineWidth=4;


        player.arcs.forEach(function(arc) {
            if(arc.hasOwnProperty("as")) {
                ctx.beginPath();
                ctx.arc(arc.x,  arc.y, player.r, arc.as, arc.ae, arc.direction === -1);
                ctx.stroke();
                ctx.closePath();
            } else {
                ctx.beginPath();
                ctx.moveTo(arc.x, arc.y);
                ctx.lineTo(arc.x + arc.dx, arc.y + arc.dy);
                ctx.stroke();
                ctx.closePath();
            }
        });

        var position = this.calculatePosition(player);
        if(player.direction === 0) {
            ctx.beginPath();
            ctx.moveTo(player.x, player.y);
            ctx.lineTo(position.x, position.y);
            ctx.stroke();
            ctx.closePath();
        } else {
            var now = Date.now();
            var dT = now - player.time - this.latency;;
            var fi = ((Math.PI * player.v * dT) / (2* player.r)) % (2 * Math.PI);
            var rs = player.a + -player.direction * Math.PI/2;
            var re = rs + player.direction * fi;

            var _f = function(func, angle) {
                return player.r * func(player.a + player.direction * angle);
            };
            var circleCenter = {
                x: _f(Math.cos, Math.PI/2) + player.x,
                y: _f(Math.sin, Math.PI/2) + player.y
            };

            ctx.beginPath();
            ctx.moveTo(player.x, player.y);
            ctx.arc(circleCenter.x, circleCenter.y, player.r, rs, re, player.direction === -1);
            ctx.stroke();
            ctx.moveTo(player.x, player.y);
            ctx.closePath();
        }


        ctx.beginPath();
        ctx.arc(position.x, position.y, 5, 0, Math.PI*2, false);
        ctx.fill();
        ctx.closePath();

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
    var dT = now - player.time - this.latency;

    var x, y, a;
    if(player.direction === 0) {
        x = player.x + Math.cos(player.a) * player.v * dT;
        y = player.y + Math.sin(player.a) * player.v * dT;
        return {"x": x, "y": y};
    } else {
        var fi = (Math.PI * player.v * dT) / (2* player.r);
        var _f = function(func, angle) {
          return player.r * func(player.a + player.direction*angle);
        };
        var currentCirclePos =  {
            x: -1 * _f(Math.cos, Math.PI/2),
            y: -1 * _f(Math.sin, Math.PI/2)
        };
        var futureCirclePos =  {
            x: -1 * _f(Math.cos, Math.PI/2 + fi),
            y: -1 * _f(Math.sin, Math.PI/2 + fi)
        };

        return {
            "x": player.x + futureCirclePos.x - currentCirclePos.x,
            "y": player.y + futureCirclePos.y - currentCirclePos.y
        };
    }

};

GameHandler.prototype.onWelcome = function(message)
{
    var self = this;

    for (var player in message.players)
    {
        this.players.push(new Player(message.players[player]))
    }

    $("body").on("click", "#start-button", function() {
        self.doReady();
    });
    this.updatePlayerList();

};

GameHandler.prototype.updatePlayerList = function()
{
    $("#online_players").html(this.templates["player-list-template"]({"players": this.players}));
}

GameHandler.prototype.onJoin = function(message)
{
    this.players.push(new Player(message.player));
    this.updatePlayerList();
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
    var playersLeft = [];

    for (var i in this.players)
    {
        if (this.players[i].name !== message.player.name)
        {
            playersLeft.push(this.players[i]);
        }
    }

    this.players = playersLeft;

    this.updatePlayerList();
};

GameHandler.prototype.doReady = function(id)
{
    var width = window.innerWidth - $("aside#online_players").width();
    var height = window.innerHeight;

    this.socket.doMessage(id, "Ready", {"sizeX": width, "sizeY": height});
};

GameHandler.prototype.onReady = function(message)
{
    this.context.canvas.width = message.sizeX;
     this.context.canvas.height = message.sizeY;
};

GameHandler.prototype.onStart = function(message, id, time)
{
    this.context.canvas.width  = message.sizeX;
    this.context.canvas.height = message.sizeY;

    console.log(window.innerWidth + " = " + message.sizeX);
    console.log(window.innerHeight + " = " + message.sizeY);

    var self = this;

    window.onkeyup = function(event)
    {
        if ((event.keyCode == 37) || (event.keyCode == 39))
        {
            self.doTurn.call(self, null, 0);
        }
    };
    window.onkeydown = function(event)
    {
        if ((event.keyCode == 37) || (event.keyCode == 39))
        {
            event.stopPropagation();
            self.doTurn.call(self, null, ((event.keyCode == 37) ? -1 : 1));
        }
    };
    window.setInterval(function()
    {
        self.draw.call(self);
    }, 20); // 50Hz
};

GameHandler.prototype.onDeath = function(message)
{
    for (var i in message.players)
    {
        this.findPlayer(message.players[i]).alive = false;
    }

};