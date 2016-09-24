package amai.org.conventions.updates;

import amai.org.conventions.model.Update;

public class UpdateViewModel {
    private Update update;
    private boolean collapsed;
	private boolean focused;

    public UpdateViewModel(Update update, boolean collapsed) {
        this.update = update;
        this.collapsed = collapsed;
	    this.focused = false;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public Update getUpdate() {
        return update;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public boolean isFocused() {
		return focused;
	}
}
