package mobilsoft.icell.hu.seniti2.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import mobilsoft.icell.hu.seniti2.persistence.dao.EventDao;
import mobilsoft.icell.hu.seniti2.persistence.entity.Event;

@Database(entities = {Event.class}, version = 1)
public abstract class EventRoomDatabase extends RoomDatabase {
    private static EventRoomDatabase instance;

    public static synchronized EventRoomDatabase getDatabase(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    EventRoomDatabase.class, "event_database")
                    .build();
        }
        return instance;
    }

    public abstract EventDao eventDao();
}