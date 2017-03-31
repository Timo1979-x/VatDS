package by.gto.model;

public class CustomerInfo {
    private int unp;
    private String name;
    private String address;

    public CustomerInfo() {
    }

    public CustomerInfo(int unp, String name, String address) {
        this.name = name;
        this.address = address;
        this.unp = unp;
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

    public int getUnp() {
        return unp;
    }

    public void setUnp(int unp) {
        this.unp = unp;
    }

    @Override
    public String toString() {
        return "CustomerInfo{" +
                "unp=" + unp +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
