import hudson.model.*
import hudson.plugins.*
import hudson.tasks.Maven
import hudson.tools.InstallSourceProperty
import jenkins.model.Jenkins
import jenkins.plugins.*
import org.jenkinsci.plugins.maven.*

mavenName = "maven-387"
mavenVersion = "3.8.7"

def mavenPlugin = Jenkins.get().getExtensionList(Maven.DescriptorImpl.class)[0]

def maven3Install = mavenPlugin.installations.find { install -> (install.name == mavenName) }

if (maven3Install == null) {
    def newMavenInstall = new Maven.MavenInstallation(mavenName, null,
            [new InstallSourceProperty([new Maven.MavenInstaller(mavenVersion)])]
    )
    mavenPlugin.installations += newMavenInstall
    mavenPlugin.save()
}

