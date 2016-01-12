/*
 * Copyright (C) 2015
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get the most recent media published by a user.
 *
 */
public class GetRecentMediaFeed {

    private static final Logger log = LogManager.getRootLogger();
    private static String userApiKey = "";
    private static String userId = "";
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public static void get(String[] args) throws IOException {

        userApiKey = StringEscapeUtils.escapeJava(args[0]);
        userId = StringEscapeUtils.escapeJava(args[1]);
        fileOutputFormat = StringEscapeUtils.escapeJava(args[2]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[3]);

        Instagram instagram = new Instagram(userApiKey);

        int mediaCount = 0;

        try (
                // put JSON in a file
                PrintWriter writer = new PrintWriter(fileOutputPath + "/"
                        + userId + "_recent_media", "UTF-8")) {

            MediaFeed userMediaFeed = instagram.getRecentMediaFeed(userId);
            List<MediaFeedData> mediaFeedsList = userMediaFeed.getData();

            if (mediaFeedsList.size() > 0) {

                log.info("Total media found in this call: "
                        + mediaFeedsList.size());

                Helpers.showRateLimitStatus(userMediaFeed.getAPILimitStatus(),
                        userMediaFeed.getRemainingLimitStatus());

                mediaCount += mediaFeedsList.size();

                for (MediaFeedData mediaData : mediaFeedsList) {

                    if ("json".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(mediaData);
                        writer.println(json);
                    }
                }
                // remove elements from list for next chunk
                mediaFeedsList.clear();

                MediaFeed recentUserMediaNextPage = instagram.
                        getRecentMediaNextPage(userMediaFeed.getPagination());

                while (recentUserMediaNextPage.getPagination().
                        getNextUrl() != null) {

                    mediaFeedsList.addAll(recentUserMediaNextPage.getData());

                    log.info("Total media found in this call: "
                            + mediaFeedsList.size());

                    mediaCount += mediaFeedsList.size();

                    for (MediaFeedData mediaData : mediaFeedsList) {

                        if ("json".equals(fileOutputFormat.toLowerCase())) {

                            String json = new Gson().toJson(mediaData);
                            writer.println(json);
                        }
                    }
                    // remove elements from list for next chunk
                    mediaFeedsList.clear();

                    recentUserMediaNextPage = instagram.
                            getRecentMediaNextPage(recentUserMediaNextPage.
                                    getPagination());

                    log.info("Total media collected: " + mediaCount);
                    if (recentUserMediaNextPage.getRemainingLimitStatus() > 0) {
                        Helpers.showRateLimitStatus(recentUserMediaNextPage.
                                getAPILimitStatus(),
                                recentUserMediaNextPage.
                                getRemainingLimitStatus());
                    }
                }
                log.info("!!! DONE !!!");
            } else {
                log.info("No media found against provided userid.");
            }
        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }
}
