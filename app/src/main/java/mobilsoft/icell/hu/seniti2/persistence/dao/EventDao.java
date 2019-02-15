package mobilsoft.icell.hu.seniti2.persistence.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import mobilsoft.icell.hu.seniti2.persistence.entity.Event;

@Dao
public interface EventDao {

    @Query("select * from event")
    LiveData<List<Event>> findAllEvent();

    @Insert
    void insert(Event... events);

    @Query("delete from event")
    void deleteAll();

    @Query("select * from event")
    List<Event> findAllEventSync();
}