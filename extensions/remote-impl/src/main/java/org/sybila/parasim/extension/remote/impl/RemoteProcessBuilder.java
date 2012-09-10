/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.remote.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private final String username;

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
        this.username = username;
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
                    final Process nativeProcess = new ProcessBuilder(command).start();
                    // FIXME: Linux dependent
                    processHolder.process = new Process() {

                        @Override
                        public OutputStream getOutputStream() {
                            return nativeProcess.getOutputStream();
                        }

                        @Override
                        public InputStream getInputStream() {
                            return nativeProcess.getInputStream();
                        }

                        @Override
                        public InputStream getErrorStream() {
                            return nativeProcess.getErrorStream();
                        }

                        @Override
                        public int waitFor() throws InterruptedException {
                            return nativeProcess.waitFor();
                        }

                        @Override
                        public int exitValue() {
                            return nativeProcess.exitValue();
                        }

                        @Override
                        public void destroy() {
                            List<String> kill = new ArrayList<>();
                            kill.add("ssh");
                            kill.add("-t");
                            if (username != null) {
                                kill.add("-l");
                                kill.add(username);
                            }
                            kill.add(host.toString());
                            kill.add("ps -aux | grep \""+commandToString(command).replace(commandToString(kill) + " ", "") +"\" | awk '{print \"kill \"$2}' | bash");
                            try {
                                new ProcessBuilder(kill).start();
                            } catch (IOException ignored) {
                            }
                            nativeProcess.destroy();
                        }
                    };
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

    private static String commandToString(List<String> command) {
        return command.toString().replace("[", "").replace("]", "").replace(", ", " ");
    }

    private static class ProcessHolder {
        public volatile Process process;
        public volatile Exception exception;
    }

}
