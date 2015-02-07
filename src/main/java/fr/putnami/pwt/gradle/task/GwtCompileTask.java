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
package fr.putnami.pwt.gradle.task;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import fr.putnami.pwt.gradle.PwtLibPlugin;
import fr.putnami.pwt.gradle.action.JavaAction;
import fr.putnami.pwt.gradle.extension.CodeStyle;
import fr.putnami.pwt.gradle.extension.CompilerOptions;
import fr.putnami.pwt.gradle.extension.JsInteropMode;
import fr.putnami.pwt.gradle.extension.LogLevel;
import fr.putnami.pwt.gradle.extension.MethodNameDisplayMode;
import fr.putnami.pwt.gradle.utli.JavaCommandBuilder;

public class GwtCompileTask extends AbstractPwtTask {

	public static final String NAME = "gwtCompile";

	private List<String> modules;

	private File war;
	private File work;
	private File gen;
	private File deploy;
	private File extra;
	private File missingDepsFile;
	private File saveSourceOutput;

	private LogLevel logLevel;
	private boolean compileReport;
	private boolean draftCompile;
	private boolean checkAssertions;
	private boolean incremental;
	// private String namespace;
	private CodeStyle style;
	private int optimize;
	private boolean overlappingSourceWarnings;
	private boolean saveSource;
	private boolean failOnError;
	private boolean validateOnly;
	private String sourceLevel;
	private int localWorkers;
	private MethodNameDisplayMode methodNameDisplayMode;
	private boolean enforceStrictResources;
	private boolean checkCasts;
	private boolean classMetadata;
	private boolean closureCompiler;
	private JsInteropMode jsInteropMode;

	public GwtCompileTask() {
		setName(NAME);
		setDescription("Compile the GWT modules");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	@TaskAction
	public void exec() {
		Configuration sdmConf = getProject().getConfigurations().getByName(PwtLibPlugin.CONF_GWT_SDM);
		Configuration compileConf = getProject().getConfigurations().getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME);

		JavaCommandBuilder builder = new JavaCommandBuilder();
		builder.setMainClass("com.google.gwt.dev.Compiler");

		builder.addClassPath("src/main/java");
		builder.addClassPath("src/main/resources");
		builder.addClassPath(compileConf.getAsPath());
		builder.addClassPath(sdmConf.getAsPath());

		builder.addArg("-workDir", getWork());
		builder.addArg("-gen", getGen());
		builder.addArg("-war", getWar());
		builder.addArg("-deploy", getDeploy());
		builder.addArg("-extra", getExtra());

		builder.addArg("-logLevel", getLogLevel());
		builder.addArg("-localWorkers", getLocalWorkers());
		builder.addArgIf(isFailOnError(), "-failOnError", "-nofailOnError");
		builder.addArg("-sourceLevel", getSourceLevel());
		builder.addArgIf(isDraftCompile(), "-draftCompile", "-nodraftCompile");
		builder.addArg("-optimize", getOptimize());
		builder.addArg("-style", getStyle());
		builder.addArgIf(isCompileReport(), "-compileReport", "-nocompileReport");

		if (isIncremental()) {
			builder.addArg("-incremental");
//			builder.addArg("-incrementalCompileWarnings");
		}


		builder.addArgIf(isCheckAssertions(), "-checkAssertions", "-nocheckAssertions");
		builder.addArgIf(isCheckCasts(), "-XcheckCasts", "-XnocheckCasts");
		builder.addArgIf(isEnforceStrictResources(), "-XenforceStrictResources", "-XnoenforceStrictResources");
		builder.addArgIf(isClassMetadata(), "-XclassMetadata", "-XnoclassMetadata");

		builder.addArgIf(isOverlappingSourceWarnings(), "-overlappingSourceWarnings",
			"-nooverlappingSourceWarnings");
		builder.addArgIf(isSaveSource(), "-saveSource", "-nosaveSource");
		builder.addArg("-XmethodNameDisplayMode", getMethodNameDisplayMode());

		builder.addArgIf(isClosureCompiler(), "-XclosureCompiler", "-XnoclosureCompiler");

		// builder.addArg("-jsInteropMode", getJsInteropMode());

		for (String module : getModules()) {
			builder.addArg(module);
		}

		JavaAction compileAction = new JavaAction(builder.toString());
		compileAction.execute(this);
		compileAction.join();
	}

