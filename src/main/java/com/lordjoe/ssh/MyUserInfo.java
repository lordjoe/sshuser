package com.lordjoe.ssh;

import com.jcraft.jsch.UserInfo;

/**
 * com.lordjoe.ssh.MyUserInfo
 * User: Steve
 * Date: 10/23/2019
 */
public class MyUserInfo implements UserInfo {

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean promptPassword(String message) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return false;
    }

    @Override
    public boolean promptYesNo(String message) {
        return false;
    }

    @Override
    public void showMessage(String message) {

    }
}
