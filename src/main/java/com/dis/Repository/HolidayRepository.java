package com.dis.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dis.Entity.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

}
