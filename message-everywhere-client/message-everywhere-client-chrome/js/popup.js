const server = 'http://127.0.0.1:8080/message-everywhere/'

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('fetch').onclick = fetchMsg
    document.getElementById('autoFetch').onclick = observeMsg
})

var fetchMsg = event => fetch(server + 'hello').then(d => {
    var messageList = document.querySelector('.message-list')
    var newItem = document.createElement('li')
    newItem.appendChild(document.createTextNode('hello'))
    messageList.appendChild(newItem)
})

var observe = null
var observeMsg = event => {
    var checked = event.srcElement.checked
    console.debug('Auto Fetch: ', event, checked)
    if (checked) {
        if (observe && observe.readyState != EventSource.CLOSED) {
            console.debug('Observer exists, return directly')
            return
        }
        observe = new EventSource(server + 'observe')
        observe.onopen = event => {
            console.info('Start observe msg', event)
        }
        observe.onerror = event => {
            console.warn('Msg observer error', event)
        }
        observe.onmessage = event => {
            console.debug('Recieve msg', event.data)
        }
    } else if (observe) {
        console.debug('To close observe')
        observe.close()
    }
}