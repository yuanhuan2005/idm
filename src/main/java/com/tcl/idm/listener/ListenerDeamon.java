package com.tcl.idm.listener;

import com.tcl.idm.repository.IpaddressRequestLimitRepository;
import com.tcl.idm.repository.UserRequestLimitRepository;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.DateFormatterUtils;

public class ListenerDeamon extends Thread
{
	public ListenerDeamon()
	{
		setDaemon(true);
	}

	@Override
	public void run()
	{
		while (true)
		{
			String currentMinute = DateFormatterUtils.getCurrentUTCMinuteTime();

			// 清理IP请求记录
			IpaddressRequestLimitRepository.deleteIpaddressRequestLimitExceptCurrentMinute(currentMinute);

			// 清理用户请求记录
			UserRequestLimitRepository.deleteUserRequestLimitExceptCurrentMinute(currentMinute);

			// 每隔1分钟清理一次访问记录
			CommonService.sleep(60);
		}
	}
}
