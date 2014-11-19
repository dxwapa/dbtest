package com.lg.db;

public class BasicSqlResult implements SqlResult{

	String sql;
	Object []selObjs;
	Class returnClass;
	
	

	public BasicSqlResult(String sql, Object[] selObjs) {
		super();
		this.sql = sql;
		this.selObjs = selObjs;
	}

	public BasicSqlResult(String sql, Object[] selObjs, Class returnClass) {
		super();
		this.sql = sql;
		this.selObjs = selObjs;
		this.returnClass = returnClass;
	}

	public BasicSqlResult(String sql, Class returnClass) {
		super();
		this.sql = sql;
		this.returnClass = returnClass;
	}

	@Override
	public String getSql() {
		return sql;
	}

	@Override
	public Object[] getSelObjs() {
		return selObjs;
	}

	@Override
	public Class getReturnClass() {
		return returnClass;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setSelObjs(Object[] selObjs) {
		this.selObjs = selObjs;
	}

	public void setReturnClass(Class returnClass) {
		this.returnClass = returnClass;
	}
	
	
}
