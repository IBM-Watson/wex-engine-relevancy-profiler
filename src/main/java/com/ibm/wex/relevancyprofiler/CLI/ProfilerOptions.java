package com.ibm.wex.relevancyprofiler.CLI;


import com.ibm.wex.relevancyprofiler.CLI.InputModes;

public class ProfilerOptions {

    public static final int DEFAULT_SLEEP_TIME = 0;
    public static final int DEFAULT_THREAD_COUNT = 20;
    public static final int DEFAULT_MAX_RESULTS = 9999999;

    private String _groundTruthPath = "";
    private String _outPath = "";
    private int _sleepTime = DEFAULT_SLEEP_TIME;
    private int _numberOfThreads = DEFAULT_THREAD_COUNT;

    private String _engineEndpoint = null;
    private String _apiUsername = "";
    private String _apiPassword = "";
    private int _maxResults = DEFAULT_MAX_RESULTS;
    private String _projectSettings = "";

    private boolean _outputMetricsOnly = false;

    private InputModes.InputMode _inputMode = InputModes.InputMode.EngineAPI;

    private String _queryResultFolder = "";


    public boolean ShouldThrottle() { return _sleepTime > 0; }

    public int getMaxResults() { return _maxResults; }
    public void setMaxResults(int value) { _maxResults = value; }
    public void setMaxResults(String value) { _maxResults = Integer.valueOf(value); }

    public int getThreadCount() { return _numberOfThreads; }
    public void setThreadCount(int value) { _numberOfThreads = value; }
    public void setThreadCount(String value) { _numberOfThreads = Integer.valueOf(value); }

    public String getEngineEndpoint() { return _engineEndpoint; }
    public void setEngineEndpoint(String value) { _engineEndpoint = value; }

    public String getProjectSettings() { return _projectSettings; }
    public void setProjectSettings(String value) { _projectSettings = value; }

    public String getGroundTruthPath() { return _groundTruthPath; }
    public void setGroundTruthPath(String value) { _groundTruthPath = value; }

    public Boolean shouldOutputMetricsOnly() { return _outputMetricsOnly; }
    public void setMetricsOnly(boolean value) { _outputMetricsOnly = value; }
    public void setMetricsOnly(String value) { _outputMetricsOnly = Boolean.valueOf(value); }

    public String getOutputPath() { return _outPath; }
    public void setOutputPath(String value) { _outPath = value; }

    public String getApiUsername() { return _apiUsername; }
    public void setApiUsername(String value) { _apiUsername = value; }

    public String getApiPassword() { return _apiPassword; }
    public void setApiPassword(String value) { _apiPassword = value; }

    public String getQueryResultFolder() { return _queryResultFolder; }
    public void setQueryResultFolder(String value) { _queryResultFolder = value; }

    public InputModes.InputMode getInputMode() { return _inputMode; }
    public void setInputMode(InputModes.InputMode value) { _inputMode = value; }
    public void setInputMode(String value) { _inputMode = InputModes.InputMode.valueOf(value); }

    public int getSleepTime() { return _sleepTime; }
    public void setSleepTime(int value) { _sleepTime = value; }
    public void setSleepTime(String value) { _sleepTime = Integer.valueOf(value); }


    public boolean isValidOptions() { return getError() == null; }

    public String getError() {
        if (_inputMode == InputModes.InputMode.EngineAPI ||
                _inputMode == InputModes.InputMode.EngineSearch ||
                _inputMode == InputModes.InputMode.EngineXMLFeed) {

            if (_engineEndpoint == null || _engineEndpoint.isEmpty()) {
                return "Missing required field: Engine Endpoint";
            }

            if (_inputMode == InputModes.InputMode.EngineAPI) {
                if (_apiPassword == null || _apiPassword.isEmpty()) {
                    return "Missing required field: API Password";
                }
                else if (_apiUsername == null || _apiUsername.isEmpty()) {
                    return "Missing required field: API Username";
                }
            }

            if (_inputMode == InputModes.InputMode.EngineXMLFeed) {
                if (_projectSettings == null || _projectSettings.isEmpty()) {
                    return "missing required field: Project Settings";
                }
            }
        }
        else if (_inputMode == InputModes.InputMode.FileCSV || _inputMode == InputModes.InputMode.FileEngineXMLFeed) {
            if (_queryResultFolder == null | _queryResultFolder.isEmpty()) {
                return "Missing required field: Query Results Folder";
            }
        }

        if (_outPath == null || _outPath.isEmpty()) {
            return "Missing required field: Output Path";
        }

        if (_groundTruthPath == null || _groundTruthPath.isEmpty()) {
            return "Missing required field: Ground Truth File Path";
        }

        return null;
    }
}
