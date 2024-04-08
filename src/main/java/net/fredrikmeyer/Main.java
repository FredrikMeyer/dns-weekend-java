package net.fredrikmeyer;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) throws Exception {

        Options opts = new Options();
        Option url = new Option("u",
                "url",
                true,
                "URL to the server");
        url.setRequired(true);
        opts.addOption(url);

        Option recordTypeOption = new Option("r",
                "record-type",
                true,
                "Record type (A or TXT)");
        recordTypeOption.setRequired(false);
        opts.addOption(recordTypeOption);

        Option ipAddressOption = new Option("i",
                "ip",
                true,
                "IP address");
        ipAddressOption.setRequired(false);
        opts.addOption(ipAddressOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null; //not a good practice, it serves it purpose

        try {
            cmd = parser.parse(opts,
                    args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name",
                    opts);

            System.exit(1);
        }

        String domainName = cmd.getOptionValue("url");
        String recordType = cmd.getOptionValue("record-type");
        String ipAddress = cmd.getOptionValue("ip");

        if (recordType == null) {
            recordType = "A";
        }

        if (ipAddress == null) {
            ipAddress = "8.8.8.8";
        }

        System.out.println("Asking for: " + domainName);

        Query query = new Query();

        var resourceType = fromCode(recordType);

        String s = query.resolve(domainName,
                resourceType);

        System.out.println("Answer: " + s);
    }

    public static ResourceType fromCode(String code) {
        if (code == null) return null;
        return switch (code) {
            case "TXT" -> ResourceType.TYPE_TXT;
            case "A" -> ResourceType.TYPE_A;
            case "NS" -> ResourceType.TYPE_NS;
            case "CNAMe" -> ResourceType.TYPE_CNAME;
            default -> null;
        };
    }
}