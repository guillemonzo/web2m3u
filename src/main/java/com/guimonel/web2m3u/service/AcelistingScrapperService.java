package com.guimonel.web2m3u.service;

import com.guimonel.web2m3u.constants.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class AcelistingScrapperService {

    @Value("${acelisting.url}")
    private String siteUrl;

    @Value("${acestream.host.stream.url}")
    private String acestreamHostUrl;

    @Value("${web2m3u.images.endpoint}")
    private String imagesEndpoint;

    public String scrapSite(){

        StringBuilder listBuilder = new StringBuilder();

        try {

            listBuilder.append(Constants.TAG_EXTM3U);
            listBuilder.append(Constants.NEW_LINE);

            Document doc = Jsoup.connect(siteUrl).get();

            Element table =  doc.body().select(".table.table-striped.table-bordered.table-condensed").get(0);

            for(Element child : table.children()) {

                if(child.tagName().equals("tbody")){

                    for(Element row : child.children()){

                        StringBuilder channelNameBuilder = new StringBuilder();

                        if(!row.hasClass("success")) {

                            for (Element column : row.children()) {

                                Elements links = column.select("a");

                                if(CollectionUtils.isEmpty(links)) {

                                    //Build the channel name

                                    //Eliminate day and league from the chanel name
                                    String mutedText = column.select("td.text-muted").text();
                                    String text = column.text().replace(mutedText, "");

                                    channelNameBuilder.append(text);
                                    channelNameBuilder.append(Constants.BLANK_SPACE);
                                    channelNameBuilder.append(Constants.PIPE);
                                    channelNameBuilder.append(Constants.BLANK_SPACE);
                                }
                                else {

                                    //Iterate over the links

                                    for(Element link : links){

                                        String streamId = link.attr("href").replace("acestream://", "");

                                        StringBuilder channelNumber = new StringBuilder();
                                        channelNumber.append(Constants.OPENING_PARENTHESIS);
                                        channelNumber.append(link.text());
                                        channelNumber.append(Constants.CLOSING_PARENTHESIS);

                                        //Channel line
                                        listBuilder.append(Constants.TAG_EXTINF);
                                        listBuilder.append(channelNameBuilder.toString().concat(channelNumber.toString()));
                                        listBuilder.append(Constants.NEW_LINE);

                                        // Link line
                                        listBuilder.append(acestreamHostUrl);
                                        listBuilder.append(streamId);
                                        listBuilder.append(Constants.NEW_LINE);

                                    }

                                }

                            }
                        }

                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(listBuilder.length() > 0){
            listBuilder = addImageLinks(listBuilder);
        }

        return listBuilder.toString();
    }


    private StringBuilder addImageLinks(StringBuilder listBuilder){

        StringBuilder result = new StringBuilder();

        String list = listBuilder.toString();
        String[] lines = list.split(Constants.NEW_LINE);

        for(String line : lines){

            String[] columns = line.split("\\|");

            if(columns.length > 1){

                //Time
                String time = columns[0].replace(Constants.TAG_EXTINF, "").trim();

                //Teams
                String teamsString = columns[2];
                String[] teams = teamsString.split(Constants.HYPHEN);

                String home  = teams[0].trim();
                String visitor = teams[1].trim();

                //Channel
                String channel = columns[3].split(Constants.HYPHEN)[0].trim().replace(Constants.OPENING_PARENTHESIS, "");

                String logoUrl = String.format(imagesEndpoint, home, visitor, time, channel);

                try {

                    String logoAttribute = "tvg-logo=\""+ URLEncoder.encode(logoUrl, "UTF-8") + "\"";
                    line = line.replace(Constants.TAG_EXTINF, Constants.TAG_EXTINF.concat(Constants.BLANK_SPACE).concat(logoAttribute).concat(Constants.COMMA));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            result.append(line).append(Constants.NEW_LINE);

        }

        return result;
    }

}
