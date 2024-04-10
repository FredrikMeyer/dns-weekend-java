package net.fredrikmeyer.dnsweekend;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Options opts = createOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;

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

        if (recordType == null) {
            recordType = "A";
        }

        System.out.println("Asking for: " + domainName);

        Query query = new Query();

        var resourceType = fromCode(recordType);

        String s = query.resolve(domainName,
                resourceType);

        System.out.println("Answer: " + s);
    }

    private static Options createOptions() {
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
        return opts;
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