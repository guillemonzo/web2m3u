package com.guimonel.web2m3u.controller;


import com.guimonel.web2m3u.service.AcelistingScrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = MediaType.TEXT_PLAIN_VALUE)
public class M3uController {


    @Autowired
    private AcelistingScrapperService acelistingScrapperService;

    @RequestMapping( method = RequestMethod.GET, value = "/m3u")
    public String getM3uPlaylist(){

        return acelistingScrapperService.scrapSite();

    }

}
