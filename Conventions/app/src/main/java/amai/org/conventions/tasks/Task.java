package amai.org.conventions.tasks;

import android.app.Activity;
import android.content.Context;

import amai.org.conventions.ConventionsApplication;

public abstract class Task<SharedData> {
	private Task<SharedData> nextTask;
	private SharedDataHolder<SharedData> holder;

	// Called from the task executor
	public void setNextTask(Task<SharedData> nextTask) {
		this.nextTask = nextTask;
	}

	// For debug
	public Task<SharedData> getNextTask() {
		return nextTask;
	}

	// Used in execute
	protected void executeNextTask() {
		if (nextTask == null) {
			return;
		}
		nextTask.execute();
	}

	public final void execute() {
		Context currentContext = ConventionsApplication.getCurrentContext();
		if (!(currentContext instanceof Activity)) {
			return;
		}
		execute((Activity) currentContext);
	}

	public abstract void execute(Activity currentActivity);

	public void setSharedDataHolder(SharedDataHolder<SharedData> holder) {
		this.holder = holder;
	}

	protected void setSharedData(SharedData d) {
		if (holder != null) {
			holder.setSharedData(d);
		}
	}

	protected SharedData getSharedData() {
		if (holder != null) {
			return holder.getSharedData();
		}
		return null;
	}

	public abstract String getName();
}
