package bozoware.base.irc.ircserver;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class IRCServer extends Thread {
    Socket socket;
    Scanner in;
    PrintWriter out;
    Scanner inp;
    boolean loggedIN;
    String username;

    IRCSender sender;

    public IRCServer(Socket socket, Scanner in, PrintWriter out, Scanner inp, String username) {
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