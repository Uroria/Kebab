package com.uroria.kebab.application;

import com.uroria.kebab.server.KebabConfiguration;
import com.uroria.kebab.server.KebabServer;
import io.sentry.Sentry;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Optional;

public final class Bootstrap {
    private static final Options OPTIONS = new Options();
    private static CommandLine cmd;

    public static void main(String... arguments) {
        KebabServer.logger().info("Loading configuration");
        setOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            cmd = parser.parse(OPTIONS, arguments);
        } catch (ParseException exception) {
            KebabServer.logger().error("Cannot parse arguments", exception);
            return;
        }

        KebabConfiguration.reload();
        getArgument("host").ifPresent(KebabConfiguration::setHost);
        getArgument("sentrydsn").ifPresent(KebabConfiguration::setSentryDSN);
        getArgument("version").ifPresent(KebabConfiguration::setVersion);

        if (KebabConfiguration.isSentryEnabled()) {
            String sentryDSN = KebabConfiguration.getSentryDSN();
            if (sentryDSN == null) KebabConfiguration.setSentryEnabled(false);
            Sentry.init(options -> {
                options.setDsn(sentryDSN);
                options.setTracesSampleRate(1.0);
            });
        }

        KebabServer server = new KebabServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::terminate));
        server.start();
    }

    private static void setOptions() {
        OPTIONS.addOption(new Option("h", "host", true, "server host address"));
        OPTIONS.addOption(new Option("p", "port", true, "server port"));
        OPTIONS.addOption(new Option("s", "sentry", false, "whether sentry is enabled"));
        OPTIONS.addOption(new Option("dsn", "sentrydsn", true, "the sentry dsn"));
        OPTIONS.addOption(new Option("v", "version", true, "minecraft version"));
    }

    private static Optional<String> getArgument(String name) {
        return Optional.ofNullable(cmd.getOptionValue(name));
    }
}
