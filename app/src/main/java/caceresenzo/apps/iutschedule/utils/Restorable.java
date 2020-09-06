package caceresenzo.apps.iutschedule.utils;

import android.os.Bundle;

public interface Restorable {

	/**
	 * Save the instance state of this {@link Restorable restorable} to a {@link Bundle bundle}.
	 *
	 * @param outState Output {@link Bundle bundle}.
	 */
	public void saveInstanceState(Bundle outState);

	/**
	 * Restore the instance state of this {@link Restorable restorable} with a {@link Bundle bundle}.
	 *
	 * @param savedInstanceState Previously saved {@link Bundle bundle}.
	 */
	public void restoreInstanceState(Bundle savedInstanceState);

}