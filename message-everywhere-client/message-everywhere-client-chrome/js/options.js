document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('autoFetch').onclick = event => {
        chrome.runtime.sendMessage({
            type: 'autoFetch',
            data: event.srcElement.checked
        });
    }
});

chrome.runtime.onMessage.addListener(function (request, sender, callback) {
    switch (request.type) {
        case 'autoFetch':
            document.getElementById('autoFetch').checked = request.data
            break;
    }
})