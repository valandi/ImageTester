import com.applitools.eyes.Eyes;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.RectangleSize;
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
            eyes.setApiKey(cmd.getOptionValue("k"));
            if (cmd.hasOption("s")) eyes.setServerUrl(new URI(cmd.getOptionValue("s")));
            if (cmd.hasOption("ml")) eyes.setMatchLevel(Utils.parseEnum(MatchLevel.class, cmd.getOptionValue("ml")));
            if (cmd.hasOption("p")) eyes.setProxy(new ProxySettings(cmd.getOptionValue("p")));
            if (cmd.hasOption("br")) eyes.setBranchName(cmd.getOptionValue("br"));
            if (cmd.hasOption("pb")) eyes.setParentBranchName(cmd.getOptionValue("pb"));
            if (cmd.hasOption("bn")) eyes.setBaselineName(cmd.getOptionValue("bn"));
            if (cmd.hasOption("pb") && !cmd.hasOption("br"))
                throw new ParseException("Parent Branches (pb) should be combined with branches (br).");

            File root = new File(cmd.getOptionValue("f", "."));
            root = new File(root.getCanonicalPath());

            RectangleSize viewport = null;
            if (cmd.hasOption("vs")) {
                String[] dims = cmd.getOptionValue("vs").split("x");
                if (dims.length != 2)
                    throw new ParseException("invalid viewport-size, make sure the call is -vs <width>x<height>");
                viewport = new RectangleSize(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]));
            }

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
                .argName("address")
                .build()
        );

        options.addOption(Option.builder("s")
                .longOpt("server")
                .desc("Set custom server url")
                .hasArg()
                .argName("address")
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
        return options;
    }
}
