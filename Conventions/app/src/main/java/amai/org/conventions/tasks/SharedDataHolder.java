package amai.org.conventions.tasks;

public interface SharedDataHolder<SharedData> {
	void setSharedData(SharedData d);

	SharedData getSharedData();
}
