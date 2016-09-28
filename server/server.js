//bidlo version
var net = require('net');
var db = require('./db');
var s = []

function flushQueue(id) {
    db.pullQueue(id, function(err, queue){
        var s = queue.queue;
        console.log(s.length);
        for (var i = 0; i < parseInt(s.length / 2); i++){
            console.log(s.length, i);
            var x1 = s[i * 2],
                x2 = s[i * 2 + 1];
            x1.end(toString(x2.address()));
            x2.end(toString(x1.address()));
        }
        if (s.length % 2 != 0){
                db.addToQueue(socket, function(err){
                if (err)
                    console.error("flushing fail");
            });
        }
    });
}
const server = net.createServer((socket) => {
    console.log(socket.address());
    socket.setNoDelay(true);
    socket.write('connected');
    console.log(s.length);
    socket.on('connection', (androidId) => {
        if (androidId == null){
            socket.destroy();
            return;
        }
        socket.id = androidId;
    });
    socket.on('registration', (pass) => {
        db.createUser(socket.id, pass, function(err){
            if (err)
                socket.write('Fail');
            socket.write('Success');
        });
    });
    socket.on('search', () => {
        db.addToQueue(socket, function(err){
            if (err)
                socket.write("fail");
        });
    });
    socket.on('game over', (result) => {
        db.updateRate(socket.id, result == 'win', function(err){
            if (err)
                socket.write('fail');
        });
    });

});
server.on('error', (err) => {
  throw err;
});
server.listen(8080, () => {
  console.log('server bound');
});

//setInterval(flushQueue.bind(this, 1), 3000);
//setInterval(flushQueue.bind(this, 2), 5000);