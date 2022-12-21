// private
const debug = require('debug');
const util = require('util');

/**
 * generate consistent prefix for log messages
 * with epress session id and socket session id
 * @param {object} socket Socket information
 */
function prefix(socket) {
    return `(${socket.request.sessionID}/${socket.id})`;
}

// public
function logDebug(socket, msg) {
    debug('DEBUG')(`${msg}`);
}

/**
 * audit log to console
 * @param {object} socket Socket information
 * @param {object} msg    log message
 */
function logInfo(socket, msg) {
    console.info(`INFO: ${msg}`);
}

/**
 * logs error to socket client (if connected)
 * and console.
 * @param {object} socket Socket information
 * @param {string} myFunc Function calling this function
 * @param {object} err    error object or error message
 */
function logError(socket, myFunc, err) {
    console.error(`ERROR: ${myFunc}: ${err}`);
    logDebug(socket, `logError: ${myFunc}: ${util.inspect(err)}`);
    if (!socket.request.session) return;
    socket.emit('ssherror', `${myFunc}: ${err}`);
}

module.exports = {logError, logInfo, logDebug};
