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
            var msg = JSON.parse(event.data)
            console.debug('Recieve msg', msg)
            var messageList = document.querySelector('.message-list')

            var newItem = document.createElement('li')
            var sender = document.createElement('span')
            sender.className = 'msg-sender'
            sender.appendChild(document.createTextNode(msg.from.name))
            newItem.appendChild(sender)
            var content = document.createElement('span')
            content.className = 'msg-content'
            content.appendChild(document.createTextNode(msg.content))
            newItem.appendChild(content)
            var dateTime = new Date();
            dateTime.setTime(msg.timestamp * 1000);
            newItem.title = dateTime.toLocaleDateString()
            messageList.appendChild(newItem)
        }
    } else if (observe) {
        console.debug('To close observe')
        observe.close()
    }
}