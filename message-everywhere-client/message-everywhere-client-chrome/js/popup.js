const server = 'http://127.0.0.1:8080/message-everywhere/'

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('query').onclick = query
})

var query = element => fetch(server + 'hello').then(d => console.log(d))
