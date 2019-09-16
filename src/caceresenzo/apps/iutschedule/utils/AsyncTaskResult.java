package caceresenzo.apps.iutschedule.utils;

import android.os.AsyncTask;

public class AsyncTaskResult<T> {
	
	/* Variables */
	private final T result;
	private final Exception exception;
	
	/* Constructor */
	public AsyncTaskResult(T result) {
		this(result, null);
	}
	
	/* Constructor */
	public AsyncTaskResult(Exception exception) {
		this(null, exception);
	}
	
	/* Private Constructor */
	private AsyncTaskResult(T result, Exception exception) {
		this.result = result;
		this.exception = exception;
	}
	
	/** @return {@link AsyncTask Task}'s result. */
	public T getResult() {
		return result;
	}
	
	/** @return {@link AsyncTask Task}'s occured {@link Exception exception}. */
	public Exception getException() {
		return exception;
	}
	
}