const serverURL = 'http://127.0.0.1:8080/message-everywhere/'

var options = {
    autoFetch: true
}

var messages = []

document.addEventListener('DOMContentLoaded', function () {
    chrome.storage.sync.get(options, function (items) {
        console.info('Get options', items)
        options = items
        observeMsg(items.autoFetch);
    });
});

chrome.runtime.onMessage.addListener(function (request, sender, callback) {
    console.info('bg msg', request)
    switch (request.type) {
        case 'fetch':
            fetchMsg()
            break;
        case 'autoFetch':
            options.autoFetch = request.data
            observeMsg(request.data)
            saveOptions()
            break;
        case 'newMessage':
            messages.push(request.data)
            saveMessages()
            break;
        case 'isAutoFetch':
            callback(options.autoFetch)
            break;
        case 'getMessages':
            callback(messages)
            break;
    }
})

function fetchMsg() {
    console.info('fetch')
}

var cachedConnection = null
function observeMsg(enable) {
    var oldConnection = cachedConnection
    if (enable) {
        if (oldConnection && oldConnection.readyState != EventSource.CLOSED) {
            console.debug('Observer exists, return directly')
            return
        }
        newConnection = new EventSource(serverURL + 'observe')
        newConnection.onopen = event => {
            console.info('Start observe msg', event)
        }
        newConnection.onerror = event => {
            console.warn('Msg observer error', event)
        }
        newConnection.onmessage = event => {
            var msg = JSON.parse(event.data)
            console.debug('Recieve msg', msg)
            messages.push(msg)
            saveMessages()
            chrome.runtime.sendMessage({
                type: 'newMessage',
                data: msg
            })
        }
        cachedConnection = newConnection
    } else if (oldConnection) {
        console.debug('To close observe')
        oldConnection.close()
    }
}



function saveOptions() {
    chrome.storage.sync.set(options, function () {
        console.info('option saved', options)
    });
}

function saveMessages(){
    var msg = messages
    chrome.storage.sync.set({
        messages: msg
    })
}