
var net = require('net');
var db = require('./db/db');
var queue = [];

var server = net.createServer(function (socket) {
    socket.setNoDelay(true);
    console.log("connected new user");
    var accum = "";

    socket.on('end', function () {
        console.log('user disconnected');
        socket.id = null;
    });

    socket.on('data', function (mes) {
        accum += mes;
        if (accum.slice(-1) == '#') {//last element == delimiter
            var message = accum.slice(0, accum.length - 1).split(' ');
            accum = "";
            switch (message[0]) {
                case "con":
                    if (socket.id) {
                        socket.write('1');
                        break;
                    }
                    socket.id = message[1];
                    socket.verified = false;
                    socket.write('0');
                    break;

                case "ver":
                    db.checkPass(socket.id, message[1], function (err) {
                        if (!err) {
                            socket.verified = true;
                            socket.write('0');
                        }
                        else
                            socket.write('1');
                    });
                    break;

                case "reg":
                    db.createUser(socket.id, message[1], function (err) {
                        if (err)
                            socket.write('1');
                        else socket.write('0');
                    });
                    break;

                case "reset":
                    if (socket.verified)
                        db.deleteUser(socket.id, function (err) {
                            if (err)
                                socket.write('1');
                            else
                                socket.write('0');
                        });
                    else
                        socket.write('1');
                    break;

                case "search":
                    if (socket.verified)
                        queue.push(socket);
                    else
                        socket.write('1');
                    break;

                case "gameov":
                    if (socket.verified && socket.rival != null)
                        db.updateRate(socket.id, message[1] == 'win', function (err) {
                            if (err)
                                socket.write('1')
                            else socket.write('0'); //ok
                        });
                    else
                        socket.write('1'); //game not started
                    socket.rival = null;
                    break;
                case "wall"://set wall
                    if (socket.rival == null) {
                        socket.write('1');//game not started
                    } else if (socket.rival.readyState != openState) {
                        socket.write('2');//rival leave
                    } else
                        socket.rival.write(message[1]);//ok
                    break;
                case "p"://ping
                    socket.write((new Date()).getTime().toString());
                    break;
            }
        }
    });
}).listen(8080, 'localhost');


function flushQueue(id) {
    var s = [queue, queue = []][0];//swap trick
    //queue == []
    while (s.length > 0) {
        var x1 = s.pop();
        while (!x1.destroyed && s.length > 0)
            x1 = s.pop();

        if (x1.destroyed)
            break;
        if (s.length == 0) {
            queue.push(x1);
            break;
        }

        var x2 = s.pop();
        while (!x2.destroyed && s.length > 0)
            x2 = s.pop();

        if (x2.destroyed) {
            queue.push(x1);
            break;
        }
        x1.rival = x2;
        x2.rival = x1;
        x1.write("s");//send start message
        x2.write("s");//send start message
    }
}
//setInterval(flushQueue, 3000);