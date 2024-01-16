package org.ziegelbauer.travelbuddy.database;

import android.app.Application;

import org.ziegelbauer.travelbuddy.dao.ExcursionDAO;
import org.ziegelbauer.travelbuddy.dao.VacationDAO;
import org.ziegelbauer.travelbuddy.entities.Excursion;
import org.ziegelbauer.travelbuddy.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private final VacationDAO vacationDAO;
    private final ExcursionDAO excursionDAO;

    private List<Vacation> allVacations;
    private List<Excursion> allExcursions;

    private static final int THREAD_COUNT = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(THREAD_COUNT);

    public Repository(Application app) {
        TravelBuddyDatabaseBuilder db = TravelBuddyDatabaseBuilder.getDatabase(app);
        vacationDAO = db.vacationDAO();
        excursionDAO = db.excursionDAO();
    }

    public List<Vacation> getAllVacations() {
        databaseExecutor.execute(() -> {
            allVacations = vacationDAO.getAllVacations();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return allVacations;
    }
    
    public void addVacation(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.insert(vacation);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateVacation(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.update(vacation);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteVacation(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.delete(vacation);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Excursion> getAllExcursions() {
        databaseExecutor.execute(() -> {
            allExcursions = excursionDAO.getAllExcursions();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return allExcursions;
    }

    public List<Excursion> getAssociatedExcursions(int vacationID) {
        databaseExecutor.execute(() -> {
            allExcursions = excursionDAO.getAssociatedExcursions(vacationID);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return allExcursions;
    }

    public void addExcursion(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.insert(excursion);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateExcursion(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.update(excursion);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteExcursion(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.delete(excursion);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
