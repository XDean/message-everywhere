

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('fetch').onclick = event => chrome.runtime.sendMessage({
        type: 'fetch'
    })
    const checkbox = document.getElementById('autoFetch');
    checkbox.onclick = event => chrome.runtime.sendMessage({
        type: 'autoFetch',
        data: event.srcElement.checked
    })
    chrome.runtime.sendMessage({
        type: 'isAutoFetch'
    }, auto => checkbox.checked = auto)
    chrome.runtime.sendMessage({
        type: 'getMessages'
    }, messages => messages.forEach(element => onNewMessage(element)))
})

chrome.runtime.onMessage.addListener(function (request, sender, callback) {
    switch (request.type) {
        case 'autoFetch':
            document.getElementById('autoFetch').checked = request.data
            break
        case 'newMessage':
            onNewMessage(request.data)
            break
    }
})

function onNewMessage(msg) {
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