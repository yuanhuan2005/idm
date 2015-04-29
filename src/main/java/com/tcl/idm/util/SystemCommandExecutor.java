package com.tcl.idm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.CommandExecutedResult;

public class SystemCommandExecutor
{
	private static String outputMsg = "";

	private static String errorMsg = "";

	private static final Log DEBUGGER = LogFactory.getLog(SystemCommandExecutor.class);

	/**
	 * 执行本地Windows或者Linux命令，并获取返回值和执行结果信息
	 * 
	 * @param cmd
	 *            待执行的命令
	 * @return 命令返回值
	 */
	public static CommandExecutedResult execLocalCommand(String cmd)
	{
		CommandExecutedResult commandExecutedResult = new CommandExecutedResult();
		int exitValue = -1;
		String resultMessage = "";
		commandExecutedResult.setExitValue(exitValue);
		commandExecutedResult.setResultMessage(resultMessage);
		Process process = null;

		try
		{
			process = Runtime.getRuntime().exec(cmd);

			// 获取CPU的核数，如果是单核CPU的话，就不能启动线程去监控输出和错误流了，只能
			if (1 == Runtime.getRuntime().availableProcessors())
			{
				InputStream stderrStream = process.getErrorStream();
				InputStream stdoutStream = process.getInputStream();
				InputStreamReader errStreamReader = new InputStreamReader(stderrStream);
				InputStreamReader outStreamReader = new InputStreamReader(stdoutStream);
				BufferedReader errBufReader = new BufferedReader(errStreamReader);
				BufferedReader outBufReader = new BufferedReader(outStreamReader);
				String errLine = null;
				String outLine = null;
				while (null != (errLine = errBufReader.readLine()) || null != (outLine = outBufReader.readLine()))
				{
					if (null != errLine && !"".equals(errLine))
					{
						SystemCommandExecutor.DEBUGGER.debug(errLine);
						SystemCommandExecutor.errorMsg += errLine;
					}
					if (null != outLine && !"".equals(outLine))
					{
						SystemCommandExecutor.DEBUGGER.debug(outLine);
						SystemCommandExecutor.outputMsg += outLine;
					}
				}
			}
			else
			{
				// 获取进程的标准输入流
				final InputStream inputStream = process.getInputStream();

				// 获取进程的错误流
				final InputStream errorStream = process.getErrorStream();

				// 开启输出流监控进程
				Thread outputStreamThread = new Thread()
				{
					@Override
					public void run()
					{
						BufferedReader br1 = new BufferedReader(new InputStreamReader(inputStream));
						try
						{
							String line1 = null;
							SystemCommandExecutor.outputMsg = "";
							while ((line1 = br1.readLine()) != null)
							{
								if (line1 != null)
								{
									SystemCommandExecutor.outputMsg += line1;
									SystemCommandExecutor.DEBUGGER.debug(line1);
								}
							}
						}
						catch (IOException e)
						{
							SystemCommandExecutor.DEBUGGER.error("Exception: " + e.toString());
						}
						finally
						{
							try
							{
								inputStream.close();
							}
							catch (IOException e)
							{
								SystemCommandExecutor.DEBUGGER.error("Exception: " + e.toString());
							}
						}
					}
				};
				outputStreamThread.start();

				// 开启错误流监控进程
				Thread errorStreamThread = new Thread()
				{
					@Override
					public void run()
					{
						BufferedReader br2 = new BufferedReader(new InputStreamReader(errorStream));
						try
						{
							String line2 = null;
							SystemCommandExecutor.errorMsg = "";
							while ((line2 = br2.readLine()) != null)
							{
								if (line2 != null)
								{
									SystemCommandExecutor.errorMsg += line2;
									SystemCommandExecutor.DEBUGGER.debug(line2);
								}
							}
						}
						catch (IOException e)
						{
							SystemCommandExecutor.DEBUGGER.error("Exception: " + e.toString());
						}
						finally
						{
							try
							{
								errorStream.close();
							}
							catch (IOException e)
							{
								SystemCommandExecutor.DEBUGGER.error("Exception: " + e.toString());
							}
						}
					}
				};
				errorStreamThread.start();

				// 等待输出流和错误流监控进程结束
				while (outputStreamThread.isAlive() || errorStreamThread.isAlive())
				{
					// 只是等待，不做其他操作
					;
				}
			}

			// 获取返回值
			exitValue = process.waitFor();
			if (0 == exitValue)
			{
				resultMessage = SystemCommandExecutor.outputMsg;
			}
			else
			{
				resultMessage = SystemCommandExecutor.errorMsg;
			}
		}
		catch (Exception e)
		{
			SystemCommandExecutor.DEBUGGER.error("Exception: " + e.toString());
		}
		finally
		{
			if (null != process)
			{
				process.destroy();
			}
		}

		commandExecutedResult.setExitValue(exitValue);
		commandExecutedResult.setResultMessage(resultMessage);
		return commandExecutedResult;
	}
}
