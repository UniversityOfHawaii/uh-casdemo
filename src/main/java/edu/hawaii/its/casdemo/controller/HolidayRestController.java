package edu.hawaii.its.casdemo.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.casdemo.service.HolidayService;
import edu.hawaii.its.casdemo.model.Holiday;
import edu.hawaii.its.casdemo.model.Type;

@RestController
public class HolidayRestController {

    private static final Log logger = LogFactory.getLog(HolidayRestController.class);

    @Autowired
    private HolidayService holidayService;

    @GetMapping("/api/holidays")
    public ResponseEntity<JsonData<List<Holiday>>> holidays() {
        logger.debug("Entered REST holidays...");
        JsonData<List<Holiday>> data = new JsonData<>(holidayService.findHolidays());
        return ResponseEntity
                .ok()
                .body(data);
    }

    @GetMapping("/api/holidays/{id}")
    public ResponseEntity<JsonData<Holiday>> holiday(@PathVariable Integer id) {
        logger.info("Entered REST holiday(" + id + ") ...");
        JsonData<Holiday> data = new JsonData<>(holidayService.findHoliday(id));
        return ResponseEntity
                .ok()
                .body(data);
    }

    @GetMapping(value = "/api/holidaygrid/get",
            params = { "page", "size" })
    public Page<Holiday> findPaginated(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        logger.debug("Entered REST holidays grid...");
        return holidayService.findPaginatedHdays(page, size);
    }

    @GetMapping("/api/types")
    public ResponseEntity<JsonData<List<Type>>> types() {
        logger.debug("Entered REST types...");
        List<Type> types = holidayService.findTypes();
        JsonData<List<Type>> data = new JsonData<>(types);
        return ResponseEntity
                .ok()
                .body(data);
    }

    public HolidayService getHolidayService() {
        return holidayService;
    }

    public void setHolidayService(HolidayService holidayService) {
        this.holidayService = holidayService;
    }
}
