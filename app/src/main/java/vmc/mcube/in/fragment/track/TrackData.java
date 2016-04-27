package vmc.mcube.in.fragment.track;

import java.util.Date;

/**
 * Created by mukesh on 7/7/15.
 */
public class TrackData {
    private String callId;
    private String callFrom;
    private String callerName;
    private String groupName;
    private Date callTime;
    private String status;
    private String callTimeString;
    private String audioLink;

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getCallTimeString() {
        return callTimeString;
    }

    public void setCallTimeString(String callTimeString) {
        this.callTimeString = callTimeString;
    }

    public TrackData() {
    }

    public TrackData(String callId, String groupName) {
        this.callId = callId;
        this.groupName = groupName;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallFrom() {
        return callFrom;
    }

    public void setCallFrom(String callFrom) {
        this.callFrom = callFrom;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getCallTime() {
        return callTime;
    }

    public void setCallTime(Date callTime) {
        this.callTime = callTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
