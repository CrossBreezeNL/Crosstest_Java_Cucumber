
// CommandLineProcessExecutor executes shell commands on the host OS.
// It supports both Windows and Unix-like systems, and captures the output for verification.
package com.xbreeze.xtest.process.execution;

import com.xbreeze.xtest.config.CommandLineConfig;
import com.xbreeze.xtest.config.ProcessConfig;
import com.xbreeze.xtest.exception.XTestProcessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * CommandLineProcessExecutor executes a command line process using the system shell.
 * It supports both Windows (cmd.exe) and Unix-like (bash) environments.
 * The output of the last executed command is stored for later verification.
 */
public class CommandLineProcessExecutor implements ProcessExecutor {
    // Holds the output of the executed command
    private String commandOutput  = null;
    // Holds just the assembled command text (without shell tool prefix)
    private String lastCommandText = null;
    // Holds the Windows execution tool prefix (e.g., "cmd.exe /c")
    private String lastWindowsToolPrefix = null;
    // Holds the other OS execution tool prefix (e.g., "bash -c")
    private String lastOtherToolPrefix = null;
    // Reference to the currently running process, used for cleanup
    private Process process = null;

    public CommandLineProcessExecutor() {
        // Add a shutdown hook to ensure process is killed if JVM exits unexpectedly
        // Note: This is a last-resort safety net; normal cleanup should happen via cleanUp()
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }));
    }

    /**
     * Executes the given command using the appropriate shell for the OS.
     * Delegates to the overloaded method with no CommandLineConfig.
     *
     * @param config  The process configuration (not used for command line execution)
     * @param command The command to execute
     * @throws XTestProcessException if the command fails or an error occurs
     */
    @Override
    public void runProcess(ProcessConfig config, String command) throws XTestProcessException {
        runProcess(config, command, null);
    }

    /**
     * Executes the given command using the shell specified in the CommandLineConfig,
     * or the OS-appropriate default shell if clConfig is null.
     * Captures and stores the output. Throws an exception if the command fails.
     *
     * @param config   The process configuration (not used for command line execution)
     * @param command  The command to execute
     * @param clConfig The commandline configuration specifying tool, flags, and working directory (may be null)
     * @throws XTestProcessException if the command fails or an error occurs
     */
    public void runProcess(ProcessConfig config, String command, CommandLineConfig clConfig) throws XTestProcessException {
        String tool;
        String toolFlag;
        String workingDirectory;
        String windowsTool;
        String windowsToolFlag;
        String otherTool;
        String otherToolFlag;

        if (clConfig != null) {
            tool = clConfig.getEffectiveTool();
            toolFlag = clConfig.getEffectiveToolFlag();
            workingDirectory = clConfig.getEffectiveWorkingDirectory();
            windowsTool = clConfig.getWindowsTool();
            windowsToolFlag = clConfig.getWindowsToolFlag();
            otherTool = clConfig.getOtherTool();
            otherToolFlag = clConfig.getOtherToolFlag();
        } else {
            // Default behavior: detect OS and use default shell
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                tool = "cmd.exe";
                toolFlag = "/c";
            } else {
                tool = "bash";
                toolFlag = "-c";
            }
            workingDirectory = null;
            windowsTool = "cmd.exe";
            windowsToolFlag = "/c";
            otherTool = "bash";
            otherToolFlag = "-c";
        }

        // Store the command text and both OS tool prefixes separately for verification
        lastCommandText = command;
        lastWindowsToolPrefix = windowsTool + " " + windowsToolFlag;
        lastOtherToolPrefix = otherTool + " " + otherToolFlag;

        ProcessBuilder builder = new ProcessBuilder(tool, toolFlag, command);
        if (workingDirectory != null && !workingDirectory.isEmpty()) {
            builder.directory(new File(workingDirectory));
        }
        // Merge stderr with stdout so all output is captured together
        builder.redirectErrorStream(true);
        StringBuilder output = new StringBuilder();
        try {
            // Start the process and read its output (stdout and stderr)
            process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            // Read all output lines from the process
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
            // Wait for the process to finish and get the exit code
            int exitCode = process.waitFor();
            commandOutput = output.toString();
            if (exitCode != 0) {
                // Throw an exception if the command failed (non-zero exit code)
                throw new XTestProcessException("Command failed with exit code: " + exitCode + "\nOutput:\n" + commandOutput);
            }
        } catch (IOException | InterruptedException e) {
            // Capture any output produced before the error/exception
            commandOutput = output.toString();
            // Ensure the process is killed if an error occurs
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
            throw new XTestProcessException("Error running command: " + e.getMessage() + "\nOutput:\n" + commandOutput);
        }
    }

    /**
     * Ensures any running process is forcibly terminated.
     * Should be called after process execution or on test cleanup.
     */
    @Override
    public void cleanUp() throws XTestProcessException {
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
        }
    }

    /**
     * Returns the output of the last executed command.
     * @return The command output, or null if no command has been executed.
     */
    public String getCommandOutput() {
        return commandOutput;
    }

    /**
     * Returns the assembled command text without the shell tool prefix.
     * For example: "echo dbt run --select my_model --target dev"
     * @return The command text, or null if no command has been executed.
     */
    public String getLastCommandText() {
        return lastCommandText;
    }

    /**
     * Returns the execution tool prefix for the specified OS type.
     * For "windows": e.g., "cmd.exe /c"
     * For "non-windows": e.g., "bash -c"
     * @param osType "windows" or "non-windows"
     * @return The tool prefix string, or null if no command has been executed.
     */
    public String getLastToolPrefix(String osType) {
        if ("windows".equals(osType)) {
            return lastWindowsToolPrefix;
        } else {
            return lastOtherToolPrefix;
        }
    }
}