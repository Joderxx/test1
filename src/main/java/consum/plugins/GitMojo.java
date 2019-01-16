package consum.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.*;

/**
 * @author xiejiedun on 2019/1/16
 */
public abstract class GitMojo extends AbstractMojo {


    public String readInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
        String line;
        while((line=bufferedReader.readLine())!=null)
        {
            sb.append(line+"\n");
        }
        return sb.toString();
    }

    public List<String> execBatch(String... command)throws MojoExecutionException {
        List<String> list = new ArrayList<String>();
        for (String c:command){
            list.add(exec(c));
        }
        return list;
    }

    public String exec(String command)throws MojoExecutionException{
        return exec(command,new File(currenntPath()));
    }


    public String exec(String command,String... input)throws MojoExecutionException{
        return exec(command,new File(currenntPath()),input);
    }

    public String exec(String command,File file,String... input)throws MojoExecutionException{
        Process exec = null;
        try {
            exec = Runtime.getRuntime().exec(command,null,file);
            String text = readInputStream(exec.getInputStream());
            exec.waitFor();
            if (input!=null){
                for (String e:input){
                    exec.getOutputStream().write((e+"\n").getBytes());
                }
            }
            if (exec.exitValue()==0){
                return text;
            }else {
                throw new MojoExecutionException(readInputStream(exec.getErrorStream()));
            }

        } catch (IOException e) {
            throw new MojoExecutionException("IOException",e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("InterruptedException",e);
        }finally {
            if (exec!=null){
                exec.destroy();
            }
        }
    }

    public String currenntPath(){
        File file = new File("");
        return file.getAbsolutePath();
    }

    public List<String> commitFile(){
        File dir = new File(".");
        return Arrays.asList(dir.list());
    }

    public List<String> listFiles(String...exclude){
        String path = currenntPath();
        List<String> list = new ArrayList<String>();
        listFiles(new File(path),list,new HashSet<String>(Arrays.asList(exclude)));
        return list;
    }

    private void listFiles(File file, List<String> list, Set<String> exclude){
        if (exclude.contains(file.getName())){
            return;
        }
        if (file.isDirectory()){
            for (File f: file.listFiles()){
                listFiles(f,list,exclude);
            }
        }else {
            list.add(file.getAbsolutePath());
        }
    }

    public boolean isEmpty(String s){
        return s==null||s.trim().length()==0;
    }
}
