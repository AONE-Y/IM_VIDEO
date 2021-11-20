<template>
  <span>
    <span v-if="!chat">
      <div>
        <el-card shadow="hover">
          <el-input v-model="targetName" placeholder="请输入房间号"></el-input>
          <el-button type="primary" @click="okClick">确定</el-button>
        </el-card>
      </div>
    </span>
    <span v-if="chat">
       <el-container>
    <video id="remoteView" autoplay playsinline controls style="width:85%;height:100%"></video>
    <div style="width:15%;height:100%">

      <div style="width:100%;height:500px">
        <el-scrollbar style="height:100%">
           <video id="localView" autoplay playsinline muted style="width:100%;"></video>
          <span id="videosContainer"></span>
        </el-scrollbar>

      </div>

    <el-card style="height: 50%;margin-bottom: 0">
      <div style="text-align: center;">
        欢迎您，{{ this.$store.state.user.name }}
      </div>

      <div style="display:flex;justify-content:space-around;margin-top: 30px">
         <el-button :icon="microphone" circle style="width:50px;height:50px" @click="microphoneClick"></el-button>
          <el-button :icon="volume" circle style="width:50px;height:50px" @click="volumeClick"></el-button>
          <el-button circle type="danger" size="medium" style="width:50px;height:50px" @click="handoff"><i
            class="iconfont icon-guaduan1"
          ></i></el-button>
      </div>

    </el-card>
  </div>
  </el-container>
    </span>
  </span>


</template>

<script>

import log from '@/views/monitor/job/log'

var ws
var remoteView
var localView
var localStream
var connections = {}
var container
const peerConnectionConfig = {
  'iceServers': [
    { 'urls': 'stun:stun.xten.com' }
  ]
}
var uuidInBig

