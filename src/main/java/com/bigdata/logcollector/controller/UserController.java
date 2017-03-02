package com.bigdata.logcollector.controller;

import com.bigdata.logcollector.annotation.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author sanumandla
 */
@Slf4j
@RestController
@RequestMapping("/1.0/user")
public class UserController {

    @Stream
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void post(@RequestParam("type") String type) {
        log.info("Recevied a POST request");
    }

    @Stream
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void get(@RequestParam("type") String type) {
        log.info("Recevied a GET request");
    }

    @Stream
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void put(@RequestParam("type") String type) {
        log.info("Recevied a PUT request");
    }

    @Stream
    @DeleteMapping
    public void delete(@RequestParam("type") String type) {
        log.info("Recevied a DELETE request");
    }

}
