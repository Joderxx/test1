package consum.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * @author xiejiedun on 2019/1/16
 */

@Mojo(name = "clone")
public class GitCloneMojo extends GitMojo {

    @Parameter(defaultValue = ".idea,target,.git")
    private String exclude = ".idea,target,.git";
    @Parameter(defaultValue = "")
    private String remoteHost = "https://github.com/Joderxx/test1.git";
    private String email = "";
    @Parameter(defaultValue = "")
    private String username = "";

    public static void main(String[] args) {
        File file = new File("/");
        System.out.println(System.getProperty("java.io.tmpdir"));
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        String command,ret;
        File tmp = new File(System.getProperty("java.io.tmpdir"),"git-tmp");
        command = "git clone "+remoteHost+" "+tmp.getAbsolutePath().replace(File.separator,"/");
        exec(command);
        getLog().info("Finish clone from "+remoteHost);

    }
}
