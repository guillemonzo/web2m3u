package com.guimonel.web2m3u.controller;


import com.guimonel.web2m3u.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@RestController
@RequestMapping(value = "/api", produces = MediaType.IMAGE_PNG_VALUE)
public class ImageController {

    @Autowired
    private ImageService imageService;

    @RequestMapping( method = RequestMethod.GET, value = "/images/{home}/{visitor}/match.png", params = {"time", "channel"})
    public @ResponseBody byte[] getImage(@PathVariable String home, @PathVariable String visitor, @RequestParam(required = false) String time,  @RequestParam(required = false) String channel) throws IOException {


        BufferedImage image = imageService.buildMatchImage(home, visitor, time, channel);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);

        return os.toByteArray();

    }

}
