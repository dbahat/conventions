package amai.org.conventions.model;

public class Hall {
    private String name;
    private int order;

    public Hall() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hall withName(String name) {
        setName(name);
        return this;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Hall withOrder(int order) {
        setOrder(order);
        return this;
    }
}
