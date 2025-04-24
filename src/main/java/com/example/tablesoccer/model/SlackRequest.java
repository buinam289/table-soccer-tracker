package com.example.tablesoccer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackRequest {
    private String type;
    
    @JsonProperty("callback_id")
    private String callbackId;
    
    @JsonProperty("trigger_id")
    private String triggerId;
    
    private SlackMessage message;
    private SlackUser user;
    private SlackChannel channel;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlackMessage {
        private String text;
        private String user;
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public String getUser() {
            return user;
        }
        
        public void setUser(String user) {
            this.user = user;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlackUser {
        private String id;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlackChannel {
        private String id;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCallbackId() {
        return callbackId;
    }
    
    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }
    
    public String getTriggerId() {
        return triggerId;
    }
    
    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }
    
    public SlackMessage getMessage() {
        return message;
    }
    
    public void setMessage(SlackMessage message) {
        this.message = message;
    }
    
    public SlackUser getUser() {
        return user;
    }
    
    public void setUser(SlackUser user) {
        this.user = user;
    }
    
    public SlackChannel getChannel() {
        return channel;
    }
    
    public void setChannel(SlackChannel channel) {
        this.channel = channel;
    }
}