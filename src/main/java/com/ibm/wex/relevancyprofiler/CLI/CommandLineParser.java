package com.ibm.wex.relevancyprofiler.CLI;

import org.apache.commons.cli.*;



public class CommandLineParser {

    private final String _helpFlag = "h";
    private final String _groundTruthPathFlag = "g";
    private final String _sleepTimeFlag = "s";
    private final String _threadCountFlag = "c";
    private final String _metricsOnlyFlag = "m";
    private final String _outPathFlag = "o";

    private final String _inputModeFlag = "i";  // vxmlfeed|vapi|vsearch|file

    // Engine XML Feed Options
    private final String _projectSettingsFlag = "s";

    // Engine API Flags
    private final String _usernameFlag = "u";
    private final String _passwordFlag = "p";

    // Flags used by the WEX searchers
    private final String _engineEndPointUrlFlag = "e";
    private final String _maxResultsFlag = "r";

    // Flags used by the file processing
    private final String _queryFolderFlag = "q";

    private CommandLine _cmd = null;




    public void ProcessArguments(String[] args) throws OptionsParseException {
        Options options = SetupOptions();
        org.apache.commons.cli.CommandLineParser parser = new GnuParser();

        try {
            _cmd = parser.parse(options, args);
        } catch (ParseException e) {
            throw new OptionsParseException(e);
        }
    }


    public boolean wantsHelp() { return  _cmd.hasOption(_helpFlag); }


    public void ShowHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("relevancy-profiler", SetupOptions());
    }



    private Options SetupOptions() {
        Options options = new Options();

        // add options
        options.addOption(_groundTruthPathFlag, "ground-truth", true, "Path to a file containing the list of queries to be used as a baseline for measuring relevancy (ground truth file).");
        options.addOption(_outPathFlag, "out-path", true, "The name of a folder to save the relevancy reports.");
        options.addOption(_sleepTimeFlag, "sleep", true, "Optional  (Default " + ProfilerOptions.DEFAULT_SLEEP_TIME + "). The amount of time in ms to wait between queries.");
        options.addOption(_threadCountFlag, "concurrent-requests", true, "Optional (Default " + ProfilerOptions.DEFAULT_THREAD_COUNT + ").  The number of concurrent requests that can be made to the server. This along with the sleep time allow you to adjust the profiling tool's aggressiveness when querying the servere.");
        options.addOption(_metricsOnlyFlag, "metrics-only", false, "Optional. Produce only a metrics summary with no other reports.");
        options.addOption(_inputModeFlag, "input-mode", true, "Optional (Default " + InputModes.InputMode.EngineSearch.toString() + "). Specifies the method by which data will be read by the profiler.  Valid modes are " + ListModes());

        // Engine Options
        options.addOption(_engineEndPointUrlFlag, "engine-endpoint", true, "(Required for all Engine input modes). Valid URL which points to the full Engine endpoint in the style needed for the type of search requests being conducted.");
        options.addOption(_projectSettingsFlag, "project-settings", true, "The Engine project settings to be used for searching a Velocity XML Feed.");
        options.addOption(_usernameFlag, "api-username", true, "(Required for " + InputModes.InputMode.EngineAPI.toString() + "). The username to authenticate against the Engine API.");
        options.addOption(_passwordFlag, "api-password", true, "(Required for " + InputModes.InputMode.EngineAPI.toString() + "). The password to authenticate against the Engine API.");
        options.addOption(_maxResultsFlag, "max-results", true, "Optional (Default " + ProfilerOptions.DEFAULT_MAX_RESULTS + ").  The number of results to limit the results set. Only used with Engine search modes.");

        // File Options
        options.addOption(_queryFolderFlag, "query-folder", true, "(Required for file based input modes). The folder containing result files to be analyzed.");

        // help!
        options.addOption(_helpFlag, "help", false, "Shows this help message.");

        return options;
    }



    public ProfilerOptions LoadSettings() {
        ProfilerOptions settings = new ProfilerOptions();

        if (_cmd.hasOption(_maxResultsFlag)) {
            settings.setMaxResults(_cmd.getOptionValue(_maxResultsFlag));
        }

        if (_cmd.hasOption(_threadCountFlag))
        settings.setThreadCount(_cmd.getOptionValue(_threadCountFlag));

        if (_cmd.hasOption(_engineEndPointUrlFlag)) {
            settings.setEngineEndpoint(_cmd.getOptionValue(_engineEndPointUrlFlag));
        }

        if (_cmd.hasOption(_projectSettingsFlag)) {
            settings.setProjectSettings(_cmd.getOptionValue(_projectSettingsFlag));
        }

        if (_cmd.hasOption(_groundTruthPathFlag)) {
            settings.setGroundTruthPath(_cmd.getOptionValue(_groundTruthPathFlag));
        }

        if (_cmd.hasOption(_metricsOnlyFlag)) {
            settings.setMetricsOnly(_cmd.hasOption(_metricsOnlyFlag));
        }

        if (_cmd.hasOption(_outPathFlag)) {
            settings.setOutputPath(_cmd.getOptionValue(_outPathFlag));
        }

        if (_cmd.hasOption(_usernameFlag)) {
            settings.setApiUsername(_cmd.getOptionValue(_usernameFlag));
        }

        if (_cmd.hasOption(_passwordFlag)) {
            settings.setApiPassword(_cmd.getOptionValue(_passwordFlag));
        }

        if (_cmd.hasOption(_queryFolderFlag)) {
            settings.setQueryResultFolder(_cmd.getOptionValue(_queryFolderFlag));
        }

        if (_cmd.hasOption(_inputModeFlag)) {
            settings.setInputMode(_cmd.getOptionValue(_inputModeFlag));
        }

        if (_cmd.hasOption(_sleepTimeFlag)) {
            settings.setSleepTime(_cmd.getOptionValue(_sleepTimeFlag));
        }

        return settings;
    }


    private String ListModes() {
        StringBuilder modesList = new StringBuilder();
        for (InputModes.InputMode mode : InputModes.InputMode.values()) {
            modesList.append(mode.toString()).append("|");
        }

        return modesList.toString();
    }
}
