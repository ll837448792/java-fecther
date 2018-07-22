package com.smallsoup.csdn.ui;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.smallsoup.csdn.service.CSDNFileDownloadService;
import com.smallsoup.csdn.service.CheckArgsService;
import com.smallsoup.csdn.service.EmailPoolService;
import com.smallsoup.csdn.service.SendEmailService;
import com.smallsoup.csdn.service.model.Email;
import com.smallsoup.csdn.ui.dto.OrderInfo;

@Component
@Path("/")
public class RestfulUI {
	@Autowired
	private CSDNFileDownloadService csdnFileDownloadService;
	@Autowired
	private SendEmailService sendEmailService;

	@Autowired
	private CheckArgsService checkArgsService;
	@Autowired
	private EmailPoolService emailPoolService;

	@POST
	@Path("/sayHello")
	public String sayHello(@FormParam("orderNum") String orderNum) {

		System.out.println("orderNum is " + orderNum);

		return orderNum;
	}

	@POST
	@Path("/csdnDownFile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response csdnDownFile(OrderInfo orderInfo) {
		System.out.println("OrderInfo is " + orderInfo);

		File file;
		try {
			orderInfo.getTrimOrderInfo();
			checkArgsService.checkArgs(orderInfo);
			file = csdnFileDownloadService.loginCsdnAndDownload(orderInfo);
			Email email = new Email(ImmutableList.of(orderInfo.getEmailAddr()), file);
			emailPoolService.putEmailToQueue(email);
//			sendEmailService.sendEmail(new Email(ImmutableList.of(orderInfo.getEmailAddr()), file));
		} catch (ClientProtocolException e) {
			System.err.println("ClientProtocolException e" + e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (IOException e) {
			if (e instanceof UnknownHostException) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("请检查下载链接是否正确..").build();
			}
			System.err.println("IOException e" + e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (Exception e) {
			System.err.println("Exception e" + e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return Response.status(Response.Status.OK).entity(JSONObject.toJSONString((orderInfo))).build();
	}
}
