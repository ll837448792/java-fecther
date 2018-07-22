package com.smallsoup.csdn.service;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Queues;
import com.smallsoup.csdn.service.model.Email;

@Component
public class EmailPoolService implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailPoolService.class);
	private static ConcurrentLinkedQueue<Email> emailQueue = Queues.newConcurrentLinkedQueue();

	private static final Object LOCKOBJ = new Object();
	private static volatile int size;

	@Autowired
	private SendEmailService sendEmailService;

	public void putEmailToQueue(Email email) {
		if (null == email) {
			return;
		}
		synchronized (LOCKOBJ) {
			emailQueue.add(email);
			size += 1;
			System.out.println("[EmailPoolService] putEmailToQueue queue size is {}"+ size);
			LOGGER.info("[EmailPoolService] putEmailToQueue queue size is {}", size);
		}
	}

	public Email getEmailFromQueue() {
		synchronized (LOCKOBJ) {

			if (emailQueue.isEmpty()) {
				size = 0;
				System.out.println("[EmailPoolService] getEmailFromQueue queue size is 0");
				LOGGER.info("[EmailPoolService] getEmailFromQueue queue size is 0");
				return null;
			}
			Email email = emailQueue.poll();
			size -= 1;
			System.out.println("[EmailPoolService] getEmailFromQueue queue size is "+ size);
			LOGGER.info("[EmailPoolService] getEmailFromQueue queue size is {}", size);
			return email;
		}
	}

	@Override
	public void run() {

		synchronized (LOCKOBJ) {

			Email email = getEmailFromQueue();

			if (null == email) {
				return;
			}
			System.out.println("[EmailPoolService] run will send email "+ email);
			LOGGER.info("[EmailPoolService] run will send email {}", email);
			try {
				System.out.println("sendEmailService is "+sendEmailService);
				new SendEmailService().sendEmail(email);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
