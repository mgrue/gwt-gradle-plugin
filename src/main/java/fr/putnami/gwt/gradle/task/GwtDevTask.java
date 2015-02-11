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
package fr.putnami.gwt.gradle.task;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPluginConvention;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import fr.putnami.gwt.gradle.PwtLibPlugin;
import fr.putnami.gwt.gradle.action.JavaAction;
import fr.putnami.gwt.gradle.extension.CodeServerOption;
import fr.putnami.gwt.gradle.extension.JettyOption;
import fr.putnami.gwt.gradle.extension.JsInteropMode;
import fr.putnami.gwt.gradle.extension.LogLevel;
import fr.putnami.gwt.gradle.extension.MethodNameDisplayMode;
import fr.putnami.gwt.gradle.extension.PutnamiExtension;
import fr.putnami.gwt.gradle.util.JavaCommandBuilder;
import fr.putnami.gwt.gradle.util.ResourceUtils;

public class GwtDevTask extends AbstractJettyTask {

	public static final String NAME = "gwtDev";

	private File workDir;
	private File launcherDir;

	private List<String> modules = Lists.newArrayList();

	private boolean allowMissingSrc = false;
	private String bindAddress = "127.0.0.1";
	private boolean compileTest = false;
	private int compileTestRecompiles = 1000;
	private boolean failOnError = false;
	private boolean precompile = false;
	private int sdmPort = 9876;
	private List<String> src = Lists.newArrayList();
	private boolean enforceStrictResources = false;
	private boolean incremental = true;
	private String sourceLevel = "";
	private LogLevel logLevel = LogLevel.WARN;
	private JsInteropMode jsInteropMode = JsInteropMode.NONE;
	private MethodNameDisplayMode methodNameDisplayMode = MethodNameDisplayMode.NONE;

	public GwtDevTask() {
		setName(NAME);
		setDescription("Run SDM");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	@TaskAction
	public void exec() throws Exception {
		WarPluginConvention warConvention = (WarPluginConvention) getProject().getConvention().getPlugins().get("war");
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);
		CodeServerOption sdmOption = putnami.getDev();
		JettyOption jettyOption = putnami.getJetty();

		try {
			File webOverrideFile = ResourceUtils.copy(
				"/stub.web-dev-override.xml", new File(getProject().getBuildDir(), "putnami/conf/web-dev-override.xml"),
				new ImmutableMap.Builder<String, String>()
					.put("__LAUNCHER_DIR__", sdmOption.getLauncherDir().getAbsolutePath() + "")
					.build());
			ResourceUtils.copy("/stub.jetty-dev-conf.xml", jettyOption.getJettyConf(),
				new ImmutableMap.Builder<String, String>()
					.put("__WEB_OVERRIDE__", webOverrideFile.getAbsolutePath())
					.put("__WAR_FILE__", warConvention.getWebAppDir().getAbsolutePath())
					.build());

		} catch (IOException e) {
			Throwables.propagate(e);
		}

		JavaAction jetty = execJetty(jettyOption);
		execSdm();
		jetty.join();
	}

	private JavaAction execSdm() {
		Configuration sdmConf =
			getProject().getConfigurations().getByName(PwtLibPlugin.CONF_GWT_SDM);

		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);

		JavaCommandBuilder builder = new JavaCommandBuilder();
		builder.configureJavaArgs(putnami.getDev());
		builder.setMainClass("com.google.gwt.dev.codeserver.CodeServer");
		builder.addClassPath(sdmConf.getAsPath());

		builder.addArg("-src", getSrc().get(0));
		builder.addArgIf(isAllowMissingSrc(), "-allowMissingSrc", "-noallowMissingSrc");
		builder.addArg("-bindAddress", getBindAddress());
		builder.addArgIf(isCompileTest(), "-compileTest ", "-nocompileTest");
		if (isCompileTest()) {
			builder.addArg("-compileTestRecompiles", getCompileTestRecompiles());
		}
		builder.addArgIf(isFailOnError(), "-failOnError", "-nofailOnError");
		builder.addArgIf(isPrecompile(), "-precompile", "-noprecompile");
		builder.addArg("-port", getSdmPort());
		builder.addArgIf(isEnforceStrictResources(), "-XenforceStrictResources ", "-XnoenforceStrictResources");
		builder.addArg("-workDir", getWorkDir());
		builder.addArg("-launcherDir", getLauncherDir());
		builder.addArgIf(isPrecompile(), "-incremental", "-noincremental");
		builder.addArg("-sourceLevel", getSourceLevel());
		builder.addArg("-logLevel", getLogLevel());
		builder.addArg("-XmethodNameDisplayMode", getMethodNameDisplayMode());
		builder.addArg("-XjsInteropMode", getJsInteropMode());

