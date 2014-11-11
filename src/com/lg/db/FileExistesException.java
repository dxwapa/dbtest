package com.lg.db;

public class FileExistesException extends Exception{

	public FileExistesException() {
		super();
	}

	public FileExistesException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileExistesException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileExistesException(String message) {
		super(message);
	}

	public FileExistesException(Throwable cause) {
		super(cause);
	}

	
}
