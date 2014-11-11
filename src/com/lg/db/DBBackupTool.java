package com.lg.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import com.lg.db.MysqlUtil.TransExecutes;

public class DBBackupTool {

	
	
	public boolean restoreTable(String table, String filepath)
			throws FileNotFoundException {
		File file = new File(filepath);
		if (!file.exists()) {
			throw new FileNotFoundException("文件不存在 无法导入");
		}
		MysqlUtil db = new MysqlUtil();
		Boolean bl = db.doExecute(new TransExecutes() {

			@Override
			public Boolean doExecute()  {
				String sql = "load data infile '" + filepath + "' into table "
						+ table + " CHARACTER SET utf8;";
				try {
					db.execute(sql, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});
		return bl;
	}

	public boolean backupSql(String sql, String filepath)
			throws FileExistesException {
		File file = new File(filepath);
		if (file.exists()) {
			throw new FileExistesException("已存在该文件,无法导出");
		}
		MysqlUtil db = new MysqlUtil();

		Boolean bl = db.doExecute(new TransExecutes() {

			@Override
			public Boolean doExecute() throws SQLException,
					RunAtErrorFunctionException {
				return db.execute(sql + " into outfile '" + filepath + "'",
						null);
			}
		});
		if (bl == null)
			return false;
		return bl;
	}

	public boolean runCmd(String cmd) {
		System.out.println(cmd);
		try {
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec("cmd /c " + cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean backupTables(List<String> tables, String filepath) {
		return backupDataBase(JdbcUtils.host, JdbcUtils.user,
				JdbcUtils.password, JdbcUtils.database, tables, filepath);
	}

	public boolean backupDataBase(String filepath) {
		return backupDataBase(JdbcUtils.host, JdbcUtils.user,
				JdbcUtils.password, JdbcUtils.database, null, filepath);
	}

	public boolean restoreDataBase(String filepath) throws FileNotFoundException {
		return restoreDataBase(JdbcUtils.host, JdbcUtils.user,
				JdbcUtils.password, JdbcUtils.database, filepath);
	}

	public boolean backupDataBase(String host, String name, String pwd,
			String database, List<String> tables, String filepath) {
		StringBuilder sb = new StringBuilder();
		sb.append("mysqldump");
		if (host != null) {
			sb.append(" -h " + host);
		}
		if (name != null) {
			sb.append(" -u" + name);
		}
		if (pwd != null) {
			sb.append(" -p" + pwd);
		}
		if (database != null) {
			sb.append(" " + database);
		}
		if (tables != null) {
			for (int i = 0; i < tables.size(); i++) {
				sb.append(" " + tables.get(i));
			}
		}
		sb.append(" > " + filepath);
		return runCmd(sb.toString());
	}

	public boolean restoreDataBase(String host, String name, String pwd,
			String database, String filepath) throws FileNotFoundException {
		File file = new File(filepath);
		if(!file.exists()){
			throw new FileNotFoundException(filepath+"路径下文件不存在,无法还原");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("mysql");
		if (host != null) {
			sb.append(" -h" + host);
		}
		if (name != null) {
			sb.append(" -u" + name);
		}
		if (pwd != null) {
			sb.append(" -p" + pwd);
		}
		if (database != null) {
			sb.append(" " + database);
		}
		sb.append(" < " + filepath);
		return runCmd(sb.toString());
	}
	
}
