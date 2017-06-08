package com.monk;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class Main {

    public static void main(String[] args) {
        Config config = ConfigFactory.load("config");
        ConfigObject obj = config.root();
        for (String s : obj.keySet()) {
            System.out.println(s);
        }
    }
}
