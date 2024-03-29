// private
const util = require('util');
const cipher = require('./cipher')
const debug = require('debug');
const SSH = require('ssh2').Client;
const validator = require('validator');
const request = require('request-promise')
const dnsPromises = require('dns').promises;
const CIDRMatcher = require('cidr-matcher');
const {logDebug, logInfo, logError} = require('./logging');

/**
 * parse conn errors
 * @param {object} socket Socket object
 * @param {object} err    Error object
 */
function connError(socket, err) {
    let msg = util.inspect(err);
    const {session} = socket.request;
    if (err?.level === 'client-authentication') {
        msg = `Authentication failure user=${session.username} from=${socket.handshake.address}`;
        socket.emit('allowreauth', session.ssh.allowreauth);
        socket.emit('reauth');
    }
    if (err?.code === 'ENOTFOUND') {
        msg = `Host not found: ${err.hostname}`;
    }
    if (err?.level === 'client-timeout') {
        msg = `Connection Timeout: ${session.ssh.host}`;
    }
    logError(socket, 'CONN ERROR', msg);
}

/**
 * check ssh host is in allowed subnet
 * @param {object} socket Socket information
 */
async function checkSubnet(socket) {
    let ipaddress = socket.request.session.ssh.host;
    if (!validator.isIP(`${ipaddress}`)) {
        try {
            const result = await dnsPromises.lookup(socket.request.session.ssh.host);
            ipaddress = result.address;
        } catch (err) {
            logError(socket, 'CHECK SUBNET', `${err.code}: ${err.hostname} user=${socket.request.session.username} from=${socket.handshake.address}`);
            socket.emit('ssherror', '404 HOST IP NOT FOUND');
            socket.disconnect(true);
            return;
        }
    }

    const matcher = new CIDRMatcher(socket.request.session.ssh.allowedSubnets);
    if (!matcher.contains(ipaddress)) {
        logError(socket, 'CHECK SUBNET', `Requested host ${ipaddress} outside configured subnets / REJECTED user=${socket.request.session.username} from=${socket.handshake.address}`);
        socket.emit('ssherror', '401 UNAUTHORIZED');
        socket.disconnect(true);
    }
}

