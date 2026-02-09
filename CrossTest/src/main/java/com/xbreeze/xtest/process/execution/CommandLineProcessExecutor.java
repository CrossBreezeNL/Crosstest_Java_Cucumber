
// CommandLineProcessExecutor executes shell commands on the host OS.
// It supports both Windows and Unix-like systems, and captures the output for verification.
package com.xbreeze.xtest.process.execution;

import com.xbreeze.xtest.config.CommandLineConfig;
import com.xbreeze.xtest.config.ProcessConfig;
import com.xbreeze.xtest.exception.XTestProcessException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;


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
    // Holds the execution tool prefix (e.g., "cmd.exe /c" or "bash -c")
    private String lastToolPrefix = null;
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
        int timeoutSeconds;

        if (clConfig != null) {
            tool = clConfig.getTool();
            toolFlag = clConfig.getToolFlags();
            workingDirectory = clConfig.getWorkingDirectory();
            timeoutSeconds = clConfig.getTimeout();
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
            timeoutSeconds = 0;
        }

        // Store the command text and tool prefix for verification
        lastCommandText = command;
        lastToolPrefix = tool + " " + toolFlag;

        ProcessBuilder builder = new ProcessBuilder(tool, toolFlag, command);
        if (workingDirectory != null && !workingDirectory.isEmpty()) {
            builder.directory(new File(workingDirectory));
        }
        // Redirect stdout+stderr to a temp file so we can use waitFor(timeout) on the main thread
        // without risking a pipe buffer deadlock. The output is read from the file after the process completes.
        File outputFile = null;
        try {
            outputFile = File.createTempFile("xtest-cmd-", ".out");
            outputFile.deleteOnExit();
            builder.redirectErrorStream(true);
            builder.redirectOutput(outputFile);

            process = builder.start();

            // Wait for the process to finish, with optional timeout
            if (timeoutSeconds > 0) {
                boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    commandOutput = readFile(outputFile);
                    throw new XTestProcessException("Command timed out after " + timeoutSeconds + " seconds.\nOutput:\n" + commandOutput);
                }
            } else {
                process.waitFor();
            }

            commandOutput = readFile(outputFile);

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new XTestProcessException("Command failed with exit code: " + exitCode + "\nOutput:\n" + commandOutput);
            }
        } catch (IOException | InterruptedException e) {
            // Read any output produced before the error
            if (outputFile != null && outputFile.exists()) {
                commandOutput = readFile(outputFile);
            }
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
            throw new XTestProcessException("Error running command: " + e.getMessage() + "\nOutput:\n" + commandOutput);
        } finally {
            if (outputFile != null) {
                outputFile.delete();
            }
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
     * Returns the execution tool prefix.
     * For example: "cmd.exe /c" or "bash -c"
     * @return The tool prefix string, or null if no command has been executed.
     */
    public String getLastToolPrefix() {
        return lastToolPrefix;
    }

    /**
     * Reads the contents of a file as a string using the platform default charset.
     */
    private String readFile(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        } catch (IOException e) {
            return "";
        }
    }
}