	public void configure(final Project project, final CompilerOptions options) {

		final File buildDir = new File(project.getBuildDir(), "putnami");

		options.war(new File(buildDir, "war"));
		options.workDir(new File(buildDir, "work"));
		options.gen(new File(buildDir, "gen"));
		options.deploy(new File(buildDir, "deploy"));
		options.extra(new File(buildDir, "extra"));
		options.saveSourceOutput(new File(buildDir, "extra/source"));
		options.missingDepsFile(new File(buildDir, "extra/missingDepsFile"));
		options.localWorkers(Runtime.getRuntime().availableProcessors());

		((IConventionAware) this).getConventionMapping().map("modules", new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return options.getModule();
			}
		});

		((IConventionAware) this).getConventionMapping().map("work", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getWorkDir();
			}
		});
		((IConventionAware) this).getConventionMapping().map("gen", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getGen();
			}
		});
		((IConventionAware) this).getConventionMapping().map("war", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getWar();
			}
		});
		((IConventionAware) this).getConventionMapping().map("deploy", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getDeploy();
			}
		});
		((IConventionAware) this).getConventionMapping().map("extra", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getExtra();
			}
		});
		((IConventionAware) this).getConventionMapping().map("logLevel", new Callable<LogLevel>() {
			@Override
			public LogLevel call() throws Exception {
				return options.getLogLevel();
			}
		});
		((IConventionAware) this).getConventionMapping().map("compileReport", new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception {
				return options.isCompileReport();
			}
		});
		((IConventionAware) this).getConventionMapping().map("draftCompile", new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception {
				return options.isDraftCompile();
			}
		});
		((IConventionAware) this).getConventionMapping().map("checkAssertions", new
			Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return options.isCheckAssertions();
				}
			});
		((IConventionAware) this).getConventionMapping().map("missingDepsFile", new Callable<File>()
		{
			@Override
			public File call() throws Exception {
				return options.getMissingDepsFile();
			}
		});
		((IConventionAware) this).getConventionMapping().map("optimize", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return options.getOptimize();
			}
		});
		((IConventionAware) this).getConventionMapping().map("overlappingSourceWarnings", new
			Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return options.isOverlappingSourceWarnings();
				}
			});
		((IConventionAware) this).getConventionMapping().map("saveSource", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isSaveSource();
			}
		});
		((IConventionAware) this).getConventionMapping().map("style", new Callable<CodeStyle>() {
			@Override
			public CodeStyle call() throws Exception {
				return options.getStyle();
			}
		});
		((IConventionAware) this).getConventionMapping().map("failOnError", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isFailOnError();
			}
		});
		((IConventionAware) this).getConventionMapping().map("validateOnly", new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception {
				return options.isValidateOnly();
			}
		});
		((IConventionAware) this).getConventionMapping().map("sourceLevel", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return options.getSourceLevel();
			}
		});
		((IConventionAware) this).getConventionMapping().map("localWorkers", new Callable<Integer>()
		{
			@Override
			public Integer call() throws Exception {
				return options.getLocalWorkers();
			}
		});
		((IConventionAware) this).getConventionMapping().map("incremental", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isIncremental();
			}
		});
		((IConventionAware) this).getConventionMapping().map("saveSourceOutput", new Callable<File>()
		{
			@Override
			public File call() throws Exception {
				return options.getSaveSourceOutput();
			}
		});
		((IConventionAware) this).getConventionMapping().map("methodNameDisplayMode",
			new Callable<MethodNameDisplayMode>() {
				@Override
				public MethodNameDisplayMode call() throws Exception {
					return options.getMethodNameDisplayMode();
				}
			});
		((IConventionAware) this).getConventionMapping().map("enforceStrictResources", new
			Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return options.isEnforceStrictResources();
				}
			});
		((IConventionAware) this).getConventionMapping().map("checkCasts", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return options.isCheckCasts();
			}
		});
		((IConventionAware) this).getConventionMapping().map("classMetadata", new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception {
				return options.isClassMetadata();
			}
		});
		((IConventionAware) this).getConventionMapping().map("closureCompiler", new
			Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return options.isClosureCompiler();
				}
			});
		((IConventionAware) this).getConventionMapping().map("jsInteropMode", new
			Callable<JsInteropMode>() {
				@Override
				public JsInteropMode call() throws Exception {
					return options.getJsInteropMode();
				}
			});
	}

	@OutputDirectory
	public File getWar() {
		return war;
	}

	@OutputDirectory
	public File getWork() {
		return work;
	}

	@OutputDirectory
	public File getGen() {
		return gen;
	}

	@OutputDirectory
	public File getDeploy() {
		return deploy;
	}

	@OutputDirectory
	public File getExtra() {
		return extra;
	}

	@OutputFile
	public File getMissingDepsFile() {
		return missingDepsFile;
	}

	@OutputDirectory
	public File getSaveSourceOutput() {
		return saveSourceOutput;
	}

	@Input
	public List<String> getModules() {
		return modules;
	}

	@Input
	public LogLevel getLogLevel() {
		return logLevel;
	}

	@Input
	public boolean isCompileReport() {
		return compileReport;
	}

	@Input
	public boolean isDraftCompile() {
		return draftCompile;
	}

	@Input
	public boolean isCheckAssertions() {
		return checkAssertions;
	}

	// @Input
	// public String getNamespace() {
	// return namespace;
	// }

	@Input
	public CodeStyle getStyle() {
		return style;
	}

	@Input
	public int getOptimize() {
		return optimize;
	}

	@Input
	public boolean isOverlappingSourceWarnings() {
		return overlappingSourceWarnings;
	}

	@Input
	public boolean isSaveSource() {
		return saveSource;
	}

	@Input
	public boolean isFailOnError() {
		return failOnError;
	}

	@Input
	public boolean isValidateOnly() {
		return validateOnly;
	}

	@Input
	public String getSourceLevel() {
		return sourceLevel;
	}

	@Input
	public int getLocalWorkers() {
		return localWorkers;
	}

	@Input
	public boolean isIncremental() {
		return incremental;
	}

	@Input
	public MethodNameDisplayMode getMethodNameDisplayMode() {
		return methodNameDisplayMode;
	}

	@Input
	public boolean isEnforceStrictResources() {
		return enforceStrictResources;
	}

	@Input
	public boolean isCheckCasts() {
		return checkCasts;
	}

	@Input
	public boolean isClassMetadata() {
		return classMetadata;
	}

	@Input
	public boolean isClosureCompiler() {
		return closureCompiler;
	}

	@Input
	public JsInteropMode getJsInteropMode() {
		return jsInteropMode;
	}

}
