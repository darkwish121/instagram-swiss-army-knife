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
package org.projectspinoza.isak.commandbox;

import com.beust.jcommander.JCommander;
import org.junit.Assert;
import org.junit.Test;
/**
 * Unit test for CommandExecutor.
 * 
 * 
 */
public class CommandExecutorTest {
    
    private static CommandAndControl isakCommand;
    private static JCommander subCommander;
    private static JCommander rootCommander;
    private static JCommander isakCommander;

    public static Object getSubCommand(String parsedCommand) {
        return isakCommander.getCommands().get(parsedCommand).getObjects()
                .get(0);
    }
    
    @Test
    public void evaluateCommands() {
        isakCommand = new CommandAndControl();
        rootCommander = null;

        rootCommander = new JCommander();
        rootCommander.addCommand("isak", isakCommand);
        subCommander = rootCommander.getCommands().get("isak");
        isakCommander = subCommander;
        isakCommander.addCommand(new getUserInfo());
        
        String[] args = {"isak", "getUserInfo", "-uid", "123", "-of", "JSON", 
            "-o", "/BigData/isakData" };
        rootCommander.parse(args);
            
        String parsedCommand = isakCommander.getParsedCommand();
        
        Assert.assertEquals("getUserInfo", parsedCommand);
        
        Object subCommand = getSubCommand(parsedCommand);
        
        getUserInfo userInfo = (getUserInfo) subCommand;
        
        Assert.assertEquals("123", userInfo.userId);
        Assert.assertEquals("JSON", userInfo.outputFormat);
        Assert.assertEquals("/BigData/isakData", userInfo.outputPath);
    }
}
