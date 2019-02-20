package com.fta.netty;

import org.apache.log4j.Logger;

public class TestLogMain {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(TestLogMain.class);
//  记录 debug 级别的信息
        logger.debug("This is debug message.");
        //  记录 info 级别的信息
        logger.info("This is info message.");
        //  记录 error 级别的信息
        logger.error("This is error message.");
    }
}
