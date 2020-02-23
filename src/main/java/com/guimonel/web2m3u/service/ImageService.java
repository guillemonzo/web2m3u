package com.guimonel.web2m3u.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@Service
public class ImageService {

    @Value("${logos.host.url}")
    private String logosHostUrl;

    public BufferedImage buildMatchImage(String home, String visitor, String time, String channel) throws IOException {

        BufferedImage result = new BufferedImage(1,1,1);

        String homeId = getTeamFromXML(home);
        String visitorId = getTeamFromXML(visitor);

        if(homeId != null && visitorId != null) {

            BufferedImage homeImage = ImageIO.read(new URL(String.format(logosHostUrl, homeId)));
            BufferedImage visitorImage = ImageIO.read(new URL(String.format(logosHostUrl, visitorId)));

            result = joinBufferedImage(homeImage, visitorImage);
        }

       return result;

    }

    private String getTeamFromXML(String teamName){

        try {

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(ResourceUtils.getFile("classpath:teams.xml"));

            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "//*[contains(@n,'" + teamName + "')]";

            NodeList nodes = (NodeList) xPath.evaluate(expression, doc, XPathConstants.NODESET);

            if(nodes.getLength() > 1){

                Element team = (Element) nodes.item(0);
                return team.getAttribute("id");
            }

        } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static BufferedImage joinBufferedImage(BufferedImage homeImage, BufferedImage visitorImage) {
        int offset = 2;
        int width = homeImage.getWidth() + visitorImage.getWidth() + offset;
        int height = Math.max(homeImage.getHeight(), visitorImage.getHeight()) + offset;
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.BLACK);
        g2.fillRect(0, 0, width, height);
        g2.setColor(oldColor);
        g2.drawImage(homeImage, null, 0, 0);
        g2.drawImage(visitorImage, null, homeImage.getWidth() + offset, 0);
        g2.dispose();
        return newImage;
    }
}
