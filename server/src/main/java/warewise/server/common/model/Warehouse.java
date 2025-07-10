package warewise.server.common.model;

public class Warehouse {
    private int warehouse_id;
    private String name;
    private String address;

    public Warehouse() {
    }

    public Warehouse(int warehouse_id, String name, String address) {
        this.warehouse_id = warehouse_id;
        this.name = name;
        this.address = address;
    }

    public int getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(int warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
