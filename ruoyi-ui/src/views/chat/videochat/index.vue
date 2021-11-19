<template>
  <span>
    <span v-if="!chat">
      <div>
        <el-card shadow="hover">
          <el-input v-model="targetName" placeholder="请输入对方用户名"></el-input>
          <el-button type="primary" @click="okClick">确定</el-button>
        </el-card>
      </div>
    </span>
    <span v-if="chat">
       <el-container>
    <video id="remoteView" autoplay playsinline controls style="width:65%;height:100%"></video>
    <span style="width:35%;height:100%">
    <video id="localView" autoplay playsinline controls muted style="width:100%;"></video>
    <el-card style="height: 50%">

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
  </span>
  </el-container>
    </span>
  </span>


</template>

<script>
import { addVideoChatInfo } from '@/api/chat/videochat'

var ws
var remoteView
var localVideo
var localStream
var connections = {}
const peerConnectionConfig = {
  'iceServers': [
    { 'urls': 'stun:stun.xten.com' }
  ]
}
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
    },
    openWebcam() {
      var vm = this
      navigator.mediaDevices.getUserMedia(this.mediaStreamConstraints)
        .then((mediaStream) => {
          localVideo = document.querySelector('#localView')
          localStream = mediaStream
          localVideo.srcObject = mediaStream
          vm.initWebRtc()
        }).catch((error) => {
        console.log('navigator.getUserMedia error: ', error)
      })
    },
    initWebRtc() {
      ws = new WebSocket('ws://' + window.location.hostname + ':8080/videochat/741/' + this.$store.state.user.name)
      ws.onmessage = (message) => {
        let signal = JSON.parse(message.data)
        console.log(signal.type)
        switch (signal.type) {
          case 'init':
            this.handleInit(signal)
            break
          case 'logout':
            this.handleLogout(signal)
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
        }
        ws.onopen = (message) => console.log(message)
        ws.onclose = (message) => console.log(message)
        ws.onerror = (message) => console.log(message)

      }
    },
    handleLogout(signal) {
      let peerId = signal.sender
      if (remoteView != null) {
        remoteView.srcObject = null
      }
      localVideo.srcObject = null
      delete connections[peerId]
      this.chat = false
      window.mediaStreamTrack.stop()
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
      connection.onaddstream = function(event) {
        remoteView = document.getElementById('remoteView')
        remoteView.srcObject = event.stream
      }

      connections[uuid] = connection
      return connection

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
        console.log('Setting remote description by offer from ' + peerId)
        // create an answer for the peedId.
        connection.createAnswer().then(function(sdp) {
          // and after callback set it locally and send to peer
          connection.setLocalDescription(sdp)
          ws.send(JSON.stringify({
            type: 'answer',
            receiver: peerId,
            data: sdp
          }))

        }).catch(function(e) {
          console.log('Error in offer handling.', e)
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
    }
  },
  watch: {
    chat: function() {
      if (this.chat === false) {
        ws.close()
        localVideo.srcObject = null
        if (localStream != null) {
          localStream.getTracks()[0].stop()
          localStream.getTracks()[1].stop()
        }
      }

    }
  }
}
</script>

<style scoped>

</style>
