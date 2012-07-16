var port = 1110;
var folder = './data/';

var log = require('./log');
var net = require('net');
var Long = require('./long');
var fs = require('fs');
var path = require('path');

// log.setGlobalLevel(log.DEBUG);

function receive(data, global, callback) {
  if (data.received) {
    log.debug(data.desc + ' already received.');
    return;
  }
  var bytesToCopy = global.chunk.length;
  global.bytesLeft = 0;
  if (global.chunk.length + data.contentSize > data.maxSize) {
    bytesToCopy = data.maxSize - data.contentSize;
    global.bytesLeft = global.chunk.length - bytesToCopy;
  }
  log.debug(data.desc + ' buffer, bytes to copy ' + bytesToCopy + ', bytes left ' + global.bytesLeft);
  global.chunk.copy(data.buffer, data.contentSize, 0, bytesToCopy);
  data.contentSize += bytesToCopy;
  if (data.contentSize == data.maxSize) {
    log.debug(data.desc + ' received.');
    data.received = true;
    callback();
  }
  if (global.bytesLeft > 0) {
    global.chunk = global.chunk.slice(global.chunk.length - global.bytesLeft);
  }
}

var server = net.createServer(function(socket) {

  var fileWriteStream = null;
  var fileWriteSize = Long.Long(0, 0);

  var remoteAddress = null;
  var startTime = new Date();

  socket.on('connect', function() {
    remoteAddress = socket.remoteAddress + ':' + socket.remotePort;
    log.debug(remoteAddress + ' connected.');
  });

  var fileName = {
    desc: 'file name',
    received: false,
    maxSize: 260,
    buffer: new Buffer(260),
    contentSize: 0,
    value: "<NO FILE>"
  };

  var fileLength = {
    desc: 'file length',
    received: false,
    maxSize: 8,
    buffer: new Buffer(8),
    contentSize: 0,
    value: Long.Long(0, 0)
  };

  var global = {
    bytesLeft: 0,
    chunk: null
  };

  socket.on('data', function(chunk) {
    log.debug("received chunk " + chunk.length + " bytes");

    global.bytesLeft = chunk.length;
    global.chunk = chunk;

    receive(fileName, global, function() {
      fileName.value = fileName.buffer.toString('ascii');
      log.info(remoteAddress + ' file name received as ' + fileName.value);
      fileWriteStream = fs.createWriteStream(path.join(folder, fileName.value), { flags: 'w+' });
    });

    if (global.bytesLeft == 0) {
      return;
    }

    receive(fileLength, global, function() {
      var fileLength1 = fileLength.buffer.readUInt32LE(0);
      var fileLength2 = fileLength.buffer.readUInt32LE(4);
      fileLength.value = Long.Long(fileLength1, fileLength2);
      log.info(remoteAddress + ' file length received as ' + fileLength.value.toString());
    });

    if (global.bytesLeft == 0) {
      return;
    }

    log.debug("received data " + global.chunk.length + " bytes");
    fileWriteSize = fileWriteSize.add(Long.Long(global.chunk.length, 0));
    fileWriteStream.write(global.chunk);
  });

  socket.on('end', function() {
    log.debug(remoteAddress + ' ended.');
    if (fileWriteSize.equals(fileLength.value)) {
      var speed = fileWriteSize.div(Long.Long((new Date().getTime() - startTime), 0));
      log.info(remoteAddress + ' sent a file as ' + fileName.value + ', size of ' + fileWriteSize.toString() + ', KBPS is ' + speed);
    } else {
      log.error(remoteAddress + ' sent a file as ' + fileName.value + ', size of ' + fileLength.value.toString() + ', but received ' + fileWriteSize.toString());
    }
    if (fileWriteStream) {
      fileWriteStream.end();    
    }
  });

});

logws = fs.createWriteStream("server.log", { flags: 'a+' });
function logToFile(str) {
  console.log(str);
  logws.write(str + "\n");
}
log.setOutputFunc(logToFile);


if (!fs.existsSync(folder)) {
  log.error("cannot find data folder " + folder);
  process.exit(1);
}

server.listen(port, null);

log.info("server running at port " + port);