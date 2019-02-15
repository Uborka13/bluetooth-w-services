package mobilsoft.icell.hu.seniti2.controller.dao;

import com.google.gson.annotations.SerializedName;

import mobilsoft.icell.hu.seniti2.persistence.entity.Event;

public class EventsItem {

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("location")
    private String location;

    @SerializedName("timestamp")
    private long timestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public EventsItem setEventEntity(Event eventEntity) {
        this.name = eventEntity.getName();
        this.description = eventEntity.getDescription();
        this.location = eventEntity.getEventLocation();
        this.timestamp = eventEntity.getTimestamp();
        return this;
    }
}