/*input check*/
function checkParams(redis, conn, socket) {
    const host = redis.host
    const token = redis.token

    if (!host) {
        logDebug(socket, `PARAMS ERROR: REDIS HOST CAN NOT BE BLANK`);
        logError(socket, 'PARAMS ERROR', 'REDIS HOST CAN NOT BE BLANK');
        conn.end();
        socket.disconnect(true);
        return false
    }

    const regExp = /^((([1-9]?\d|1\d{2}|2[0-4]\d|25[0-5])\.){3}([1-9]?\d|1\d{2}|2[0-4]\d|25[0-5])):([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
    if (!regExp.test(host)) {
        logDebug(socket, `PARAMS ERROR: THIS IS AN INVALID REDIS HOST`);
        logError(socket, 'PARAMS ERROR', 'THIS IS AN INVALID REDIS HOST');
        conn.end();
        socket.disconnect(true);
        return false
    }

    if (!token) {
        logDebug(socket, `PARAMS ERROR: TOKEN CAN NOT BE BLANK`);
        logError(socket, 'PARAMS ERROR', 'TOKEN CAN NOT BE BLANK');
        conn.end();
        socket.disconnect(true);
        return false
    }

    socket.emit('title', host);
    return true
}

/*connect redis*/
async function initLogin(redis, conn, socket, stream) {
    const headers = cipher.getHeaders(redis.token);
    const response = await request.get(redis.params.getRedisUrl, {headers: headers}).catch((errMsg) => {
        logDebug(socket, `TOKEN INVALID: ${errMsg}`);
        logError(socket, 'TOKEN INVALID', errMsg);
        conn.end();
        socket.disconnect(true);
    });

    logInfo(socket, response);
    const result = JSON.parse(response);
    if (!result.flag) {
        logDebug(socket, `TOKEN INVALID: ${result.msg}`);
        logError(socket, 'TOKEN INVALID', result.msg);
        conn.end();
        socket.disconnect(true);
        return
    }
    const password = result.data.requirepass;
    const hostAry = redis.host.split(':')
    const ip = hostAry[0]
    const port = hostAry[1]

    let command;
    if (password) {
        command = `redis-cli -c -h ${ip} -p ${port} -a ${password} \r`
    } else {
        command = `redis-cli -c -h ${ip} -p ${port} \r`
    }
    logInfo(socket, command);
    stream.write(command);
}

// public
module.exports = function appSocket(socket) {
    let login = false;

    socket.once('disconnecting', (reason) => {
        logDebug(socket, `SOCKET DISCONNECTING: ${reason}`);
        if (login === true) {
            // logInfo(socket, `LOGOUT user=${socket.request.session.username} from=${socket.handshake.address} host=${socket.request.session.ssh.host}:${socket.request.session.ssh.port}`);
            login = false;
        }
    });

    async function setupConnection() {
        // if websocket connection arrives without an express session, kill it
        if (!socket.request.session) {
            socket.emit('401 UNAUTHORIZED');
            logDebug(socket, 'SOCKET: No Express Session / REJECTED');
            socket.disconnect(true);
            return;
        }

        // If configured, check that requsted host is in a permitted subnet
        if (socket.request.session?.ssh?.allowedSubnets?.length > 0) {
            checkSubnet(socket);
        }

        const conn = new SSH();
        conn.on('banner', (data) => {
            // need to convert to cr/lf for proper formatting
            socket.emit('data', data.replace(/\r?\n/g, '\r\n').toString('utf-8'));
        });

        conn.on('handshake', (data => {
            socket.emit('setTerminalOpts', socket.request.session.ssh.terminal);
            socket.emit('menu');
            socket.emit('allowreauth', socket.request.session.ssh.allowreauth);
            // socket.emit('footer', `ssh://${socket.request.session.username}@${socket.request.session.ssh.host}:${socket.request.session.ssh.port}`);
            if (socket.request.session.ssh.header.background)
                socket.emit('headerBackground', socket.request.session.ssh.header.background);
            if (socket.request.session.ssh.header.name)
                socket.emit('header', socket.request.session.ssh.header.name);
        }));

        conn.on('ready', () => {
            logDebug(socket, `CONN READY: LOGIN: user=${socket.request.session.username} from=${socket.handshake.address} host=${socket.request.session.ssh.host} port=${socket.request.session.ssh.port} allowreplay=${socket.request.session.ssh.allowreplay} term=${socket.request.session.ssh.term}`);
            //logInfo(socket, `LOGIN user=${socket.request.session.username} from=${socket.handshake.address} host=${socket.request.session.ssh.host}:${socket.request.session.ssh.port}`);
            login = true;
            socket.emit('status', 'SSH CONNECTION ESTABLISHED');
            socket.emit('statusBackground', 'green');
            socket.emit('allowreplay', socket.request.session.ssh.allowreplay);
            const {term, cols, rows, redis} = socket.request.session.ssh;
            conn.shell({term, cols, rows, redis}, async (err, stream) => {
                if (err) {
                    logError(socket, `EXEC ERROR`, err);
                    conn.end();
                    socket.disconnect(true);
                    return;
                }

                socket.once('disconnect', (reason) => {
                    logDebug(socket, `CLIENT SOCKET DISCONNECT: ${util.inspect(reason)}`);
                    conn.end();
                    socket.request.session.destroy();
                });

                socket.on('error', (errMsg) => {
                    logDebug(socket, `SOCKET ERROR: ${errMsg}`);
                    logError(socket, 'SOCKET ERROR', errMsg);
                    conn.end();
                    socket.disconnect(true);
                });

                socket.on('control', (controlData) => {
                    if (controlData === 'replayCredentials' && socket.request.session.ssh.allowreplay) {
                        // stream.write(`${socket.request.session.userpassword}\n`);
                    }
                    logDebug(socket, `SOCKET CONTROL: ${controlData}`);
                });

                socket.on('resize', (data) => {
                    // stream.setWindow(data.rows, data.cols);
                    logDebug(socket, `SOCKET RESIZE: ${JSON.stringify([data.rows, data.cols])}`);
                });

                if (checkParams(redis, conn, socket)) {
                    await initLogin(redis, conn, socket, stream)
                }

                socket.on('data', (data) => {
                    if (data[0].charCodeAt(0) === 3) {
                        socket.emit('data', 'ERR unknown command ');
                        return;
                    }
                    stream.write(data);
                });

                let init = true;
                stream.on('data', (data) => {
                    const msg = data.toString('utf-8');
                    //初始化输出根据:和>判断已经连上redis，在这之前的信息不进行输出
                    if (init && msg.includes(':') && msg.includes('>')) {
                        init = false;
                    }
                    if (!init) {
                        socket.emit('data', msg)
                    }
                });

                stream.on('close', (code, signal) => {
                    logDebug(socket, `STREAM CLOSE: ${util.inspect([code, signal])}`);
                    if (socket.request.session?.username && login === true) {
                        //logInfo(socket, `LOGOUT user=${socket.request.session.username} from=${socket.handshake.address} host=${socket.request.session.ssh.host}:${socket.request.session.ssh.port}`);
                        login = false;
                    }
                    if (code !== 0 && typeof code !== 'undefined')
                        logError(socket, 'STREAM CLOSE', util.inspect({message: [code, signal]}));
                    socket.disconnect(true);
                    conn.end();
                });

                stream.stderr.on('data', (data) => {
                    console.error(`STDERR: ${data}`);
                });
            });
        });

        conn.on('end', (err) => {
            if (err) logError(socket, 'CONN END BY HOST', err);
            logDebug(socket, 'CONN END BY HOST');
            socket.disconnect(true);
        });

        conn.on('close', (err) => {
            if (err) logError(socket, 'CONN CLOSE', err);
            logDebug(socket, 'CONN CLOSE');
            socket.disconnect(true);
        });

        conn.on('error', (err) => connError(socket, err));

        conn.on('keyboard-interactive', (_name, _instructions, _instructionsLang, _prompts, finish) => {
            logDebug(socket, 'CONN keyboard-interactive');
            finish([socket.request.session.userpassword]);
        });

        if (socket.request.session.username && (socket.request.session.userpassword || socket.request.session.privatekey) && socket.request.session.ssh) {
            const {ssh} = socket.request.session;
            ssh.username = socket.request.session.username;
            ssh.password = socket.request.session.userpassword;
            ssh.tryKeyboard = true;
            ssh.debug = debug('ssh2');
            conn.connect(ssh);
        } else {
            logDebug(socket, `CONN CONNECT: Attempt to connect without session.username/password or session varialbles defined, potentially previously abandoned client session. disconnecting websocket client.\r\nHandshake information: \r\n  ${util.inspect(socket.handshake)}`);
            socket.emit('ssherror', 'WEBSOCKET ERROR - Refresh the browser and try again');
            socket.request.session.destroy();
            socket.disconnect(true);
        }
    }

    setupConnection();
};
