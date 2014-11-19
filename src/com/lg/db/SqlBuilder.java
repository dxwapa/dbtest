package com.lg.db;

import java.lang.reflect.Field;


public class SqlBuilder {

	public static BasicSqlResult bdSelectSql(Class model,String where,Object[] objs){
		return bdSelectSql(model, where, objs, null, null);
	}
	
	public static BasicSqlResult bdSelectSql(Class model,String where,Object[] objs,Integer pageno,Integer pagemax){
		String tablename = getTableName(model);
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		Field[] fs = model.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			if (i != fs.length - 1) {
				sql.append(fs[i].getName());
				sql.append(",");
			} else {
				sql.append(fs[i].getName());
				sql.append(" from ");
				sql.append(tablename);
			}
		}
		if(where!=null){
			sql.append(" where ");
			sql.append(where);
		}
		if(pageno!=null&&pagemax!=null){
			sql.append(" limit " + (pageno - 1)* pagemax + "," + pagemax);
		}
		BasicSqlResult sr = new BasicSqlResult(sql.toString(),objs,model);
		return sr;
	}
	
	public static SqlResult bdFindAllSql(Class model){
		return bdSelectSql(model, null, null);
	}
	
	
	public static SqlResult bdDeleteSql(Object obj){
		Class model = obj.getClass();
		Field f = null;
		Object o = null;
		try {
			f = model.getDeclaredField(getPrimaryKeyCloumn(model));
			f.setAccessible(true);
			o = f.get(obj);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		f.setAccessible(true);
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(getTableName(model));
		sql.append(" where ");
		sql.append(f.getName());
		sql.append(" = ? ");
		Object objs[] = {o};
		BasicSqlResult sr = new BasicSqlResult(sql.toString(), objs);
		return sr;
	}

	public static SqlResult bdUpdateSql(Object obj){
		Class model = obj.getClass();
		Field[] fs = model.getDeclaredFields();
		StringBuilder sql = new StringBuilder();
		sql.append("update ");
		sql.append(getTableName(model));
		sql.append(" set ");
		Object objs[] = new Object[fs.length + 1];
		for (int i = 0; i < fs.length; i++) {
			fs[i].setAccessible(true);
			try {
				objs[i] = fs[i].get(obj);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} finally {
			}
			if (i != fs.length - 1) {
				sql.append(fs[i].getName());
				sql.append(" = ?,");
			} else {
				sql.append(fs[i].getName());
				sql.append(" = ? ");
			}
		}
		Field f = null;
			try {
				f = model.getDeclaredField(getPrimaryKeyCloumn(model));
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			f.setAccessible(true);
			sql.append(" where ");
			sql.append(f.getName());
			sql.append(" = ? ");
			try {
				objs[fs.length] = f.get(obj);
			} catch (IllegalArgumentException e1) {
			} catch (IllegalAccessException e1) {
			} finally {
			}
		BasicSqlResult sr = new BasicSqlResult(sql.toString(), objs);
		return sr;
	}
	
	public static SqlResult bdInsertSql(Object obj){
		Class model = obj.getClass();
		Field[] fs = model.getDeclaredFields();
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ");
		sql.append(getTableName(model));
		sql.append("(");
		StringBuilder sql2 = new StringBuilder();
		Object objs[] = new Object[fs.length];
		for (int i = 0; i < fs.length; i++) {
			fs[i].setAccessible(true);
			try {
				objs[i] = fs[i].get(obj);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} finally {
			}
			if (i != fs.length - 1) {
				sql.append(fs[i].getName());
				sql.append(",");
				sql2.append("?,");
			} else {
				sql.append(fs[i].getName());
				sql.append(") ");
				sql.append(" values(");
				sql2.append("?)");
			}
		}
		sql.append(sql2.toString());
		if(isAutoIncrement(model)){
			objs[0] = null;
		}
		BasicSqlResult sr = new BasicSqlResult(sql.toString(), objs);
		return sr;
	}
	
	public static boolean isAutoIncrement(Class clazz){
		TableName anno = (TableName) clazz.getAnnotation(TableName.class);
		return anno != null;
	}
	
	public static String getTableName(Class clazz) {
		TableName anno = (TableName) clazz.getAnnotation(TableName.class);
		if (anno == null) {
			return null;
		} else {
			return anno.value();
		}
	}
	
	public static String getPrimaryKeyCloumn(Class clazz) {
		Field fs[] = clazz.getDeclaredFields();
		return fs[0].getName();
		
	}
}
