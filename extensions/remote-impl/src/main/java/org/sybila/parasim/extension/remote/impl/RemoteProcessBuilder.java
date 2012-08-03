package org.sybila.parasim.extension.remote.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class RemoteProcessBuilder {

    private final List<String> command = new ArrayList<>();
    private final URI host;

    public RemoteProcessBuilder(URI host) {
        this(host, null);
    }

    public RemoteProcessBuilder(URI host, String username) {
        Validate.notNull(host);
        this.command.add("ssh");
        this.command.add("-t");
        if (username != null) {
            this.command.add("-l");
            this.command.add(username);
        }
        this.command.add(host.toString());
        this.host = host;
    }

    public RemoteProcessBuilder command(String... command) {
        RemoteProcessBuilder copy = copy();
        copy.command.addAll(Arrays.asList(command));
        return copy;
    }

    public Process start() throws IOException {
        return new ProcessBuilder(command).start();
    }

    public Process spawn() throws IOException {
        return spawn(true);
    }

    public Process spawn(boolean shutdownHook) throws IOException {
        final ProcessHolder processHolder = new ProcessHolder();
        new Thread() {
            @Override
            public void run() {
                try {
                    processHolder.process = new ProcessBuilder(command).start();
                } catch(IOException e) {
                    processHolder.exception = e;
                    throw new IllegalArgumentException(e);
                }
            }
        }.start();
        while (processHolder.process == null && processHolder.exception == null) {}
        if (processHolder.exception != null) {
            throw new IOException("Can't spawn a new process.", processHolder.exception);
        } else {
            if (shutdownHook) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        processHolder.process.destroy();
                    }
                });
            }
            return processHolder.process;
        }

    }

    private RemoteProcessBuilder copy() {
        RemoteProcessBuilder copy = new RemoteProcessBuilder(host);
        copy.command.clear();
        copy.command.addAll(this.command);
        return copy;
    }

    private static class ProcessHolder {
        public volatile Process process;
        public volatile Exception exception;
    }

}
