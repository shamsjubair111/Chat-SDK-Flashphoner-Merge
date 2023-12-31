package sdk.chat.ui;



public class DemoContact {

    String name;
    String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public DemoContact(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
