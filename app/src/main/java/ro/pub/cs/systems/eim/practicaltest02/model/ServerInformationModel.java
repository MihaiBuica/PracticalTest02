package ro.pub.cs.systems.eim.practicaltest02.model;

public class ServerInformationModel {
    String source;
    String clientHour;
    String clientMin;
    boolean activated = true;

    public ServerInformationModel(String source, String clientHour, String clientMin) {
        this.source = source;
        this.clientHour = clientHour;
        this.clientMin = clientMin;
        activated = false;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClientHour() {
        return clientHour;
    }

    public void setClientHour(String clientHour) {
        this.clientHour = clientHour;
    }

    public String getClientMin() {
        return clientMin;
    }

    public void setClientMin(String clientMin) {
        this.clientMin = clientMin;
    }

    public boolean isActivated() {
        return activated;
    }
    public void setActivated() {
        activated = true;
    }

    @Override
    public String toString() {
        if (activated)
        {
            return "Activated: " + clientHour + clientMin;
        }
        else
            return "Inactivated";
    }

}
