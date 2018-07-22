package com.smallsoup.csdn.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smallsoup.csdn.service.CSDNFileDownloadService;
import com.smallsoup.csdn.service.EmailPoolService;
@Component
public class Activator {
	private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);
	
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	@Autowired
	private CSDNFileDownloadService csdnFileDownloadService;
	
	@PostConstruct
	public void init() {
		System.out.println("[Activator] init thread start and loginCsdn...");
		csdnFileDownloadService.loginCsdnPager();
		LOGGER.info("[Activator] init thread start and loginCsdn...");
		executor.scheduleAtFixedRate(  
	            new EmailPoolService(),  
	            0,  
	            10*1000,  
	            TimeUnit.MILLISECONDS); 
	}
}
