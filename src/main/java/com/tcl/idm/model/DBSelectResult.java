package com.tcl.idm.model;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月21日 下午5:44:22
 */
public class DBSelectResult
{
	private ResultSet resultSet;

	private Statement statement;

	public ResultSet getResultSet()
	{
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet)
	{
		this.resultSet = resultSet;
	}

	public Statement getStatement()
	{
		return statement;
	}

	public void setStatement(Statement statement)
	{
		this.statement = statement;
	}

}