		for (String module : getModules()) {
			builder.addArg(module);
		}

		JavaAction sdmAction = new JavaAction(builder.toString());
		sdmAction.execute(this);

		return sdmAction;
	}

	public void configureJetty(Project project, JettyOption options) {
		options.setJettyConf(new File(getProject().getBuildDir(), "putnami/conf/jetty-dev-conf.xml"));
	}

	public void configureCodeServer(final Project project, final PutnamiExtension extention) {
		final CodeServerOption options = extention.getDev();

		final File buildDir = new File(project.getBuildDir(), "putnami");

		options.setLauncherDir(ResourceUtils.ensureDir(buildDir, "conf"));
		options.setWorkDir(ResourceUtils.ensureDir(buildDir, "work"));
		options.src("src/main/java");

		ConventionMapping convention = ((IConventionAware) this).getConventionMapping();

		convention.map("allowMissingSrc", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isAllowMissingSrc();
			}
		});

		convention.map("bindAddress", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return options.getBindAddress();
			}
		});
		convention.map("compileTest", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isCompileTest();
			}
		});
		convention.map("compileTestRecompiles", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return options.getCompileTestRecompiles();
			}
		});
		convention.map("failOnError", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isFailOnError();
			}
		});
		convention.map("precompile", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isPrecompile();
			}
		});
		convention.map("sdmPort", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return options.getPort();
			}
		});
		convention.map("src", new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return options.getSrc();
			}
		});
		convention.map("enforceStrictResources", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isEnforceStrictResources();
			}
		});
		convention.map("workDir", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getWorkDir();
			}
		});
		convention.map("launcherDir", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getLauncherDir();
			}
		});
		convention.map("incremental", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isIncremental();
			}
		});
		convention.map("sourceLevel", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return options.getSourceLevel();
			}
		});
		convention.map("logLevel", new Callable<LogLevel>() {
			@Override
			public LogLevel call() throws Exception {
				return options.getLogLevel();
			}
		});
		convention.map("jsInteropMode", new Callable<JsInteropMode>() {
			@Override
			public JsInteropMode call() throws Exception {
				return options.getJsInteropMode();
			}
		});
		convention.map("methodNameDisplayMode",
			new Callable<MethodNameDisplayMode>() {
				@Override
				public MethodNameDisplayMode call() throws Exception {
					return options.getMethodNameDisplayMode();
				}
			});
		convention.map("modules", new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return extention.getModule();
			}
		});
	}

	@OutputDirectory
	public File getWorkDir() {
		return workDir;
	}

	@OutputDirectory
	public File getLauncherDir() {
		return launcherDir;
	}

	@Input
	public boolean isAllowMissingSrc() {
		return allowMissingSrc;
	}

	@Input
	public String getBindAddress() {
		return bindAddress;
	}

	@Input
	public boolean isCompileTest() {
		return compileTest;
	}

	@Input
	public int getCompileTestRecompiles() {
		return compileTestRecompiles;
	}

	@Input
	public boolean isFailOnError() {
		return failOnError;
	}

	@Input
	public boolean isPrecompile() {
		return precompile;
	}

	@Input
	public int getSdmPort() {
		return sdmPort;
	}

	@Input
	public List<String> getSrc() {
		return src;
	}

	@Input
	public boolean isEnforceStrictResources() {
		return enforceStrictResources;
	}

	@Input
	public boolean isIncremental() {
		return incremental;
	}

	@Input
	public String getSourceLevel() {
		return sourceLevel;
	}

	@Input
	public LogLevel getLogLevel() {
		return logLevel;
	}

	@Input
	public JsInteropMode getJsInteropMode() {
		return jsInteropMode;
	}

	@Input
	public MethodNameDisplayMode getMethodNameDisplayMode() {
		return methodNameDisplayMode;
	}

	@Input
	public List<String> getModules() {
		return modules;
	}

}
