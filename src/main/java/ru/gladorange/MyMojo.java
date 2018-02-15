package ru.gladorange;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;

@Mojo(name = "generate",defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class MyMojo extends AbstractMojo
{
    @Parameter(property = "project.build.directory")
    private File outputDirectory;

    @Parameter
    private Resource[] resources;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;



    public void execute() throws MojoExecutionException
    {
        extendClasspath();


        File f = outputDirectory;

        for (Resource resource : resources) {
            File touch = new File( f, resource.getFilename().toLowerCase().endsWith(".yaml")?
                    resource.getFilename() :
                    resource.getFilename() +".yaml" );

            try (FileWriter w =  new FileWriter( touch ))
            {
                w.write( getSwaggerDefinition(resource.getPackages().split(",")) );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Error creating file " + touch, e );
            }

        }
    }

    private void extendClasspath() throws MojoExecutionException {
        try {
            Set<URL> urls = new HashSet<>();
            List<String> elements = new ArrayList<>(project.getCompileClasspathElements());
            elements.addAll(project.getRuntimeClasspathElements());
            //getRuntimeClasspathElements()
            //getCompileClasspathElements()
            //getSystemClasspathElements()
            for (String element : elements) {
                urls.add(new File(element).toURI().toURL());
            }

            ClassLoader contextClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());

            Thread.currentThread().setContextClassLoader(contextClassLoader);
        } catch (Exception e) {
            throw new MojoExecutionException("Enabke to extend classpath",e);
        }
    }

    private static String getSwaggerDefinition(String[] packages) throws OpenApiConfigurationException {
        Set<String> p = new HashSet<>(Arrays.asList(packages));
        SwaggerConfiguration oasConfig = new SwaggerConfiguration().prettyPrint(true).resourcePackages((p));

        OpenApiContext ctx = new JaxrsOpenApiContextBuilder().openApiConfiguration(oasConfig).ctxId(p.toString()).buildContext(true);
        return Yaml.pretty(ctx.read());
    }
}
