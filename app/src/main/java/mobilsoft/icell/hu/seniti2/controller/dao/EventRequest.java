package mobilsoft.icell.hu.seniti2.controller.dao;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventRequest {

    @SerializedName("devicename")
    private String devicename;

    @SerializedName("osversion")
    private String osversion;

    @SerializedName("events")
    private List<EventsItem> events;

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getOsversion() {
        return osversion;
    }

    public void setOsversion(String osversion) {
        this.osversion = osversion;
    }

    public List<EventsItem> getEvents() {
        return events;
    }

    public void setEvents(List<EventsItem> events) {
        this.events = events;
    }
}