// yes this is ai generated
// Function to fetch and parse the JSON config file
function loadConfig() {
    // Create a new XMLHttpRequest object
    var xhr = new XMLHttpRequest();

    // Configure it: GET-request for the URL /path/to/config.json
    xhr.open('GET', './config.json', false); // 'false' makes the request synchronous

    try {
        // Send the request over the network
        xhr.send();

        // Check if the request was successful
        if (xhr.status != 200) {
            throw new Error(`HTTP error! Status: ${xhr.status}`);
        }

        // Parse the JSON response
        var config = JSON.parse(xhr.responseText);

        // Log the config to the console (or use it in your application)
        //console.log(config);

        // Example of how to use the config values
        // var apiUrl = config.apiUrl;
        // var timeout = config.timeout;
        // var retryAttempts = config.retryAttempts;

        return config;
    } catch (error) {
        console.error('Error fetching the config file:', error);
    }
}

// Call the function to load the config
let config = loadConfig();

// Create a new WebSocket connection to the server
const port = config.port;
const host = config.host
const socketUrl = 'ws://' + host + ':' + port;
console.log(socketUrl);
const socket = new WebSocket(socketUrl);

function getFileTypeFromUrl(url) {
    const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'webp'];
    const videoExtensions = ['mp4', 'webm', 'mov'];
    const audioExtensions = ['mp3', 'wav', 'ogg'];

    let extension = url.split('.').pop().toLowerCase();
    if (extension.includes("?")) {
        extension = extension.split('?').at(0);
    }

    if (imageExtensions.includes(extension)) {
        return 'image';
    } else if (videoExtensions.includes(extension)) {
        return 'video';
    } else if (audioExtensions.includes(extension)) {
        return 'audio';
    } else {
        return 'unknown';
    }
}

// Event handler for when the connection is established
socket.addEventListener('open', (event) => {
    console.log('Connected to WebSocket server');
});

const text = document.getElementById("livechat-text");
const img = document.getElementById('livechat-image');
const video = document.getElementById('livechat-video');
const user = document.getElementById('livechat-user');
const avatar = document.getElementById('livechat-avatar');

let isText = false;
let isImg = false;
let isVideo = false;
let isAudio = false;

function hideDiscord() {
    if (isText || isImg || isVideo || isAudio) return;
    user.style.visibility = "hidden";
    avatar.style.visibility = "hidden";
}

socket.addEventListener('message', (event) => {
    let data = event.data;
    if (data.startsWith("text: ")) {
        data = data.substring(6);

        text.textContent = data;
        text.style.visibility = "visible"
        isText = true;
        setTimeout(() => {
            text.style.visibility = "hidden";
            isText = false;
            hideDiscord();
        }, 2000 + 150*data.length);
    }
    if (data.startsWith("url: ")) {
        data = data.substring(5);
        let type = getFileTypeFromUrl(data);
        console.log("receive url " + type + " " + data);
        if (type === "image") {
            img.src = "";
            img.src = data;
            img.style.visibility = "visible"
            isImg = true;
            setTimeout(() => {
                img.style.visibility = "hidden";
                isImg = false;
                hideDiscord();
            }, 5000);
        } else if (type === "audio") {
            const audio = new Audio(data);
            audio.play();
            isAudio = true;
            audio.addEventListener('ended', () => {
                isAudio = false;
                hideDiscord();
            });
        } else if (type === "video") {
            video.src = "";
            video.src = data;
            video.style.visibility = "visible"
            video.play();
            isVideo = true;

            video.addEventListener('ended', () => {
                video.style.visibility = "hidden"
                isVideo = false;
                hideDiscord();
            });
        }
    }
    if (data.startsWith("discord: ")) {
        data = data.substring(9);
        const parts = data.split('-');
        const username = parts[0];
        const avatarUrl = parts[1];
        user.textContent = username;
        user.style.visibility = "visible"
        avatar.src = avatarUrl;
        avatar.style.visibility = "visible"
    }
});

// Event handler for when the connection is closed
socket.addEventListener('close', (event) => {
    console.log('Disconnected from WebSocket server');
});

// Event handler for when an error occurs
socket.addEventListener('error', (error) => {
    console.error('WebSocket error:', error);
});