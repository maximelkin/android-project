
var net = require('net');
var db = require('./db/db');
var openState = require('ws').OPEN;

var queue = [];

//wss.on('connection', function (ws) {
var server = net.createServer(function (ws) {
    ws.setNoDelay(false);
    ws.bufferSize = 0;
    console.log("CONNECTED");
    var accum = "";
    ws.write('hello');
    console.log(ws.write);
    ws.on('end', function () {
        console.log('out');
        ws.id = null;
    });
    ws.on('data', function (mes) {
        accum += mes;
        if (accum.slice(-1) == '#') {
            var message = accum.slice(0, accum.length - 1).split(' ');
            accum = "";
            switch (message[0]) {
                case "con":
                    if (ws.id) {
                        ws.write('1');
                        break;
                    }
                    ws.id = message[1];
                    ws.verified = false;
                    ws.write('0');
                    break;

                case "ver":
                    db.checkPass(ws.id, message[1], function (err) {
                        if (!err) {
                            ws.verified = true;
                            ws.write('0');
                        }
                        else
                            ws.write('1');
                    });
                    break;

                case "reg":
                    db.createUser(ws.id, message[1], function (err) {
                        if (err)
                            ws.write('1');
                        else ws.write('0');
                    });
                    break;

                case "reset":
                    if (ws.verified)
                        db.deleteUser(ws.id, function (err) {
                            if (err)
                                ws.write('1');
                            else
                                ws.write('0');
                        });
                    else
                        ws.write('1');
                    break;

                case "search":
                    if (ws.verified)
                        queue.push(ws);
                    else
                        ws.write('1');
                    break;

                case "gameov":
                    if (ws.verified && ws.rival != null)
                        db.updateRate(ws.id, message[1] == 'win', function (err) {
                            if (err)
                                ws.write('1')
                            else ws.write('0');
                        });
                    else
                        ws.write('1');
                    ws.rival = null;
                    break;
                case "wall":
                    if (ws.rival == null) {
                        ws.write('1');
                        break;
                    }
                    if (ws.rival.readyState != openState) {
                        ws.write('2');//rival leave
                        break;
                    }
                    ws.rival.write(message[1]);
                    break;
            }
        }
    });
}).listen(8080, 'localhost');


function flushQueue(id) {
    var s = queue;

    while (s.length > 1) {
        var x1 = s.pop(),
            x2 = null;
        while (x1.readyState != openState && s.length > 0)
            x1 = s.pop();
        if (x1.readyState != openState)
            break;
        if (s.length == 0) {
            s.push(x1);
            break;
        }
        while (x2.readyState != openState && s.length > 0)
            x2 = s.pop();

        if (x2.readyState != openState) {
            s.push(x1);
            break;
        }
        x1.rival = x2;
        x2.rival = x1;
        x1.write("s");
        x2.write("s");
    }
}
setInterval(flushQueue, 3000);
//setInterval(flushQueue.bind(this, 2), 4000);