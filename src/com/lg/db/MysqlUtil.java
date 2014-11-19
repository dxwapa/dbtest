package com.lg.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MysqlUtil {

	Connection conn;
	ResultSet rs;
	PreparedStatement ps;

	
	
	
	public boolean delete(Object obj)throws SQLException,RunAtErrorFunctionException{
		SqlResult sr = SqlBuilder.bdDeleteSql(obj);
		return execute(sr);
	}
	
	public boolean update(Object obj)throws SQLException,RunAtErrorFunctionException{
		SqlResult sr = SqlBuilder.bdUpdateSql(obj);
		return execute(sr);
	}
	
	public boolean save(Object obj) throws SQLException,RunAtErrorFunctionException{
		if (!isExecuting)
			throw new RunAtErrorFunctionException("该方法只能在doExecute内部执行");
		SqlResult sr = SqlBuilder.bdInsertSql(obj);
		String sql = sr.getSql();
		Object[] objs = sr.getSelObjs();
		//System.out.println(sql);
		ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		if (objs != null)
			for (int i = 0; i < objs.length; i++) {
				ps.setObject(i + 1, objs[i]);
			}
		int updatenum = ps.executeUpdate();
		if (updatenum > 0) {
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				String fieldname = SqlBuilder.getPrimaryKeyCloumn(obj.getClass());
				try {
					Field fd = obj.getClass().getDeclaredField(fieldname);
					Object pk = rs.getObject(1);
					fd.setAccessible(true);
					fd.set(obj, pk);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			closeResultSet();
			closePreparedStatement();
			return true;
		} else {
			closePreparedStatement();
			return false;
		}
	}

	public <T>List<T>getPageList(Class<T>returnclass,String where,Object[]objs,int pageno,int pagemax){
		SqlResult sr = SqlBuilder.bdSelectSql(returnclass, where, objs, pageno, pagemax);
		return doListQuery(sr);
	}
	
	public <T>List<T> findAll(Class<T>clazz){
		SqlResult sr = SqlBuilder.bdFindAllSql(clazz);
		return doListQuery(sr.getSql(), null, clazz);
	}
	
	public <T>List<T>doListQuery(SqlResult sr){
		return doListQuery(sr.getSql(), sr.getSelObjs(), sr.getReturnClass());
	}
	
	public <T> List<T> doListQuery(String sql, Object[] objs,
			Class<T> returnclass) {
		beforeExecute();
		try {
			ps = conn.prepareStatement(sql);
			if (objs != null)
				for (int i = 0; i < objs.length; i++) {
					ps.setObject(i + 1, objs[i]);
				}
			rs = ps.executeQuery();
			List<T> list = cursorToList(returnclass);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			afterExecute();
		}
		return null;
	}

	
	public <T> T doObjQuery(SqlResult sr){
		return (T) doObjQuery(sr.getSql(), sr.getSelObjs(), sr.getReturnClass());
	}
	
	public <T> T doObjQuery(String sql, Object[] objs, Class<T> returnclass) {
		beforeExecute();
		try {
			ps = conn.prepareStatement(sql);
			if (objs != null)
				for (int i = 0; i < objs.length; i++) {
					ps.setObject(i + 1, objs[i]);
				}
			rs = ps.executeQuery();
			T t = cursorToObj(returnclass);
			return t;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			afterExecute();
		}
		return null;
	}

	public interface TransExecutes {
		public <T> T doExecute() throws SQLException,
				RunAtErrorFunctionException;
	}

	public boolean execute(SqlResult sr) throws SQLException,
			RunAtErrorFunctionException {
		return execute(sr.getSql(), sr.getSelObjs());
	}

	public boolean execute(String sql, Object[] objs) throws SQLException,
			RunAtErrorFunctionException {
		if (!isExecuting)
			throw new RunAtErrorFunctionException("该方法只能在doExecute内部执行");
		ps = conn.prepareStatement(sql);
		if (objs != null)
			for (int i = 0; i < objs.length; i++) {
				ps.setObject(i + 1, objs[i]);
			}
		boolean bl = ps.execute();
		closePreparedStatement();
		return bl;
	}

	boolean isExecuting = false;

	public <T> T doExecute(TransExecutes exes) {
		if (exes != null) {
			beforeExecute();
			isExecuting = true;
			try {
				T t = exes.doExecute();
				conn.commit();
				return t;
			} catch (Exception e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} finally {
				isExecuting = false;
				afterExecute();
			}
		}
		return null;
	}

	public void beforeExecute() {
		if (!isExecuting) {
			if (conn == null) {
				try {
					conn = JdbcUtils.getConnection();
					conn.setAutoCommit(false);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void afterExecute() {
		if (!isExecuting) {
			closeResultSet();
			closePreparedStatement();
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn = null;
		}
	}

	public void closeResultSet() {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				rs = null;
			}
		}
	}

	public void closePreparedStatement() {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				ps = null;
			}
		}
	}


	public <T> List<T> cursorToList(Class model) {
		if (rs == null)
			return null;
		List<T> list = new ArrayList<T>();
		if (model == String.class || model == Integer.class
				|| model == Long.class || model == Float.class
				|| model == Double.class || model == Short.class) {
			try {
				while (rs.next()) {
					list.add( (T) getObjFromResultSet(model,rs));
					//list.add((T) rs.getObject(1));
					//m = (T) rs.getObject(1);
				}
			} catch (SQLException e) {//rs被关闭或者getObject没有了 不作处理
				
			}
		} else {
			Constructor<T> cs = null;
			try {
				cs = model.getConstructor();
			} catch (SecurityException e2) {
				e2.printStackTrace();
			} catch (NoSuchMethodException e2) {
				e2.printStackTrace();
			}
			Field fs[] = model.getDeclaredFields();
			try {
				while (rs.next()) {
					T m = null;
					try {
						m = cs.newInstance();
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
					for (int i = 0; i < fs.length; i++) {
						setFieldFromResultSet(fs[i], rs, m);
					}
					if (m != null)
						list.add(m);
				}
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} 
		}
		return list;
	}

	/*
	 * 1.fd是Integer rs得到的是long 将rs得到的转成integer，如果不能则??
	 * 2.
	 */
	public void setFieldFromResultSet(Field fd,ResultSet rs,Object m){
		Object o = null;
		Type type = fd.getGenericType();
		try {
			if(type == Integer.class||type == int.class){
					o = rs.getInt(fd.getName());
			} else if (type == String.class) {
				o = rs.getString(fd.getName());
			} else if (type == Double.class
					|| type == double.class) {
				o = rs.getDouble(fd.getName());
			} else if (type == Float.class
					|| type == float.class) {
				o = rs.getFloat(fd.getName());
			} else if (type == Long.class
					|| type == long.class) {
				o = rs.getLong(fd.getName());
			} else if (type == Date.class) {
				o = rs.getDate(fd.getName());
			}
			
		} catch (SQLException e) {
			
		}
		fd.setAccessible(true);
		try {
			fd.set(m, o);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public  <T> T getObjFromResultSet(Class<T> model,ResultSet rs){
		T o = null;
		try {
			if(model == Integer.class||model == int.class){
					Integer r = rs.getInt(1);
					o = (T) r;
			} else if (model == String.class) {
				String r = rs.getString(1);
				o = (T) r;
			} else if (model == Double.class
					|| model == double.class) {
				Double r =  rs.getDouble(1);
				o = (T) r;
			} else if (model == Float.class
					|| model == float.class) {
				Float r = rs.getFloat(1);
				o = (T) r;
			} else if (model == Long.class
					|| model == long.class) {
				Long r =  rs.getLong(1);
				o = (T) r;
			} else if (model == Date.class) {
				Date r =  rs.getDate(1);
				o = (T) r;
			}
			
		} catch (SQLException e) {
			
		}
		return o;
	}
	public <T> T cursorToObj(Class<T> model) {
		T m = null;
		if (model == String.class || model == Integer.class
				|| model == Long.class || model == Float.class
				|| model == Double.class || model == Short.class) {
			try {
				if (rs.next()) {
					m = getObjFromResultSet(model,rs);
				}
			} catch (SQLException e) {// rs被关闭或者getObject没有了 不作处理
				
			}
		} else {
			try {
				if (rs.next()) {
					Field[] fs = model.getDeclaredFields();
					Constructor cs = null;
					try {// 反射异常 直接打印
						cs = model.getConstructor();
						m = (T) cs.newInstance();
					} catch (NoSuchMethodException e1) {
						e1.printStackTrace();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
					for (int i = 0; i < fs.length; i++) {
						setFieldFromResultSet(fs[i], rs, m);
					}
					return m;
				} else {
					return null;
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return m;
	}
}
