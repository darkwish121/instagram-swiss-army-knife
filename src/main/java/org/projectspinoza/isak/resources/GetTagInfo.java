/*
 * Copyright (C) 2015 Orbit Software Solutions
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.projectspinoza.isak.resources;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.tags.TagInfoData;
import org.jinstagram.entity.tags.TagInfoFeed;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get information about a tag object.
 *
 */
public class GetTagInfo {

    private static final Logger log = LogManager.getRootLogger();
    private static String userApiKey = "";
    private static String tagName = "";
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public static void get(String[] args) throws IOException {

        userApiKey = StringEscapeUtils.escapeJava(args[0]);
        tagName = StringEscapeUtils.escapeJava(args[1]);
        fileOutputFormat = StringEscapeUtils.escapeJava(args[2]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[3]);

        Instagram instagram = new Instagram(userApiKey);
        
        try {
            
            PrintWriter writer = null;
            if ("json".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {
            
                writer = new PrintWriter(fileOutputPath
                                    + "/" + tagName + "_tag_info", "UTF-8");
            }
            
            TagInfoFeed tagFeed = instagram.getTagInfo(tagName);
            TagInfoData tagData = tagFeed.getTagInfo();
            
            if ("console".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {
            
                log.info("name: " + tagData.getTagName());
                log.info("media_count: " + tagData.getMediaCount());
            }

            if ("json".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {

                String json = new Gson().toJson(tagData);
                if(writer != null)
                    writer.println(json);
            }
            if(writer != null)
                writer.close();
            
            Helpers.showRateLimitStatus(tagFeed.getAPILimitStatus(),
                        tagFeed.getRemainingLimitStatus());
            log.info("!!! DONE !!!");
        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        }
    }
}