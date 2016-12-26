var net = require('net');
var db = require('./db/db');
var queue = [];

net.createServer(function (socket) {
    socket.setNoDelay(true);

    console.log("connected new user");
    var accumulator = "";

    socket.on('end', function () {
        console.log('user disconnected');
        socket.id = null;
    });

    socket.on('data', function (message_peace) {
        accumulator += message_peace;
        while (accumulator.indexOf('#') != -1) {
            const message = accumulator.split('#')[0].split(' ');
            accumulator = accumulator.split('#')[1];//dangerous
            if (socket.id == null && message[0] != "con"){
                continue;
            }
            switch (message[0]) {
                case "con":
                    if (socket.id || message.length < 2) {
                        socket.write('1');
                        break;
                    }
                    socket.id = message[1];
                    socket.verified = false;
                    socket.write('0');
                    break;

                case "ver":
                    if (message.length < 2) {
                        socket.write('1');
                        break;
                    }
                    db.checkPass(socket.id, message[1], function (err) {
                        if (err) {
                            socket.write('1');
                            return;
                        }
                        socket.username = db.getUsername(socket.id, function (err) {
                            if (err) {
                                socket.write('1');
                                return;
                            }
                            socket.verified = true;
                            socket.write('0');
                        });
                    });
                    break;

                case "reg":
                    if (message.length < 3){
                        socket.write('1');
                        break;
                    }
                    db.createUser(socket.id, message[1], message[2], function (err) {
                        if (err)
                            socket.write('1');
                        else socket.write('0');
                    });
                    break;

                case "reset":
                    if (!socket.verified) {
                        socket.write('1');
                        break;
                    }
                    db.deleteUser(socket.id, function (err) {
                        if (err)
                            socket.write('1');
                        else
                            socket.write('0');
                    });
                    break;

                case "search":
                    if (socket.verified)
                        queue.push(socket);
                    else
                        socket.write('1');
                    break;

                case "gameov":
                    if (!socket.verified || socket.rival == null || message.length < 2) {
                        socket.write('1');
                        break;
                    }
                    db.updateRate(socket.id, message[1] == 'win', function (err) {
                        if (err)
                            socket.write('1');
                        else socket.write('0'); //ok
                    });
                    break;
                case "wall":
                    if (socket.rival == null) {
                        //game not started
                        socket.write('1');
                    } else if (!socket.rival._connecting) {
                        //rival leave
                        socket.write('2');
                    } else {
                        //ok
                        socket.rival.write(message[1]);
                    }
                    break;
            }
        }
    })
    ;
}).listen(8080, function () {
    console.log("listening");
});


function flushQueue() {
    var s = [queue, queue = []][0];
    //now s = queue
    //queue = []
    while (s.length > 0) {
        //trying get alive user
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
        x1.write(x2.username);//send start message
        x2.write(x1.username);//send start message
    }
}
setInterval(flushQueue, 3000);
