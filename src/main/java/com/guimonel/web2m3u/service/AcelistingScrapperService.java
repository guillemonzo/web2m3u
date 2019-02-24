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

@Service
public class AcelistingScrapperService {

    @Value("${acelisting.url}")
    private String siteUrl;

    @Value("${acestream.host.stream.url}")
    private String acestreamHostUrl;

    public String scrapSite(){

        try {

            StringBuilder listBuilder = new StringBuilder();
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

                                    channelNameBuilder.append(column.text());
                                    channelNameBuilder.append(Constants.BLANK_SPACE);
                                    channelNameBuilder.append(Constants.HYPHEN);
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

                    return listBuilder.toString();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
