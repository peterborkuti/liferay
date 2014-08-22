package com.example.borkuti.scheduler;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;


public class MyScheduler implements MessageListener {
	public void receive(Message arg0) throws MessageListenerException {
		System.out.println("Message:" + arg0.toString());
	}

}
