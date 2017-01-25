var net = require('net');
var db = require('./db/sql_db.js');
var queue = [];

net.createServer(function (socket) {
    socket.setNoDelay(true);

    console.log("connected new user");
    var accumulator = "";

    socket.on('end', function () {
        console.log('user disconnected');
        socket.id = null;
    });

    socket.on('error', function (err) {
        console.log(err);
        socket.destroy();
    });

    socket.on('data', function (message) {
        message += "";
        message = message.split(' ');
        console.log(message);
        if (socket.verified == null && !(message[0] == "con" || message[0] == "top")) {
            return;
        }
        switch (message[0]) {

            //get top
            case "top":
                db.getTop(message[1], message[2], function (err, top) {
                    if (err) {
                        socket.write('1');
                        return;
                    }
                    socket.write(top);
                });
                break;

            case "del":
                db.deleteUser(socket.id, function (err) {
                    if (err) {
                        socket.write('1');
                        return;
                    }
                    socket.write('0');
                });
                break;
            //first message
            case "con":
                if (socket.id || message.length < 2) {
                    socket.write('1');
                    break;
                }
                socket.id = message[1];
                socket.verified = false;
                socket.write('0');
                break;

            //verification of user
            case "ver":
                if (message.length == 1) {
                    socket.write('1');
                    break;
                }
                db.checkPass(socket.id, message[1], function (err) {
                    if (err) {
                        socket.write('1');
                    } else {
                        socket.write('0');
                    }
                });
                break;

            //registration of new user
            case "reg":
                if (message.length < 3) {
                    socket.write('1');
                    break;
                }
                db.deleteUser(socket.id, function (err) {
                    console.log(err);
                });
                db.createUser(socket.id, message[1], message[2], function (err) {
                    if (err) {
                        socket.write('1');
                        console.log(err);
                        return;
                    }
                    socket.verified = true;
                    socket.username = message[2];
                    socket.write('0');
                    console.log("reg ok");
                });
                break;

            //user go in queue
            case "search":
                /* if (socket.verified) {
                     queue.push(socket);
                     console.log("NEW MAN IN QUEUE");
                 }
                 else
                     socket.write('1');*/
                socket.rival = socket;
                socket.write(socket.username + " 0");
                break;

            //game over
            case "gameov":
                if (!socket.verified || socket.rival == null || message.length < 2) {
                    socket.write('1');
                    break;
                }

                db.updateRate(socket.id, message[1] == 'win', function (err) {
                    if (err) {
                        socket.write('1');
                    } else {
                        socket.write('0'); //ok
                    }
                });
                break;

            //set new wall
            case "wall":
                if (socket.rival == null) {
                    console.log("NO RIVAL");
                    //game not started
                    socket.write('1');
                } else if (socket.destroyed) {
                    socket.write('2');
                } else {
                    socket.rival.write(message[1] + ' ' + message[2] + ' ' + message[3] + ' ' + message[4]);
                }
                break;
        }
    });
}).on('error', function (err) {
    console.log(err);
}).listen(8080, function () {
    console.log("listening");

    function flushQueue() {
        var s = [queue, queue = []][0];
        //now s = queue
        //queue = []
        while (s.length > 1) {
            //trying get alive user
            var x1 = s.pop();
            while (x1.destroyed && s.length > 0)
                x1 = s.pop();

            if (x1.destroyed)
                break;
            if (s.length == 0) {
                queue.push(x1);
                break;
            }

            var x2 = s.pop();
            while (x2.destroyed && s.length > 0)
                x2 = s.pop();

            if (x2.destroyed) {
                queue.push(x1);
                break;
            }
            x1.rival = x2;
            x2.rival = x1;
            console.log("STARTED");
            console.log(x1.username);
            console.log(x2.username);
            x1.write(x2.username + " 1");//send start message
            x2.write(x1.username + " 0");//send start message
        }
        if (s.length == 1) {
            queue.push(s[0]);
        }
    }
    setInterval(flushQueue, 3000);
});

