package com.lg.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lg.db.DBBackupTool;
import com.lg.db.FileExistesException;

/**
 * ����ǰ ��Ҫ�޸�jdbuutil�еĲ��� �Լ� ���Է��������sql���
 * ���Է�������ִ�У��ȱ��� ��ԭ
 */
public class BackupTest {

	static DBBackupTool tool;
	@BeforeClass
	public static void init(){
		tool = new DBBackupTool();
	}
	
	/*
	 * ����select���  ��jdbcutil�����������Լ����ݿ�
	 */
	
	public void testBackupSql(){
		try {
			tool.backupSql("select *from route where rid <3", "E:/b.txt");
		} catch (FileExistesException e) {//���ݵ�·�����ܴ��� �����޷�ִ�� sql
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
	 * �����������ݿ�  ��jdbcutil������
	 */
	
	public void testBackupDB(){
		tool.backupDataBase("E:/a.sql");
		//tool.backupDataBase(host, name, pwd, database, tables, filepath);//ָ��·����mysql���ݿⱸ��
	}

	public void testRestoreDB(){
		try {
			tool.restoreDataBase("E:/a.sql");
			//tool.restoreDataBase(host, name, pwd, database, filepath);//ָ��·����mysql���ݿ�ָ�
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
	
	//��testRestoreDBһ������ԭ�޷�ָ��table
	public void testRestoreDBTables(){
		try {
			tool.restoreDataBase("E:/c.sql");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
