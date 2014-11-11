package com.lg.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lg.db.DBBackupTool;
import com.lg.db.FileExistesException;

/**
 * 测试前 需要修改jdbuutil中的参数 以及 测试方法里面的sql语句
 * 测试方法单独执行，先备份 后还原
 */
public class BackupTest {

	static DBBackupTool tool;
	@BeforeClass
	public static void init(){
		tool = new DBBackupTool();
	}
	
	/*
	 * 备份select语句  在jdbcutil中配置连接以及数据库
	 */
	
	public void testBackupSql(){
		try {
			tool.backupSql("select *from route where rid <3", "E:/b.txt");
		} catch (FileExistesException e) {//备份的路径不能存在 否则无法执行 sql
			e.printStackTrace();
		}
	}
	
	
	public void testRestoreSql(){
		try {
			boolean bl = tool.restoreTable("route", "E:/b.txt");
			System.out.println(bl);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 备份整个数据库  在jdbcutil中配置
	 */
	
	public void testBackupDB(){
		tool.backupDataBase("E:/a.sql");
		//tool.backupDataBase(host, name, pwd, database, tables, filepath);//指定路径的mysql数据库备份
	}

	public void testRestoreDB(){
		try {
			tool.restoreDataBase("E:/a.sql");
			//tool.restoreDataBase(host, name, pwd, database, filepath);//指定路径的mysql数据库恢复
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBackupDBTables(){
		List<String> tables = new ArrayList<String>();
		tables.add("route");
		tables.add("stepnote");
		tool.backupTables(tables, "E:/c.sql");
	}
	
	//与testRestoreDB一样，还原无法指定table
	public void testRestoreDBTables(){
		try {
			tool.restoreDataBase("E:/c.sql");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
