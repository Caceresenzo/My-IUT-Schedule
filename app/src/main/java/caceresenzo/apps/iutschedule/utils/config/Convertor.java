package caceresenzo.apps.iutschedule.utils.config;

public interface Convertor<T, R> {

	/**
	 * Applies this convertor to the given argument.
	 *
	 * @param t the convertor argument.
	 * @return the convertor result.
	 */
	R apply(T t);

}