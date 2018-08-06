package com.miklesw.conway.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GridController {

    @RequestMapping(path="/spawn", method = RequestMethod.POST)
    public void spawnCell(@RequestBody Object position ) {

    }
}
