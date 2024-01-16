package org.ziegelbauer.travelbuddy.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.ziegelbauer.travelbuddy.dao.ExcursionDAO;
import org.ziegelbauer.travelbuddy.dao.VacationDAO;
import org.ziegelbauer.travelbuddy.entities.Excursion;
import org.ziegelbauer.travelbuddy.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 2, exportSchema = false)
public abstract class TravelBuddyDatabaseBuilder extends RoomDatabase {
    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    private static volatile TravelBuddyDatabaseBuilder INSTANCE;

    static TravelBuddyDatabaseBuilder getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (TravelBuddyDatabaseBuilder.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TravelBuddyDatabaseBuilder.class,
                            "TravelBuddyDB")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
