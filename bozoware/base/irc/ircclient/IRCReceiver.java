package bozoware.base.irc.ircclient;

import bozoware.base.irc.ircserver.IRCSender;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class IRCReceiver extends Thread {
    Socket socket;
    Scanner in;
    PrintWriter out;
    Scanner inp;
    boolean loggedIN;
    String username;

    IRCSender sender;

    public IRCReceiver(Socket socket, Scanner in, PrintWriter out, Scanner inp, String username) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.inp = inp;
        this.loggedIN = false;
        this.username = username;
    }

    public IRCSender getSender() {
        return sender;
    }
}