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
 * @goal git-update
 * 
 * @phase process-sources
 */
@Mojo(name = "update")
public class GitUpdateMojo extends GitMojo {

    /**
     * 排除哪些目录或文件
     */
    @Parameter(property = "exclude",defaultValue = ".idea,target,.git")
    private String exclude = ".idea,target,.git";
    /**
     * 更新时记录的消息
     */
    @Parameter(property = "message",defaultValue = "update and commit")
    private String message = "update and commit";
    /**
     * 远程仓库地址
     */
    @Parameter(property = "remoteHost",defaultValue = "")
    private String remoteHost = "git@github.com:Joderxx/test1.git";
    /**
     * 邮箱
     */
    @Parameter(property = "email",defaultValue = "")
    private String email = "";
    /**
     * 用户名
     */
    @Parameter(property = "username",defaultValue = "")
    private String username = "";
    /**
     * 密码
     */
    @Parameter(property = "password",defaultValue = "")
    private String password = "";
    /**
     * 分支
     */
    @Parameter(property = "branch",defaultValue = "master")
    private String branch = "master";
    /**
     * 是否是更新
     */
    @Parameter(property = "update",defaultValue = "true")
    private boolean update = false;
    /**
     * 是否初始化,优先级比update高
     */
    @Parameter(property = "init",defaultValue = "false")
    private boolean init = false;

    public static void main(String[] args) throws MojoExecutionException {
        new GitUpdateMojo().execute();
    }

    public void execute() throws MojoExecutionException {

        String command,s;
        //是否需要初始化
        if (init){
            try {
                command = "git remote remove origin";
                exec(command);
            }catch (Exception e){

            }
            List<String> list = commitFile();
            //是不是git项目
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
        //显示分支
        command = "git branch";
        s = exec(command);
        if (s.indexOf(branch)!=-1){
            command = "git checkout "+branch;
            s = exec(command);
            getLog().info(s);
        }

        getLog().info("当前分支\n"+s);
        //显示用户名
        command = "git config --global user.name";
        if (isEmpty(username)){
            username = exec(command).trim();
        }
        //显示邮箱
        command = "git config --global user.email";
        if (isEmpty(email)){
            email = exec(command).trim();
        }
        getLog().info("Username: "+username);
        getLog().info("Email: "+email);
        command = "git add %s";
        //列出需要上传的文件和目录
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
            //从线上更新本地代码
            if (!init&&update){
                command = "git fetch origin";
                exec(command);
                getLog().info("Fetch finish...");
            }
            //更新到线上
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
