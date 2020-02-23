package com.guimonel.web2m3u;

import com.guimonel.web2m3u.constants.Constants;
import com.guimonel.web2m3u.service.AcelistingScrapperService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Web2m3uApplicationTests {

	@Autowired
	private AcelistingScrapperService acelistingScrapperService;

	@Value("${web2m3u.images.endpoint}")
	private String imagesEndpoint;

	@Test
	public void acelistingScrapper() {
		System.out.print(acelistingScrapperService.scrapSite());
	}


	@Test
	public void logoUrl(){

		StringBuilder listBuilder = new StringBuilder();
		StringBuilder result = new StringBuilder();

		listBuilder.append("#EXTINF:0,15:00  | SOCCER | OLYMPIQUE LYON-DIJON  | (22 - FRE)").append(Constants.NEW_LINE);
		listBuilder.append("http://192.168.0.20:6878/ace/manifest.m3u8?id=e66a9948a93cc936b4f9ca02e33b86003afa0f0f").append(Constants.NEW_LINE);
		listBuilder.append("#EXTINF:0,16:00  | SOCCER | GRANADA-MALAGA  | (10 - SPA)").append(Constants.NEW_LINE);
		listBuilder.append("http://192.168.0.20:6878/ace/manifest.m3u8?id=53e0d6ed38f68d7b2a0557c17869fb04fe45365d").append(Constants.NEW_LINE);

		String list = listBuilder.toString();

		String[] lines = list.split(Constants.NEW_LINE);
		for(String line : lines){

			String[] columns = line.split("\\|");

			if(columns.length > 1){

				//Time
				String time = columns[0].replace("#EXTINF:0,", "").trim();


				//Teams
				String teamsString = columns[2];
				String[] teams = teamsString.split(Constants.HYPHEN);

				String home  = teams[0].trim();
				String visitor = teams[1].trim();


				//Channel
				String channel = columns[3].split(Constants.HYPHEN)[0].trim().replace(Constants.OPENING_PARENTHESIS, "");

				String logoUrl = String.format(imagesEndpoint, home, visitor, time, channel);

				line = line.replace(Constants.TAG_EXTINF, Constants.TAG_EXTINF + "tvg-logo=\""+ logoUrl + "\",");

			}

			result.append(line).append(Constants.NEW_LINE);

		}


		System.out.print(result.toString());


	}


	@Test
	public void xPath(){

		try {

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(ResourceUtils.getFile("classpath:teams.xml"));

			XPath xPath = XPathFactory.newInstance().newXPath();

			String expression = "//*[contains(@n,'Valencia')]";

			NodeList nodes = (NodeList) xPath.evaluate(expression, doc, XPathConstants.NODESET);

			if(nodes.getLength() > 1){

				Element team = (Element) nodes.item(0);
				System.out.println(team.getAttribute("n"));
			}

		} catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}


	}

}
