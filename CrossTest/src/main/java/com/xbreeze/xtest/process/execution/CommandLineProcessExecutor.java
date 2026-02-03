
// CommandLineProcessExecutor executes shell commands on the host OS.
// It supports both Windows and Unix-like systems, and captures the output for verification.
package com.xbreeze.xtest.process.execution;

import com.xbreeze.xtest.config.ProcessConfig;
import com.xbreeze.xtest.exception.XTestProcessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * CommandLineProcessExecutor executes a command line process using the system shell.
 * It supports both Windows (cmd.exe) and Unix-like (bash) environments.
 * The output of the last executed command is stored for later verification.
 */
public class CommandLineProcessExecutor implements ProcessExecutor {
    // Holds the output of the executed command
    private static String commandOutput  = null;
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
     * Captures and stores the output. Throws an exception if the command fails.
     *
     * @param config  The process configuration (not used for command line execution)
     * @param command The command to execute
     * @throws XTestProcessException if the command fails or an error occurs
     */
    @Override
    public void runProcess(ProcessConfig config, String command) throws XTestProcessException {
        // Detect the operating system to choose the correct shell
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder;
        if (os.contains("win")) {
            // On Windows, use cmd.exe to interpret the command string
            builder = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            // On Unix-like systems, use bash to interpret the command string
            builder = new ProcessBuilder("bash", "-c", command);
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
            commandOutput  = output.toString();
            if (exitCode != 0) {
                // Throw an exception if the command failed (non-zero exit code)
                throw new XTestProcessException("Command failed with exit code: " + exitCode + "\nOutput:\n" + commandOutput );
            }
        } catch (IOException | InterruptedException e) {
            // Capture any output produced before the error/exception
            commandOutput  = output.toString();
            // Ensure the process is killed if an error occurs
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
            throw new XTestProcessException("Error running command: " + e.getMessage() + "\nOutput:\n" + commandOutput );
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
}