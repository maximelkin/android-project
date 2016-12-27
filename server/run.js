var forever = require('forever-monitor');

var child = new (forever.Monitor)('server.js', {
    max: 10,
    silent: true,
    args: []
});

child.on('exit', function () {
    console.log("exit");
});

child.start();