package com.dbsoftwares.commandapi.utils;

import lombok.Data;

@Data
public class MessageConfig {

    private String noPermissionMessage = "&cYou are not allowed to do this!";
    private String usageMessage = "&ePlease use &b{usage}!";
    private String helpHeaderMessage = "&e{commandName} Command Help:";
    private String helpFormat = "&b- &e{usage}";

}
