package caceresenzo.apps.iutschedule.managers;

import java.util.ArrayList;
import java.util.List;

import caceresenzo.apps.iutschedule.managers.implementations.EventColorManager;
import caceresenzo.apps.iutschedule.managers.implementations.StudentManager;
import caceresenzo.apps.iutschedule.managers.implementations.VirtualCalendarManager;

public class ScheduleManager extends AbstractManager {
	
	/* Singleton */
	private static ScheduleManager INSTANCE;
	
	/* Managers */
	private List<AbstractManager> managers;
	
	/* Private Constructor */
	private ScheduleManager() {
		this.managers = new ArrayList<>();
		
		loadManagers();
	}
	
	/** Add all managers in one list. */
	private void loadManagers() {
		managers.add(StudentManager.get());
		managers.add(VirtualCalendarManager.get());
		managers.add(EventColorManager.get());
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		for (AbstractManager manager : managers) {
			manager.initialize();
		}
	}
	
	@Override
	public void step() {
		super.step();
		
		for (AbstractManager manager : managers) {
			manager.step();
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		step();
		
		for (AbstractManager manager : managers) {
			manager.destroy();
		}
	}
	
	/** @return ScheduleManager's singleton instance. */
	public static final ScheduleManager get() {
		if (INSTANCE == null) {
			INSTANCE = new ScheduleManager();
		}
		
		return INSTANCE;
	}
	
}