package com.lordjoe.utilities;

import java.lang.reflect.Method;

/**
 * com.lordjoe.utilities.MainRunner
 * User: Steve
 * Date: 2/25/2020
 * args[0] is the class name holding the main
 * other args are the arguments
 */
public class MainRunner {

    public static String[] displaceArgs(String[] args)
    {
        String[] ret = new String[args.length - 1];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = args[i + 1] ;

        }
        return ret;
    }

    public static void main(String[] args) throws Exception {
        String className = args[0];
        Class mainClass = Class.forName(className);
        Method mainMethod = mainClass.getMethod("main",String[].class);
        String[] displacedArgs = displaceArgs(args);
        mainMethod.invoke(null,(Object)displacedArgs);
    }

}
