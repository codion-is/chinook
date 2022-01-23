import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
/**
 * Plugin for building JasperReports reports.
 */
class BuildReportsPlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {
        def config = project.extensions.create('buildReports', BuildReportsExtension)
        def buildReports = project.task('buildReports') {
            group = 'build'
            inputs.dir config.sourceDir
            outputs.dir config.targetDir
            doLast {
                ant.taskdef(name: 'jrc', classname: 'net.sf.jasperreports.ant.JRAntCompileTask', classpath: config.taskClasspath.get())
                config.targetDir.get().mkdirs()
                ant.jrc(srcdir: config.sourceDir.get(), destdir: config.targetDir.get()) {
                    classpath(path: config.buildClasspath.get())
                    include(name: '**/*.jrxml')
                }
            }
        }
        project.configure(project) {
            project.afterEvaluate {
                buildReports.mustRunAfter('classes')
                project.getTasks().findByName('jar').dependsOn(buildReports)
                project.getTasks().findByName('compileTestJava').dependsOn(buildReports)
            }
        }
    }
}

abstract class BuildReportsExtension {
    /**
     * @return The classpath on which to find the JRAntCompileTask.
     */
    abstract Property<String> getTaskClasspath()
    /**
     * @return The classpath to use when compiling the reports
     */
    abstract Property<FileCollection> getBuildClasspath()
    /**
     * @return The reports source dir
     */
    abstract Property<File> getSourceDir()
    /**
     * @return The target dir for the compiled reports
     */
    abstract Property<File> getTargetDir()
}