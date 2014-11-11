package com.lg.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MysqlUtil {

	Connection conn;
	ResultSet rs;
	PreparedStatement ps;

	public <T> List<T> doListQuery(String sql, Object[] objs,
			Class<T> returnclass) {
		beforeExecute();
		try {
			System.out.println(sql);
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

	public <T> T doObjQuery(String sql, Object[] objs, Class<T> returnclass) {
		beforeExecute();
		try {
			System.out.println(sql);
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
		public <T>T doExecute() throws SQLException, RunAtErrorFunctionException;
	}

	/*public boolean execute(SqlResult sr) throws SQLException, RunAtErrorFunctionException {
		return execute(sr.getSql(), sr.getSelObjs());
	}*/

	public boolean execute(String sql, Object[] objs) throws SQLException, RunAtErrorFunctionException {
		if(!isExecuting)
			throw new RunAtErrorFunctionException("该方法只能在doExecute内部执行");
		System.out.println(sql);
		ps = conn.prepareStatement(sql);
		if (objs != null)
			for (int i = 0; i < objs.length; i++) {
				ps.setObject(i + 1, objs[i]);
			}
		return ps.execute();
	}

	boolean isExecuting = false;

	
	public <T>T doExecute(TransExecutes exes) {
		if (exes != null) {
			beforeExecute();
			isExecuting = true;
			try {
				T t = exes.doExecute();
				conn.commit();
				return t;
			} catch (SQLException | RunAtErrorFunctionException e) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
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
		Constructor<T> cs = null;
		try {
			cs = model.getConstructor();
		} catch (SecurityException e2) {
			e2.printStackTrace();
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
		}
		if (model == String.class) {
			try {
				while (rs.next()) {
					String str = rs.getString(1);
					list.add((T) str);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}
			}

		} else if (model == Integer.class || model == int.class) {
			try {
				while (rs.next()) {
					Integer str = rs.getInt(1);
					list.add((T) str);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}
			}

		} else if (model == Double.class || model == double.class) {
			try {
				while (rs.next()) {
					Double str = rs.getDouble(1);
					list.add((T) str);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}
			}

		} else if (model == Float.class || model == float.class) {
			try {
				while (rs.next()) {
					Float str = rs.getFloat(1);
					list.add((T) str);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}
			}

		} else if (model == Long.class || model == long.class) {
			try {
				while (rs.next()) {
					Long str = rs.getLong(1);
					list.add((T) str);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}
			}

		} else {
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
						Object o = null;
						try {
							if (fs[i].getGenericType() == int.class
									|| fs[i].getGenericType() == Integer.class) {
								o = rs.getInt(fs[i].getName());
							} else if (fs[i].getGenericType() == String.class) {
								o = rs.getString(fs[i].getName());
							} else if (fs[i].getGenericType() == Double.class
									|| fs[i].getGenericType() == double.class) {
								o = rs.getDouble(fs[i].getName());
							} else if (fs[i].getGenericType() == Float.class
									|| fs[i].getGenericType() == float.class) {
								o = rs.getFloat(fs[i].getName());
							} else if (fs[i].getGenericType() == Long.class
									|| fs[i].getGenericType() == long.class) {
								o = rs.getLong(fs[i].getName());
							} else if (fs[i].getGenericType() == Date.class) {
								o = rs.getDate(fs[i].getName());
							}
						} catch (SQLException e1) {
						}
						try {
							fs[i].setAccessible(true);
							fs[i].set(m, o);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					if (m != null)
						list.add(m);
				}
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return list;
	}

	public <T> T cursorToObj(Class model) {
		T m = null;
		if (model == String.class || model == Integer.class
				|| model == Long.class || model == Float.class
				|| model == Double.class || model == Short.class) {
			try {
				if (rs.next()) {
					m = (T) rs.getObject(1);
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
						Object o = null;
						if (fs[i].getGenericType() == int.class
								|| fs[i].getGenericType() == Integer.class) {
							try {
								o = rs.getInt(fs[i].getName());// 根据字段名
																// 从resultset得到数据,如果得不到则不作处理
							} catch (SQLException e) {
							}
						} else if (fs[i].getGenericType() == String.class) {
							try {
								o = rs.getString(fs[i].getName());
							} catch (SQLException e) {
							}
						} else if (fs[i].getGenericType() == Double.class
								|| fs[i].getGenericType() == double.class) {
							try {
								o = rs.getDouble(fs[i].getName());
							} catch (SQLException e) {
							}
						} else if (fs[i].getGenericType() == Float.class
								|| fs[i].getGenericType() == float.class) {
							try {
								o = rs.getFloat(fs[i].getName());
							} catch (SQLException e) {
							}
						} else if (fs[i].getGenericType() == Long.class
								|| fs[i].getGenericType() == long.class) {
							try {
								o = rs.getLong(fs[i].getName());
							} catch (SQLException e) {
							}
						} else if (fs[i].getGenericType() == Date.class) {
							try {
								o = rs.getDate(fs[i].getName());
							} catch (SQLException e) {
							}
						}
						fs[i].setAccessible(true);
						if (o != null) {
							try {
								fs[i].set(m, o);// 反射异常 不作处理
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
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
