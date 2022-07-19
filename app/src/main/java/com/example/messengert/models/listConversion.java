package com.example.messengert.models;

import java.util.ArrayList;
import java.util.List;

public class listConversion {
    private List<ChatMessage> chatMessageList= new ArrayList<>();

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }
    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }
}
