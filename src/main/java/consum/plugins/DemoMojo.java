package consum.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author xiejiedun
 *
 * Mojo : name 指定运行所需的前缀名
 */
@Mojo(name = "demo")
public class DemoMojo extends AbstractMojo {

    /**
     * 通过@Parameter注解指定默认值
     * name代表可以传入参数name
     */
    @Parameter(property = "name",defaultValue = "Jack")
    private String name;

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("Hello World, "+name);
    }
}
