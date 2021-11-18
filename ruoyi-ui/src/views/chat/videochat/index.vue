<template>
  <el-container>
    <video id="remoteView" autoplay playsinline controls  style="width:65%;height:100%" ></video>
    <span style="width:35%;height:100%">
    <video id="localView" autoplay playsinline controls muted style="width:100%;"></video>
    <el-card style="height: 50%">

      <div style="text-align: center;">
        欢迎您，xxxx
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
</template>

<script>
var remoteView
var localVideo

export default {
  name: 'index',
  data() {
    return {
      microphone:"el-icon-microphone",
      volume:"iconfont icon-16gl-volumeMiddle",
      mediaStreamConstraints:{
        video: true,
        audio:true
      },
      localStream: Object
    }
  },
  methods: {
    microphoneClick(){
      if (this.microphone==="el-icon-microphone"){
        this.microphone="el-icon-turn-off-microphone"
      }else{
        this.microphone="el-icon-microphone"
      }
      this.mediaStreamConstraints.audio=!this.mediaStreamConstraints.audio
      this.openWebcam()
    },
    volumeClick(){
      if (this.volume==="iconfont icon-16gl-volumeMiddle"){
        this.volume="iconfont icon-16gl-volumeCross"
      }else{
        this.volume="iconfont icon-16gl-volumeMiddle"
      }

      let remoteVolume=document.getElementById("remoteView");
      remoteVolume.muted=!remoteVolume.muted;
    },
    handoff() {},
    openWebcam() {
      navigator.mediaDevices.getUserMedia(this.mediaStreamConstraints)
        .then((mediaStream) => {
          remoteView = document.querySelector('#remoteView')
          localVideo = document.querySelector('#localView')
          this.localStream = mediaStream
          remoteView.srcObject = mediaStream
          localVideo.srcObject = mediaStream
        }).catch((error) => {
        console.log('navigator.getUserMedia error: ', error)
      })
    }
  },
  created() {
    this.openWebcam()
  }
}
</script>

<style scoped>

</style>
