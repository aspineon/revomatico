package ro.dcsi.internship;

import org.apache.commons.cli.*;

public class UserSyncApp {

  // App name
  private final static String appName = "usersync";
  private final static String usage = appName + " <src> <dest>";

  // Option strings
  private final static String forgeRockShortOption = "f";
  private final static String forgeRockLongOption = "forgerock";
  private final static String csvShortOption = "c";
  private final static String csvLongOption = "csv";

  // Print help
  private static void printHelp(Options options) {
    // Print Help
    HelpFormatter help = new HelpFormatter();
    help.printHelp(usage, options);
  }

  // Parse option for UserDao
  private static IterableUserDao parseDaoInfo(Option daoInfo) {

    // Parse CSV Dao
    if (daoInfo.getOpt().equals(csvShortOption) || (daoInfo.hasLongOpt() && daoInfo.getLongOpt().equals(csvLongOption)))
      return new ExtendedCsvUserDao(daoInfo.getValue());

    // Parse ForgeRockDao
    if (daoInfo.getOpt().equals(forgeRockShortOption)
        || (daoInfo.hasLongOpt() && daoInfo.getLongOpt().equals(forgeRockLongOption)))
      return new ExtendedForgeRockUserDao(new OpenIdConfig(daoInfo.getValue(0), daoInfo.getValue(1), daoInfo.getValue(2)));

    return new ExtendedCsvUserDao("");
  }

  public static void main(String[] args) {

    // Create option group
    Options options = new Options();

    // Creating options
    Option forgeRock = Option.builder(forgeRockShortOption).hasArg(true).argName("server addr> <username> <password")
        .longOpt(forgeRockLongOption).desc("Option to specify forgeRock server").numberOfArgs(3).valueSeparator(' ')
        .build();
    Option csv = Option.builder(csvShortOption).hasArg(true).argName("file").longOpt(csvLongOption)
        .desc("Option to Specify CSV file").numberOfArgs(1).valueSeparator(' ').build();

    // Add options to group
    options.addOption(forgeRock);
    options.addOption(csv);

    try {
      Option[] parsedOptions = new DefaultParser().parse(options, args).getOptions();

      // check number of option
      if (parsedOptions.length == 2) {
        // Parse first option
        IterableUserDao src = parseDaoInfo(parsedOptions[0]);
        // Parse second option
        IterableUserDao dest = parseDaoInfo(parsedOptions[1]);

        // Actual function
        UserSync function = new UserSync();
        function.copyUsers(src, dest);
      } else {
        printHelp(options);
        throw new RuntimeException("Invalid number of options");
      }
    } catch (ParseException e) {
      printHelp(options);
      throw new RuntimeException("Invalid Arguments", e);
    }
  }
}