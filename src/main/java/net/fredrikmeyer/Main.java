package net.fredrikmeyer;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Supply a domain name.");
            return;
        }
        var domainName = args[0];
        System.out.println("Asking for: " + domainName);

        Query query = new Query();

        String s = query.lookupDomain(domainName);

        System.out.println("Answer: " + s);
    }
}