package com.lordjoe.ssh;

import com.jcraft.jsch.SftpProgressMonitor;
/**
 * com.lordjoe.ssh.MyProgressMonitor
 * User: Steve
 * Date: 10/16/20
 */




/**
 * com.lordjoe.ssh.MyProgressMonitor
 * User: Steve
 * Date: 10/23/2019
 */
public class MyProgressMonitor implements SftpProgressMonitor {
    private long count;
    private String name;
    private String dest;

    @Override
    public void init(int i, String s, String s1, long l) {
        count = 0;
        name = s;
        dest = s1;
    }

    @Override
    public boolean count(long l) {
        count = l;
        return true;
    }

    @Override
    public void end() {

    }

    public long getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
}
