package com.tcl.idm.connectionpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.tcl.idm.util.AESUtils;
import com.tcl.idm.util.CommonService;

/**
 * @author chenyanlin
 *
 */
public class DBConnectionManager
{
	static private DBConnectionManager instance;//唯一数据库连接池管理实例类
	@SuppressWarnings("unused")
	static private int clients;                 //客户连接数
	@SuppressWarnings("rawtypes")
	private final Hashtable pools = new Hashtable();//连接池

	/**
	 * 读取配置文件
	 * 
	 * @return
	 */
	public DSConfigBean readConfigInfo()
	{
		DSConfigBean dscBean = new DSConfigBean();
		dscBean.setType(CommonService.getJDBCConfValue("jdbc.type"));
		dscBean.setName(CommonService.getJDBCConfValue("jdbc.name"));
		dscBean.setDriver(CommonService.getJDBCConfValue("jdbc.driver"));
		dscBean.setUrl(CommonService.getJDBCConfValue("jdbc.url"));
		dscBean.setUsername(CommonService.getJDBCConfValue("jdbc.username"));
		dscBean.setPassword(AESUtils.decrypt(CommonService.getJDBCConfValue("jdbc.password")));
		dscBean.setMaxconn(Integer.valueOf(CommonService.getJDBCConfValue("jdbc.maxconn")));

		return dscBean;
	}

	/**
	 * 实例化管理类
	 */
	public DBConnectionManager()
	{
		init();
	}

	/**
	 * 得到唯一实例管理类
	 * @return
	 */
	static synchronized public DBConnectionManager getInstance()
	{
		if (DBConnectionManager.instance == null)
		{
			DBConnectionManager.instance = new DBConnectionManager();
		}
		return DBConnectionManager.instance;

	}

	/**
	 * 释放连接
	 * @param name
	 * @param con
	 */
	public void freeConnection(String name, Connection con)
	{
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);//根据关键名字得到连接池
		if (pool != null)
		{
			pool.freeConnection(con);//释放连接 
		}
	}

	/**
	 * 得到一个连接根据连接池的名字name
	 * @param name
	 * @return
	 */
	public Connection getConnection(String name)
	{
		DBConnectionPool pool = null;
		Connection conn = null;

		//从名字中获取连接池
		pool = (DBConnectionPool) pools.get(name);

		//从选定的连接池中获得连接
		conn = pool.getConnection();
		try
		{
			conn.setAutoCommit(false);
		}
		catch (SQLException e)
		{
		}

		return conn;
	}

	/**
	 * 得到一个连接，根据连接池的名字和等待时间
	 * @param name
	 * @param time
	 * @return
	 */
	public Connection getConnection(String name, long timeout)
	{
		DBConnectionPool pool = null;
		Connection con = null;
		pool = (DBConnectionPool) pools.get(name);//从名字中获取连接池
		con = pool.getConnection(timeout);//从选定的连接池中获得连接
		return con;
	}

	/**
	 * 释放所有连接
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void release()
	{
		Enumeration allpools = pools.elements();
		while (allpools.hasMoreElements())
		{
			DBConnectionPool pool = (DBConnectionPool) allpools.nextElement();
			if (pool != null)
			{
				pool.release();
			}
		}
		pools.clear();
	}

	/**
	 * 创建连接池
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	private void createPools(DSConfigBean dsb)
	{
		DBConnectionPool dbpool = new DBConnectionPool();
		dbpool.setName(dsb.getName());
		dbpool.setDriver(dsb.getDriver());
		dbpool.setUrl(dsb.getUrl());
		dbpool.setUser(dsb.getUsername());
		dbpool.setPassword(dsb.getPassword());
		dbpool.setMaxConn(dsb.getMaxconn());
		pools.put(dsb.getName(), dbpool);
	}

	/**
	 * 初始化连接池的参数
	 */
	private void init()
	{
		//创建连接池
		createPools(readConfigInfo());
	}

}
