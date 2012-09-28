var Player = function(player)
{
    this.x = 0; // X-position
    this.y = 0; // Y-position
    this.a = 0.0; // Angle of direction
    this.r = 0; // Radius (should be constant and same for all players)
    this.v = 0; // Velocity
    this.arcs = []; // The arcs performed by the player during the game
    this.time = Date.now(); // Last update of player (used for extrapolation)
    this.name = player.name;
    this.color = player.color;
    this.alive = true;
    this.direction = 0; // Current direction (-1 = left, 0 = straight, 1 = right)
};

Player.prototype.update = function(player, part, time)
{
    this.x = player.x;
    this.y = player.y;
    this.r = player.r;
    this.v = player.v * 100;
    this.time = time;
    this.direction = player.direction;

    this.arcs.push(part);
};