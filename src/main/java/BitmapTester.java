import com.applitools.eyes.*;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

public class BitmapTester {
    public static void main(String[] args) {
        PrintStream out = System.out;

        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();

        try {
            CommandLine cmd = parser.parse(options, args);
            Eyes eyes = new Eyes();

            eyes.setAgentId("ImageTester on " + eyes.getAgentId());
            //API key
            eyes.setApiKey(cmd.getOptionValue("k"));
            // Applitools Server url
            if (cmd.hasOption("s")) eyes.setServerUrl(new URI(cmd.getOptionValue("s")));
            // Match level
            if (cmd.hasOption("ml")) eyes.setMatchLevel(Utils.parseEnum(MatchLevel.class, cmd.getOptionValue("ml")));
            // Proxy
            if (cmd.hasOption("p")) eyes.setProxy(new ProxySettings(cmd.getOptionValue("p")));
            // Branch name
            if (cmd.hasOption("br")) eyes.setBranchName(cmd.getOptionValue("br"));
            // Parent branch
            if (cmd.hasOption("pb")) eyes.setParentBranchName(cmd.getOptionValue("pb"));
            // Baseline name
            if (cmd.hasOption("bn")) eyes.setBaselineEnvName(cmd.getOptionValue("bn"));
            // Parent branch
            if (cmd.hasOption("pb") && !cmd.hasOption("br"))
                throw new ParseException("Parent Branches (pb) should be combined with branches (br).");
            // Log file
            if (cmd.hasOption("lf")) eyes.setLogHandler(new FileLogger(cmd.getOptionValue("lf"), true, true));
            // Set failed tests
            eyes.setSaveFailedTests(cmd.hasOption("as"));
            // Viewport size
            RectangleSize viewport = null;
            if (cmd.hasOption("vs")) {
                String[] dims = cmd.getOptionValue("vs").split("x");
                if (dims.length != 2)
                    throw new ParseException("invalid viewport-size, make sure the call is -vs <width>x<height>");
                viewport = new RectangleSize(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]));
            }
            // Folder path
            File root = new File(cmd.getOptionValue("f", "."));
            root = new File(root.getCanonicalPath());

            ITestable suite = Suite.build(root, cmd.getOptionValue("a", "ImageTester"), viewport);

            if (suite == null) {
                System.out.printf("Nothing to test!\n");
                System.exit(0);
            }

            suite.run(eyes);
        } catch (ParseException e) {
            out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("BitmapTester -k <api-key> [options]", options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        options.addOption(Option.builder("k")
                .longOpt("apiKey")
                .desc("Applitools api key")
                .required()
                .hasArg().argName("apikey")
                .build());

        options.addOption(Option.builder("a")
                .longOpt("AppName")
                .desc("Set own application name, default: ImageTester")
                .hasArg().argName("name")
                .build()
        );

        options.addOption(Option.builder("f")
                .longOpt("folder")
                .desc("Set the root folder to start the analysis, default: \\.")
                .hasArg().argName("path")
                .build()
        );

        options.addOption(Option.builder("p")
                .longOpt("proxy")
                .desc("Set proxy address, optional: <user> <password>")
                .hasArgs()//.numberOfArgs(3)
                .argName("url")
                .build()
        );

        options.addOption(Option.builder("s")
                .longOpt("server")
                .desc("Set Applitools server url")
                .hasArg()
                .argName("url")
                .build()
        );

        options.addOption(Option.builder("ml")
                .longOpt("matchLevel")
                .desc(String.format("Set match level to one of [%s], default = Strict", Utils.getEnumValues(MatchLevels.class)))
                .hasArg()
                .argName("level")
                .build());

        options.addOption(Option.builder("br")
                .longOpt("branch")
                .desc("Set branch name")
                .hasArg()
                .argName("name")
                .build());

        options.addOption(Option.builder("pb")
                .longOpt("parentBranch")
                .desc("Set parent branch name, optional when working with branches")
                .hasArg()
                .argName("name")
                .build());

        options.addOption(Option.builder("bn")
                .longOpt("baseline")
                .desc("Set baseline name")
                .hasArg()
                .argName("name")
                .build());

        options.addOption(Option.builder("vs")
                .longOpt("viewportsize")
                .desc("Declare viewport size identifier <width>x<height> ie. 1000x600, if not set,default will be the first image in every test")
                .hasArg()
                .argName("size")
                .build());

        options.addOption(Option.builder("lf")
                .longOpt("logFile")
                .desc("Specify Applitools log-file")
                .hasArg()
                .argName("file")
                .build());

        options.addOption(Option.builder("as")
                .longOpt("autoSave")
                .desc("Automatically save failed tests. Waring, might save buggy baselines without human inspection. ")
                .hasArg(false)
                .build());

        return options;
    }
}
