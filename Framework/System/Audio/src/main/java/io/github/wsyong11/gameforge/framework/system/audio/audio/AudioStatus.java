package io.github.wsyong11.gameforge.framework.system.audio.audio;

public enum AudioStatus {
    LOADING,    // 异步加载 metadata
    READY,      // metadata 准备好了
    FAILED,     // 加载或解析失败
    CLOSED      // 已释放
}
