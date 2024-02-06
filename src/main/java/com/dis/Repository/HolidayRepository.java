package com.dis.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dis.Entity.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    @Query(value = "select h from Holiday h")
    List<Holiday> findAllHolidays();

}
