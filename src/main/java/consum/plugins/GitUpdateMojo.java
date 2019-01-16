package consum.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.*;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * 
 * @phase process-sources
 */
@Mojo(name = "git-update")
public class GitUpdateMojo extends GitMojo {

    @Parameter(defaultValue = ".idea,target,.git")
    private String exclude = ".idea,target,.git";
    @Parameter(defaultValue = "update and commit")
    private String message = "update and commit";
    @Parameter(defaultValue = "")
    private String remoteHost = "git@github.com:Joderxx/test1.git";
    @Parameter(defaultValue = "")
    private String email = "";
    @Parameter(defaultValue = "")
    private String username = "";
    @Parameter(defaultValue = "")
    private String password = "";
    @Parameter(defaultValue = "master")
    private String branch = "master";
    @Parameter(defaultValue = "true")
    private boolean update = false;
    @Parameter(defaultValue = "true")
    private boolean init = true;

    public static void main(String[] args) throws MojoExecutionException {
        new GitUpdateMojo().execute();
    }

    public void execute() throws MojoExecutionException {

        String command,s;
        if (init){
            List<String> list = commitFile();
            if (!list.contains(".git")){
                command = "git init";
                getLog().info(exec(command));
            }
            if (!isEmpty(username)){
                command = "git config user.name '"+username+"'";
                exec(command);
                getLog().info("init username: "+username);
            }
            if (!isEmpty(email)){
                command = "git config user.email '"+email+"'";
                exec(command);
                getLog().info("init username: "+email);
            }
        }
        command = "git branch";
        s = exec(command);
        if (s.indexOf(branch)!=-1){
            command = "git checkout "+branch;
            s = exec(command);
            getLog().info(s);
        }

        getLog().info("当前分支\n"+s);
        command = "git config --global user.name";
        if (isEmpty(username)){
            username = exec(command).trim();
        }
        command = "git config --global user.email";
        if (isEmpty(email)){
            email = exec(command).trim();
        }
        getLog().info("Username: "+username);
        getLog().info("Email: "+email);
        command = "git add %s";
        List<String> ls = listFiles(exclude.split(","));
        for (String e:ls){
            s = String.format(command, e.replace(File.separator,"/"));
            exec(s);
            getLog().info(s);
        }
        command = String.format("git commit -m \"%s\"",message);
        s = exec(command);
        getLog().info("commit:"+command);
        getLog().info("commit:"+s);

        if (!isEmpty(remoteHost)){
            try {
                command = "git remote add origin "+remoteHost;
                this.getLog().info(exec(command));
            }catch (MojoExecutionException e){
                getLog().info("已经连接到远程地址");
            }
            if (!init&&update){
                command = "git fetch origin";
                exec(command);
                getLog().info("Fetch finish...");
            }
            command = "git push -u origin "+branch ;
            if (!isEmpty(username)&&!isEmpty(password)){
                exec(command,username,password);
            }else {
                exec(command);
            }
            getLog().info("Push finish...");

        }
    }

}
