package com.kx.netty.nettycommserver.netty;

import java.io.Serializable;

/**
 * 　　* @description: TODO
 * 　　* @author kx
 * 　　* @date 2020/04/05 17:28
 *
 */
public class DataContent implements Serializable {

    private Integer action;		// 动作类型
    private ChatMsg chatMsg;	// 用户的聊天内容entity
    private String extand;		// 扩展字段

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getExtand() {
        return extand;
    }

    public void setExtand(String extand) {
        this.extand = extand;
    }
}
