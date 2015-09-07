package com.argo.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

import org.gradle.tooling.BuildException

class AptPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.configurations.create 'apt'
        project.extensions.create 'apt', AptPluginExtension

        project.afterEvaluate {
            if (isJavaProject(project)) {
                applyToJavaProject(project)
            } else {
                throw new BuildException('The project isn\'t a java (argo) project', null)
            }
        }
    }

    def applyToJavaProject(project) {
        File aptOutputDir = getAptOutputDir(project)
        project.task('addAptCompilerArgs') << {
            project.compileJava.options.compilerArgs.addAll '-processorpath',
                    project.configurations.apt.asPath, '-s', aptOutputDir.path

            project.compileJava.source = project.compileJava.source.filter {
                !it.path.startsWith(aptOutputDir.path)
            }

            project.compileJava.doFirst {
                logger.info "Generating sources using the annotation processing tool:"
                logger.info "  Output directory: ${aptOutputDir}"

                aptOutputDir.mkdirs()
            }
        }
        project.tasks.getByName('compileJava').dependsOn 'addAptCompilerArgs'
    }

    def isJavaProject(project) {
        project.plugins.hasPlugin('java')
    }

    def sourceSetName(variant) {
        variant.dirName.split('/').last()
    }

    def getAptOutputDir(project) {
        def aptOutputDirName = project.apt.outputDirName
        if (!aptOutputDirName) {
            aptOutputDirName = 'build/source/apt'
        }
        project.file aptOutputDirName
    }
}
