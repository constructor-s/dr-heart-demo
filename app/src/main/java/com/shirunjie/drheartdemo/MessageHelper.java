package com.shirunjie.drheartdemo;

import io.smooch.core.Message;
import io.smooch.core.Smooch;

/**
 * Created by shirunjie on 16-04-26.
 */
public class MessageHelper {
    public void sendOnMessage() {
        sendMessage(new Message("Heart rate monitoring has been turned on"));
    }

    public void sendOffMessage() {
        sendMessage(new Message("Heart rate monitoring has been turned off"));
    }

    public void sendMessage(Message message) {
        Smooch.getConversation().sendMessage(message);
    }
}
