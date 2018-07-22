package com.smallsoup.csdn.ui.dto;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

public class OrderInfo {

	private String linkAddr;
	private String emailAddr;
	private String orderNum;

	public String getLinkAddr() {
		return linkAddr;
	}

	public void setLinkAddr(String linkAddr) {
		this.linkAddr = linkAddr;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * 去掉各个属性的前后空格
	 * 
	 * @param orderInfo
	 * @return 去掉前后空格的orderInfo
	 */
	public OrderInfo getTrimOrderInfo() throws IllegalArgumentException, IllegalAccessException {

		Class<? extends OrderInfo> class1 = this.getClass();
		System.out.println("before "+this);
		Field[] fields = class1.getDeclaredFields();
		Field.setAccessible(fields, true);
		for (Field field : fields)  {
			String clazz = field.getType().getName();
			if ("java.lang.String".equals(clazz)) {
				String value = (String)field.get(this);
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				field.set(this, value.trim());
			}
		}
		return this;
	}
	
//	@Test
	public void test(){
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setEmailAddr(" ljasd ");
		orderInfo.setLinkAddr("ljiansi  ");
		orderInfo.setOrderNum("  128190 ");
		try {
			orderInfo.getTrimOrderInfo();
			System.out.println(orderInfo);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderInfo [linkAddr=");
		builder.append(linkAddr);
		builder.append(", emailAddr=");
		builder.append(emailAddr);
		builder.append(", orderNum=");
		builder.append(orderNum);
		builder.append("]");
		return builder.toString();
	}
}