export default {
  name: 'index',
  data() {
    return {
      chat: false,
      targetName: '',
      microphone: 'el-icon-microphone',
      volume: 'iconfont icon-16gl-volumeMiddle',
      mediaStreamConstraints: {
        video: true,
        audio: true
      }

    }
  },
  methods: {
    okClick() {
      this.chat = true
      this.openWebcam()
    },
    openWebcam() {
      var vm = this
      navigator.mediaDevices.getUserMedia(this.mediaStreamConstraints)
        .then((mediaStream) => {
          remoteView = document.getElementById('remoteView')
          localView = document.querySelector('#localView')
          remoteView.srcObject = mediaStream
          localStream = mediaStream
          localView.srcObject = mediaStream
          localView.addEventListener('click', function(event) {
            vm.setBigVideo('localView')
          }, false)
          vm.initWebRtc()
        }).catch((error) => {
        console.log('navigator.getUserMedia error: ', error)
      })
    },
    microphoneClick() {
      if (this.microphone === 'el-icon-microphone') {
        this.microphone = 'el-icon-turn-off-microphone'
      } else {
        this.microphone = 'el-icon-microphone'
      }
      this.mediaStreamConstraints.audio = !this.mediaStreamConstraints.audio
      this.openWebcam()
    },
    volumeClick() {
      if (this.volume === 'iconfont icon-16gl-volumeMiddle') {
        this.volume = 'iconfont icon-16gl-volumeCross'
      } else {
        this.volume = 'iconfont icon-16gl-volumeMiddle'
      }

      let remoteVolume = document.getElementById('remoteView')
      remoteVolume.muted = !remoteVolume.muted
    },
    handoff() {
      this.chat = false
      if (remoteView != null) {
        remoteView.srcObject = null
      }
      if (container != null) {
        container.children.forEach((e) => {
          e.outerHTML = ''
        })
      }
      Object.keys(connections).forEach((e) => delete connections[e])

    },
    initWebRtc() {
      ws = new WebSocket('ws://' + window.location.hostname + ':8080/meeting/' + this.targetName + '/ab' + this.$store.state.user.name)
      ws.onmessage = (message) => {
        let signal = JSON.parse(message.data)
        console.log(signal.type)
        switch (signal.type) {
          case 'init':
            this.handleInit(signal)
            break
          case 'offer':
            this.handleOffer(signal)
            break
          case 'answer':
            this.handleAnswer(signal)
            break
          case 'ice':
            this.handleIce(signal)
            break
          case 'logout':
            this.handleLogout(signal)
            break
        }
        ws.onopen = (message) => console.log(message)
        ws.onclose = (message) => console.log(message)
        ws.onerror = (message) => console.log(message)

      }
    },
    handleInit(signal) {
      const peerId = signal.sender
      const connection = this.getRTCPeerConnectionObject(peerId)
      connection.createOffer().then(function(sdp) {
        connection.setLocalDescription(sdp)
        console.log('Creating an offer for', sdp)
        ws.send(JSON.stringify({
          type: 'offer',
          receiver: peerId,
          data: sdp
        }))
      }).catch(function(e) {
        console.log('Error in offer creation.', e)
      })
    },
    handleOffer(signal) {
      const peerId = signal.sender
      const connection = this.getRTCPeerConnectionObject(peerId)
      connection.setRemoteDescription(new RTCSessionDescription(signal.data)).then(function() {
        connection.createAnswer().then(function(sdp) {
          connection.setLocalDescription(sdp)
          ws.send(JSON.stringify({
            type: 'answer',
            receiver: peerId,
            data: sdp
          }))
        }).catch(function(e) {
          console.log('Error in createAnswer.', e)
        })

      }).catch(function(e) {
        console.log('Error in offer handling.', e)
      })
    },
    handleAnswer(signal) {
      var connection = this.getRTCPeerConnectionObject(signal.sender)
      connection.setRemoteDescription(new RTCSessionDescription(signal.data)).then(function() {
        console.log('Setting remote description by answer from' + signal.sender)
      }).catch(function(e) {
        console.log('Error in answer acceptance.', e)
      })
    },
    handleIce(signal) {
      if (signal.data) {
        console.log('Adding ice candidate')
        const connection = this.getRTCPeerConnectionObject(signal.sender)
        connection.addIceCandidate(new RTCIceCandidate(signal.data))
      }
    },
    handleLogout(signal) {
      let peerId = signal.sender
      if (remoteView != null) {
        remoteView.srcObject = localView.srcObject
      }
      delete connections[peerId]
      var videoElement = document.getElementById(peerId)
      videoElement.outerHTML = ''
    },
    getRTCPeerConnectionObject(uuid) {
      if (connections[uuid]) {
        return connections[uuid]
      }
      let connection = new RTCPeerConnection(peerConnectionConfig)
      connection.addStream(localStream)
      connection.onicecandidate = function(event) {
        console.log('ice candidate')
        if (event.candidate) {
          ws.send(JSON.stringify({
            type: 'ice',
            receiver: uuid,
            data: event.candidate
          }))
        }
      }
      let vm = this
      connection.onaddstream = function(event) {
        console.log('Received new stream from ' + uuid)
        container = document.getElementById('videosContainer')
        var video = document.createElement('video')

        container.appendChild(video)
        video.id = uuid
        video.style = 'width:100%'
        video.className += ' videoElement'
        video.autoplay = true
        video.srcObject = event.stream
        video.muted = true
        video.playsInline = true
        video.addEventListener('click', function(event) {
          vm.setBigVideo(uuid)
        }, false)
        //   vm.setBigVideo(uuid)
      }
      connections[uuid] = connection
      return connection
    },
    setBigVideo(uuid) {
      let remote = document.getElementById(uuid)
      remoteView.srcObject = remote.srcObject
      if (uuidInBig && document.getElementById(uuidInBig)) {
        document.getElementById(uuidInBig).classList.remove('active')
      }
      document.getElementById(uuid).classList.add('active')
      uuidInBig = uuid
    }
  },
  watch: {
    chat: function() {
      if (this.chat === false) {
        ws.close()
        localView.srcObject = null
        if (localStream != null) {
          localStream.getTracks()[0].stop()
          localStream.getTracks()[1].stop()
        }
      }
    }
  },
  beforeDestroy() {
   this.handoff()
    ws.close()
    if (localStream != null) {
      localStream.getTracks()[0].stop()
      localStream.getTracks()[1].stop()
    }
  }
}
</script>

<style scoped>

</style>
