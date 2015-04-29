package com.tcl.idm.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartTimerListener implements ServletContextListener
{
	private ListenerDeamon messageListenerDeamon = null;

	/**
	 * 创建一个初始化监听器对象，一般由容器调用
	 */
	public StartTimerListener()
	{
		super();
	}

	/**
	 * 让Web程序运行的时候自动加载Timer
	 */
	@Override
	public void contextInitialized(ServletContextEvent e)
	{
		if (null == messageListenerDeamon)
		{
			messageListenerDeamon = new ListenerDeamon();
		}

		messageListenerDeamon.start();
	}

	/**
	 * 该方法由容器调用 空实现
	 */
	@Override
	public void contextDestroyed(ServletContextEvent e)
	{
		if (null != messageListenerDeamon)
		{
			messageListenerDeamon.interrupt();
		}
	}
}
