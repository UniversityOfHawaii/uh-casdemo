package edu.hawaii.its.casdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.hawaii.its.casdemo.model.Holiday;
import edu.hawaii.its.casdemo.model.Type;
import edu.hawaii.its.casdemo.repository.HolidayRepository;
import edu.hawaii.its.casdemo.repository.HolidayTypeRepository;

@Service
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private HolidayTypeRepository holidayTypeRepository;

    @Transactional(readOnly = true)
    public Page<Holiday> findPaginatedHdays(final int page, final int size) {
        return holidayRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holidaysById", key = "#id")
    public Holiday findHoliday(Integer id) {
        Optional<Holiday> holiday = holidayRepository.findById(id);
        return holiday.orElse(null);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holidayTypesById", key = "#id")
    public Type findType(Integer id) {
        Optional<Type> type = holidayTypeRepository.findById(id);
        return type.orElse(null);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holidays")
    public List<Holiday> findHolidays() {
        return holidayRepository.findAllByOrderByObservedDateDesc();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holidayTypes")
    public List<Type> findTypes() {
        return holidayTypeRepository.findAll();
    }

}
