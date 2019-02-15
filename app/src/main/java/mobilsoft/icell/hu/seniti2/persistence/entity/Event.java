package mobilsoft.icell.hu.seniti2.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

@Entity(tableName = "event")
public class Event {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "timestamp")
    private Long timestamp;
    @ColumnInfo(name = "eventLocation")
    private String eventLocation;
    @ColumnInfo(name = "description")
    private String description;

    public Event() {
    }

    public Event(String name, Long timestamp, Class eventLocation) {
        this.name = name;
        this.timestamp = timestamp / 1000;
        this.eventLocation = eventLocation.getSimpleName();
    }

    public Event(String name, Long timestamp, Class eventLocation, String description) {
        this.name = name;
        this.timestamp = timestamp / 1000;
        this.eventLocation = eventLocation.getSimpleName();
        this.description = description;
    }


    public Event(String name, Long timestamp, Class eventLocation, Context context) {
        this.name = name;
        this.timestamp = timestamp / 1000;
        this.eventLocation = eventLocation.getSimpleName();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        this.description = batteryPct*100 + "%";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
