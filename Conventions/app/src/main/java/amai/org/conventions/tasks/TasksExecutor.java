package amai.org.conventions.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TasksExecutor<SharedData> implements SharedDataHolder<SharedData> {
	private SharedData sharedData;
	private final List<Task<SharedData>> tasks;

	@SafeVarargs
	public TasksExecutor(SharedData initialSharedData, Task<SharedData>... tasks) {
		this.sharedData = initialSharedData;
		this.tasks = new ArrayList<>(Arrays.asList(tasks));
	}

	public void addTask(Task<SharedData> task) {
		tasks.add(task);
	}

	public void execute() {
		setupTasks();
		if (!tasks.isEmpty()) {
			tasks.get(0).execute();
		}
	}

	private void setupTasks() {
		if (tasks.isEmpty()) {
			return;
		}
		for (int i = 0; i < tasks.size() - 1; i++) {
			Task<SharedData> currTask = tasks.get(i);
			currTask.setNextTask(tasks.get(i + 1));
			currTask.setSharedDataHolder(this);
		}
		tasks.get(tasks.size() - 1).setSharedDataHolder(this); // The loop doesn't go over the last one
	}

	@Override
	public SharedData getSharedData() {
		return sharedData;
	}

	@Override
	public void setSharedData(SharedData sharedData) {
		this.sharedData = sharedData;
	}

	public void executeNextTaskAfter(String name) {
		setupTasks();
		Task<SharedData> task = getTaskByName(name);
		if (task != null) {
			task.executeNextTask();
		}
	}

	private Task<SharedData> getTaskByName(String name) {
		for (Task<SharedData> task : tasks) {
			if (task.getName().equals(name)) {
				return task;
			}
		}

		return null;
	}
}
