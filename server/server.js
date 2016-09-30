
var net = require('net');
var db = require('./db/db');
var WebSocketServer = require('ws').Server,
    wss = new WebSocketServer({ port: 8080 });

wss.on('connection', function(ws) {
    ws.on('message', function(mes){
        var message = mes.split(' ');
        switch (message[0]) {
            case "connect":
                if (ws.id)
                    break;
                ws.id = message[1];
                ws.verified = false;
                break;

            case "ver":
                checkPass(ws.id, message[1], function(err){
                    if (!err){
                        ws.verified = true;
                        ws.write('success');
                    }
                });
                break;

            case "reg":
                db.createUser(ws.id, message[1], function(err){
                    if (err)
                        ws.write('Fail');
                    else ws.write('success');
                });
                break;

            case "reset":
                if (ws.verified)
                    db.deleteUser(ws.id, function(err){
                        if (err)
                            ws.write('fail');
                        ws.write('success');
                        ws.end("bue");
                    });
                break;

            case "search":
                if (ws.verified)
                    db.addToQueue(ws, function(err){
                        if (err)
                            socket.write("fail")
                        else socket.write('success');
                    });
                break;

            case "gameov":
                if (ws.verified)
                    db.updateRate(ws.id, message[1] == 'win', function(err){
                        if (err)
                            socket.write('fail')
                        else socket.write('success');
                    });
                break;
        }
    });
});

function flushQueue(id) {
    db.pullQueue(id, function(err, queue){
        var s = queue.queue;
        
        while (s.length > 1){
            var x1 = s.pop(),
                x2 = s.pop();
            x1.send(x2._socket.remoteAddress);
            x2.send(x1._socket.remoteAddress);
        }
        if (s.length == 1){
            db.addToQueue(socket, function(err){
                if (err)
                    console.error("flushing fail");
            });
        }
    });
}
setInterval(flushQueue.bind(this, 1), 3000);
setInterval(flushQueue.bind(this, 2), 5000);