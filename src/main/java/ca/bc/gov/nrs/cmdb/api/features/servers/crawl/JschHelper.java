package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.DefaultSessionFactory;
import com.pastdev.jsch.command.CommandRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JschHelper
{
    private static final Logger log = LoggerFactory.getLogger(JschHelper.class);

    /**
     * Executes a routine command on the target host and returns the Stdout. The full, unmodified contents of Stdout
     * will also be logged at DEBUG. If the command returns Stderr, then the contents will be logged at WARNING.
     *
     * @param sessionFactory
     * @param command
     * @return
     * @throws IOException
     * @throws JSchException
     */
    public static String doExecuteCommand(DefaultSessionFactory sessionFactory, String command) throws IOException, JSchException
    {
        CommandRunner cmd = new CommandRunner(sessionFactory);

        log.trace("Attempting to execute command '{}' on {}", command, sessionFactory.getHostname());
        CommandRunner.ExecuteResult result = cmd.execute(command);
        if (result.getStderr() != null && result.getStderr().length() > 0)
        {
            log.warn("Stderr: {}", result.getStderr().trim());
        }

        log.trace(result.getStdout());

        cmd.close();

        return result.getStdout().trim();
    }
}
