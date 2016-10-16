
var net = require('net');
var db = require('./db/db');
var openState = require('ws').OPEN;
//var WebSocketServer = require('ws').Server,
//  wss = new WebSocketServer({ port: 8080 });


//wss.on('connection', function (ws) {
var server = net.createServer(function (ws) {
    ws.setNoDelay(false);
    ws.bufferSize = 0;
    console.log("CONNECTED");
    var accum = "";
    console.log(ws.write('123321\0'));
    ws.on('end', function () {
        console.log('out');
        ws.id = null;
    });
    ws.on('data', function (mes) {
        //var message = mes; //mes.split(' ');
        accum += mes;
        console.log(accum);
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
                    console.log('search1');
                    if (ws.verified)
                        db.addToQueue(ws, function (err) {
                            if (err)
                                ws.write('1')
                            else ws.write('0');
                        });
                    else
                        ws.write('1');
                    break;

                case "gameov":
                    if (ws.verified)
                        db.updateRate(ws.id, message[1] == 'win', function (err) {
                            if (err)
                                ws.write('1')
                            else ws.write('0');
                        });
                    else
                        ws.write('1');
                    break;
            }
        }
    });
}).listen(8080, 'localhost');


function flushQueue(id) {
    db.pullQueue(id, function (err, queue) {
        var s = queue.queue;

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
            x1.send(x2._socket.remoteAddress);
            x2.send(x1._socket.remoteAddress);
        }
        if (s.length == 1) {
            db.addToQueue(socket, function (err) {
                if (err)
                    console.error("flushing fail");
            });
        }
    });
}
setInterval(flushQueue.bind(this, 1), 3000);
setInterval(flushQueue.bind(this, 2), 4000);