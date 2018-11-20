/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.Thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author thepu
 */
public class ThreadPoolManager {
    private static ThreadPoolManager instance;
    
    private ExecutorService executorService;
    
    public void execute(Runnable runnable){
        this.executorService.execute(runnable);
    }
    
    public static ThreadPoolManager getInstance(){
        if(instance==null){
        ThreadPoolManager.instance = new ThreadPoolManager();
        }
        return instance;
    }
    
    private ThreadPoolManager(){
        super();
        this.executorService = Executors.newFixedThreadPool(1);
    }
}
