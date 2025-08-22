// config (from Thymeleaf / window.AGORA_CONF)
const __CONF__ = (typeof window !== 'undefined' && window.AGORA_CONF) ? window.AGORA_CONF : {};
const APP_ID = __CONF__.appId ?? null;               // fail explicitly if missing
const TOKEN = __CONF__.token ?? null;               // null if no token (dev only)
const CHANNEL = __CONF__.channel ?? 'main';
const UID = (__CONF__.uid !== undefined && __CONF__.uid !== null) ? Number(__CONF__.uid) : null;

// client config (rtc / vp8 is fine; change codec to 'h264' for Safari if needed)
const client = AgoraRTC.createClient({ mode: 'rtc', codec: 'vp8' });

let localTracks = [];       // [audioTrack, videoTrack]
let remoteUsers = {};       // map uid -> IAgoraRTCRemoteUser
let localUid = null;        // store assigned uid after join

// utility: guard
function ensureConfig() {
    if (!APP_ID) {
        console.error('Agora APP_ID missing. Cannot join channel.');
        alert('Configuration error: missing Agora App ID.');
        throw new Error('Missing Agora App ID');
    }
}

async function joinAndDisplayLocalStream() {
    ensureConfig();

    // add listeners ONCE (or guard to avoid double registration)
    client.on('user-published', handleUserJoined);
    client.on('user-unpublished', handleUserUnpublished);
    client.on('user-left', handleUserLeft);

    // token renewal handlers (only when tokens are in use)
    if (TOKEN) {
        client.on('token-privilege-will-expire', async () => {
            console.log('Token will expire soon — requesting new token from server...');
            try {
                const res = await fetch(`/api/video/token/refresh?channel=${encodeURIComponent(CHANNEL)}`);
                const { token: newToken } = await res.json();
                if (newToken) {
                    await client.renewToken(newToken);
                    console.log('Token renewed');
                } else {
                    console.warn('Server returned empty token; cannot renew.');
                }
            } catch (err) {
                console.error('Failed to renew token', err);
            }
        });

        client.on('token-privilege-did-expire', async () => {
            console.warn('Token expired — need to re-join with new token');
            // Optionally trigger a UI prompt to rejoin
        });
    }

    try {
        // Join: order -> (appId, channel, token, uid). Use server-injected UID to match token binding.
        localUid = await client.join(APP_ID, CHANNEL, TOKEN, UID);

        // create local tracks (call triggered by user gesture so browser will allow)
        localTracks = await AgoraRTC.createMicrophoneAndCameraTracks();

        // render local video element
        const player = `<div class="video-container" id="user-container-${localUid}">
                      <div class="video-player" id="user-${localUid}"></div>
                    </div>`;
        document.getElementById('video-streams').insertAdjacentHTML('beforeend', player);
        localTracks[1].play(`user-${localUid}`);

        // publish
        await client.publish([localTracks[0], localTracks[1]]);
        console.log('Published local tracks');
    } catch (err) {
        console.error('Failed to join / publish', err);
        alert('Could not join the channel. Check console for details.');
    }
}

async function handleUserJoined(user, mediaType) {
    remoteUsers[user.uid] = user;
    await client.subscribe(user, mediaType);

    if (mediaType === 'video') {
        // remove if existing placeholder
        const existing = document.getElementById(`user-container-${user.uid}`);
        if (existing) existing.remove();

        const player = `<div class="video-container" id="user-container-${user.uid}">
                      <div class="video-player" id="user-${user.uid}"></div>
                    </div>`;
        document.getElementById('video-streams').insertAdjacentHTML('beforeend', player);

        // user.videoTrack becomes available after subscribe
        user.videoTrack.play(`user-${user.uid}`);
    }

    if (mediaType === 'audio') {
        user.audioTrack.play();
    }
}

async function handleUserUnpublished(user, mediaType) {
    // user stopped sending track (but may still be in channel)
    if (mediaType === 'video') {
        const el = document.getElementById(`user-container-${user.uid}`);
        if (el) {
            // hide or show placeholder
            el.remove();
        }
    }
    if (mediaType === 'audio') {
        // nothing to do; remote audio track is stopped
    }
}

async function handleUserLeft(user) {
    delete remoteUsers[user.uid];
    const el = document.getElementById(`user-container-${user.uid}`);
    if (el) el.remove();
}

async function leaveAndRemoveLocalStream() {
    try {
        // stop local tracks
        for (const t of localTracks) {
            if (!t) continue;
            t.stop();
            t.close();
        }

        // leave the channel
        await client.leave();

        // cleanup listeners
        client.off('user-published', handleUserJoined);
        client.off('user-unpublished', handleUserUnpublished);
        client.off('user-left', handleUserLeft);

        // reset UI
        document.getElementById('join-btn').style.display = 'block';
        document.getElementById('stream-controls').style.display = 'none';
        document.getElementById('video-streams').innerHTML = '';
        localTracks = [];
        remoteUsers = {};
        localUid = null;
    } catch (err) {
        console.error('Error leaving channel', err);
    }
}

async function toggleMic(e) {
    if (!localTracks[0]) return;
    // use setMuted for quick mute/unmute (keeps capture alive)
    const isMuted = localTracks[0].muted;
    await localTracks[0].setMuted(!isMuted);
    e.target.innerText = isMuted ? 'Mic on' : 'Mic off';
    e.target.style.backgroundColor = isMuted ? 'cadetblue' : '#EE4B2B';
}

async function toggleCamera(e) {
    if (!localTracks[1]) return;
    const isMuted = localTracks[1].muted;
    await localTracks[1].setMuted(!isMuted);
    e.target.innerText = isMuted ? 'Camera on' : 'Camera off';
    e.target.style.backgroundColor = isMuted ? 'cadetblue' : '#EE4B2B';
}

document.getElementById('join-btn').addEventListener('click', async () => {
    await joinAndDisplayLocalStream();
    document.getElementById('join-btn').style.display = 'none';
    document.getElementById('stream-controls').style.display = 'flex';
});
document.getElementById('leave-btn').addEventListener('click', leaveAndRemoveLocalStream);
document.getElementById('mic-btn').addEventListener('click', toggleMic);
document.getElementById('camera-btn').addEventListener('click', toggleCamera);
