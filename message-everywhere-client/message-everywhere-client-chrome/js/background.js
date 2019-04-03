const serverURL = 'http://127.0.0.1:8080/message-everywhere/'

var autoFetch = true
var messages = []

chrome.runtime.onMessage.addListener(function (request, sender, callback) {
    console.info('bg msg', request)
    switch (request.type) {
        case 'fetch':
            fetchMsg()
            break;
        case 'autoFetch':
            autoFetch = request.value
            observeMsg(autoFetch)
            break;
        case 'isAutoFetch':
            callback(autoFetch)
            break;
        case 'getMessages':
            callback(messages)
            break;
    }
})

function fetchMsg() {
    console.info('fetch')
}

observeMsg(autoFetch)

var observe = null
function observeMsg(enable) {
    if (enable) {
        if (observe && observe.readyState != EventSource.CLOSED) {
            console.debug('Observer exists, return directly')
            return
        }
        observe = new EventSource(serverURL + 'observe')
        observe.onopen = event => {
            console.info('Start observe msg', event)
        }
        observe.onerror = event => {
            console.warn('Msg observer error', event)
        }
        observe.onmessage = event => {
            var msg = JSON.parse(event.data)
            console.debug('Recieve msg', msg)
            messages.push(msg)
            chrome.runtime.sendMessage({
                type: 'newMessage',
                data: msg
            })
        }
    } else if (observe) {
        console.debug('To close observe')
        observe.close()
    }
}