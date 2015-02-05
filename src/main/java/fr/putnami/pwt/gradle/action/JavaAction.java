/**
 * This file is part of pwt.
 *
 * pwt is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * pwt is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with pwt. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package fr.putnami.pwt.gradle.action;

import com.google.common.base.Throwables;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.logging.LogLevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JavaAction implements Action<Task> {

	protected class ProcessLogger extends Thread {
		private final InputStream stream;
		private LogLevel level;
		private boolean quit = false;

		public ProcessLogger(InputStream stream, LogLevel level) {
			this.stream = stream;
			this.level = level;
		}

		@Override
		public void run() {
			BufferedReader input = new BufferedReader(new InputStreamReader(stream));
			try {
				String line = input.readLine();
				while (!quit && line != null) {
					if (level == LogLevel.ERROR) {
						System.err.println(line);
					} else {
						System.out.println(line);
					}
					line = input.readLine();
				}
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}finally{
				try {
					stream.close();
				} catch (IOException e) {
					throw Throwables.propagate(e);
				}
			}
		}

		public void quitLogger() {
			quit = true;
		}
	}

	private final String javaCommand;

	private Process process;

	private ProcessLogger errorLogger;
	private ProcessLogger infoLogger;

	public JavaAction(String javaCommand) {
		super();
		this.javaCommand = javaCommand;
	}

	@Override
	public void execute(Task task) {
		try {
			task.getLogger().info(javaCommand);
			process = Runtime.getRuntime().exec(javaCommand);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					kill();
				}
			});
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		errorLogger = new ProcessLogger(process.getErrorStream(), LogLevel.ERROR);
		errorLogger.start();
		infoLogger = new ProcessLogger(process.getInputStream(), LogLevel.INFO);
		infoLogger.start();
	}

	public void kill() {
		errorLogger.quitLogger();
		infoLogger.quitLogger();
		process.destroy();
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw Throwables.propagate(e);
		}
	}

	public void join() {
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw Throwables.propagate(e);
		}
	}


}
