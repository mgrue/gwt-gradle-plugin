package de.esoco.gwt.gradle.task

import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.LogLevel
import org.gradle.internal.extensibility.DefaultConvention
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

import java.nio.file.Files
import java.util.function.Function

import static org.gradle.api.logging.LogLevel.DEBUG
import static org.gradle.api.logging.LogLevel.INFO
import static org.gradle.api.logging.LogLevel.QUIET
import static org.gradle.api.logging.LogLevel.WARN

/**
 * Created by James X. Nelson (James@WeTheInter.net) on 3/27/19 @ 11:30 AM.
 */
class GwtCompileTaskTest extends Specification {

    LogLevel LOG_LEVEL = INFO

    private File rootDir
    File getRootDir() {
        this.@rootDir ?: (this.@rootDir = newTmpDir())
    }

    File newTmpDir() {
        Files.createTempDirectory(getClass().simpleName).toFile()
    }

    void setup() {

    }

    def "Gwtc should rerun when files in a transitive dependency change"() {
        given:
        setupMultiProject()
        expect:
        runSucceed('gwtCompile')
        runSucceed('jar')
        outputHasText("hello")
        outputHasNoText("goodbye")
        modifyProducer('goodbye')
        runSucceed('gwtCompile')
        runSucceed('jar')
        outputHasText("goodbye")
        outputHasNoText("hello")
    }



    File folder(String ... paths) {
        File f = getRootDir()
        for (String path : paths) {
            f = new File(f, path)
            // This assert in the loop will help diagnose any directory permissions errors you may have.
            assert f.directory || f.mkdirs() : "Unable to create directory $f"
        }
        f.mkdirs()
        return f
    }

    File file(String ... paths) {
        File f = folderFactory(paths.init()).apply(paths.last())
        !f.parentFile.exists() && f.mkdirs()
        !f.exists() && f.createNewFile()
        assert f.file : "Not a file: $f"
        return f
    }

    Function<String, File> folderFactory(String ... paths) {
        File f = folder(paths)
        return { String rest -> new File(f, rest) }
    }

    BuildResult runSucceed(
            LogLevel logLevel = LOG_LEVEL,
            File projectDir = getRootDir(),
            Boolean debug = "false" != System.getProperty("debug") && "false" != System.getenv('DEBUG'),
            String ... tasksOrFlags
    ) {
        List<String> args = [toFlag(logLevel), '--full-stacktrace', '--console=plain', *tasksOrFlags]

        GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withDebug(debug)
                .withArguments(args)
                .with({
                    // Forward build output to std out when -Ddebug=true is set.
                    debug && it.forwardStdOutput(new PrintWriter(System.out))
                    debug && it.forwardStdError(new PrintWriter(System.err))
                    return it
                })
                .build()
    }

    String toFlag(LogLevel logLevel) {
        switch (logLevel) {
            case DEBUG:
                return '-d'
            case INFO:
                return '-i'
            case WARN:
                return '-w'
            case QUIET:
                return '-q'
            default:
                throw new UnsupportedOperationException("$logLevel not supported")
        }
    }

    void setupMultiProject() {
        file('settings.gradle') << """
include(':producer') // producer will be compiling and editing dependencies
include(':consumer') // consumer exists just to depend on producer
include(':compiler') // compiler will include code that depends on producer through consumer project
"""
        file('producer', 'build.gradle') << """
plugins { id 'java' }
repositories { jcenter() }
"""
        file('consumer', 'build.gradle') << """
apply plugin: 'java'
dependencies {
    compile project(':producer')
}
"""
        file('compiler', 'build.gradle') << """
plugins { id 'de.esoco.gwt' }
repositories { jcenter() }
dependencies {
    compile project(':consumer')
}
gwt {
    module 'com.Bar'
}
"""
        file('compiler', 'src', 'main', 'resources', 'com', 'Bar.gwt.xml') << """
<module rename-to="testComp">
    <source path="foo" />
    <source path="bar" />
    <entry-point class="com.bar.SubType" />
</module>
"""
        File superType = getSuperTypeJavaFile()
        superType << """
package com.foo;
public class SuperType {
  public String getMessage() {
    return "hello";
  }
}
"""
        file('compiler', 'src', 'main', 'java', 'com', 'bar', 'SubType.java') << """
package com.bar;
import com.foo.SuperType;
import com.google.gwt.core.client.EntryPoint;
public class SubType extends SuperType implements EntryPoint {
    public void onModuleLoad() {
        log(getMessage());
    }
    private native void log(Object msg) /*-{
        \$wnd.console.log(msg);
    }-*/;
}
"""

        println "Done setting up test gwt compile in file://$rootDir"
    }

    boolean modifyProducer(String newVal) {
        File f = getSuperTypeJavaFile();
        f.text = f.text.replaceFirst("hello", newVal)
        return true
    }

    File getSuperTypeJavaFile() {
        return file('producer', 'src', 'main', 'java', 'com', 'foo', 'SuperType.java')
    }

    File findOutputFile(String endsWith = ".cache.js") {
        File folder = folder('compiler', 'build', 'gwt', 'out', 'testComp')
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith(endsWith)) {
                return f
            }
        }
        throw new IllegalArgumentException("No file in " + folder + " ends with $endsWith")
    }

    void outputHasText(String expected) {
        File output = findOutputFile()
        assert output.text.contains(expected)
    }

    void outputHasNoText(String expected) {
        File output = findOutputFile()
        assert !output.text.contains(expected)
    }
}
