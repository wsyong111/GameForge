package io.github.wsyong11.gameforge.framework.system.audio.audio.stream;

public enum AudioStreamStatus {
    READY,          // 未开始播放
    BUFFERING,      // 正在解码 / 填充缓冲
    PLAYABLE,       // 缓冲够播放 / 可直接播放
    FINISHED,       // 播放完成
    ERROR           // 播放失败 / 解码失败
}
