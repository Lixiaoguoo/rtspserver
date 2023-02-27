package com.rtsp.param;

/**
 * @author pl
 * @date 2020/2/20
 * @since jdk1.8
 */
public class VideoCmd {

    private String action;//请求动作 preview 预览，playback 回放
    private String devid;// 设备ID
    private int ch;// 通道ID
    private int stream;// 码流类型
    private int type;// 视频类型
    private int mem;// 存储类型
    private long start;// 开始时间
    private long end;// 结束时间
    private int playype;//  回放类型
    private int playspeed;// 回放速度

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDevid() {
        return devid;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }

    public int getCh() {
        return ch;
    }

    public void setCh(int ch) {
        this.ch = ch;
    }

    public int getStream() {
        return stream;
    }

    public void setStream(int stream) {
        this.stream = stream;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getPlayype() {
        return playype;
    }

    public void setPlayype(int playype) {
        this.playype = playype;
    }

    public int getPlayspeed() {
        return playspeed;
    }

    public void setPlayspeed(int playspeed) {
        this.playspeed = playspeed;
    }
}
