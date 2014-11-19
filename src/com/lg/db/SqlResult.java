package com.lg.db;

public interface SqlResult {
	
	public String getSql();
	public Object[] getSelObjs();
	public Class getReturnClass();
	
}
