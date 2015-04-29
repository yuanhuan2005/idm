package com.tcl.idm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 文件工具类
 * 
 * @author yuanhuan
 * 2014年3月28日 下午3:14:29
 */
public class FileUtils
{
	private static final Log DEBUGGER = LogFactory.getLog(FileUtils.class);

	/**
	 * 获取文件类型
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 文件类型
	 */
	public static String getFileFormat(String filePath)
	{
		String fileFormat = "";

		// 参数非空检查
		if (null == filePath || "".equals(filePath))
		{
			return fileFormat;
		}

		// 检查文件是否含有类型
		if (filePath.indexOf(".") < 0)
		{
			return fileFormat;
		}

		fileFormat = filePath.substring(filePath.lastIndexOf(".") + 1);

		return fileFormat;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 *            文件路径
	 * @return true表示存在，false表示不存在
	 */
	public static boolean isFileExisted(String filePath)
	{
		try
		{
			File file = new File(filePath);
			if (!file.exists())
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	/**
	 * 判断目录是否可写
	 * 
	 * @param 目录
	 * @return true表示可写，false表示不可写
	 */
	public static boolean isDirWritable(String dir)
	{
		File file = new File(dir);
		if (!file.exists())
		{
			FileUtils.DEBUGGER.error(dir + " not found");
			return false;
		}

		if (!file.isDirectory())
		{
			FileUtils.DEBUGGER.error(dir + " not a directory");
			return false;
		}

		if (!file.canWrite())
		{
			FileUtils.DEBUGGER.error(dir + " cat not write");
			return false;
		}

		return true;
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return true表示删除成功，false表示删除失败
	 */
	public static boolean deleteFile(String filePath)
	{
		try
		{
			File file = new File(filePath);
			if (!file.exists())
			{
				return true;
			}

			return file.delete();
		}
		catch (Exception e)
		{
			FileUtils.DEBUGGER.error("Exception: " + e.toString());
		}

		return false;
	}

	/**
	 * 在目录的后面加上必要的斜杠
	 * 
	 * @param dirPath
	 *            目录路径
	 * @return 加上斜杠的目录路径
	 */
	public static String addSlashToDirPathIfNecessary(String dirPath)
	{
		if (StringUtils.isEmpty(dirPath))
		{
			return dirPath;
		}

		String finalDirPath = dirPath;
		if (!dirPath.endsWith("/"))
		{
			finalDirPath = dirPath + "/";
		}

		return finalDirPath;
	}

	/**
	 * 写入文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileContent
	 *            文件内容
	 * @return true表示写入成功，false表示失败
	 */
	public static boolean writeFile(String filePath, String fileContent)
	{

		FileWriter fw = null;
		try
		{
			fw = new FileWriter(filePath, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(fileContent);
			pw.close();
			fw.close();
			return true;
		}
		catch (IOException e)
		{
			FileUtils.DEBUGGER.error("Exception : " + e.toString());
		}

		return false;
	}

	/**
	 * 根据新文件类型生成新的文件路径
	 * 
	 * @param filePath
	 *            文件路径，例如：/tmp/test.mp4
	 * @param newFormat
	 *            新类型，例如：flv
	 * @return 新文件路径，例如：/tmp/test.flv
	 */
	public static String genFilePathWithNewFormat(String filePath, String newFormat)
	{
		String newFilePath = "";

		// 参数非空检查
		if (StringUtils.isEmpty(filePath))
		{
			FileUtils.DEBUGGER.error("filePath is null");
			return newFilePath;
		}

		// 参数非空检查
		if (StringUtils.isEmpty(filePath))
		{
			FileUtils.DEBUGGER.error("filePath is null");
			newFilePath = filePath;
			return newFilePath;
		}

		// 检查文件是否含有类型
		if (filePath.indexOf(".") < 0)
		{
			newFilePath = filePath + "." + newFormat;
			return newFilePath;
		}

		String filePathWithoutFormat = filePath.substring(0, filePath.lastIndexOf(".") + 1);
		newFilePath = filePathWithoutFormat + newFormat;

		return newFilePath;
	}

	/**
	 * 删除目录
	 * 
	 * @param dirPath
	 *            目录路径
	 */
	public static void deleteFolder(String dirPath)
	{
		if (StringUtils.isEmpty(dirPath))
		{
			return;
		}

		try
		{
			File folder = new File(dirPath);
			File[] files = folder.listFiles();
			if (null != files)
			{
				for (File f : files)
				{
					if (f.isDirectory())
					{
						FileUtils.deleteFolder(f.getAbsolutePath());
					}
					else
					{
						f.delete();
					}
				}
			}
			folder.delete();
		}
		catch (Exception e)
		{
			FileUtils.DEBUGGER.error("Exception: " + e.toString());
		}
	}

	public static void copyFile(String oldPath, String newPath)
	{
		InputStream inStream = null;
		FileOutputStream fs = null;

		try
		{
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists())
			{
				// 文件存在时
				inStream = new FileInputStream(oldPath); // 读入原文件
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1)
				{
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		}
		catch (Exception e)
		{
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}

	public static void moveFile(String oldPath, String newPath)
	{
		FileUtils.copyFile(oldPath, newPath);
		FileUtils.deleteFile(oldPath);
	}

	public static void newFolder(String folderPath)
	{
		try
		{
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists())
			{
				myFilePath.mkdir();
			}
		}
		catch (Exception e)
		{
			FileUtils.DEBUGGER.error("Exception: " + e.toString());
		}
	}

	public static void renameFile(String oldPath, String newPath)
	{
		try
		{
			File dest = null;
			if (!oldPath.equals(newPath))
			{
				File file = new File(oldPath);
				dest = new File(newPath);
				file.renameTo(dest);
			}
		}
		catch (Exception e)
		{
			FileUtils.DEBUGGER.error("Exception: " + e.toString());
		}
	}

}
