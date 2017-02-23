/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;


/**
 *
 * @author user
 */
class RobotControl implements Runnable{
    private final Robot robot; 
    private Thread clickingThread;
    boolean running = false;
    int count = 0;
    UpdateCallBack updateCallBack;
    ArrayList<ICommand> cmdList;
    int cmdIndex = 0;
    RobotControl() {           
        
        try { 
            robot = new Robot();
        } 
        catch (AWTException e){ 
            e.printStackTrace();
            throw new RuntimeException(e);
        }        
        cmdList = new ArrayList<>();
        cmdList.add(new MouseClickCommand(300,300));                        
        cmdList.add(new MouseKeyDownCommand(KeyEvent.VK_Z));           
        cmdList.add(new MouseKeyDownCommand());   
        cmdList.add(new MouseClickCommand(400,400));    
        cmdList.add(new MouseKeyDownCommand());
        cmdList.add(new MouseClickCommand(500,500));    
        cmdList.add(new MouseKeyDownCommand());
    } 
    void executeCommand(){
        ICommand cmd = cmdList.get(cmdIndex);
        cmd.execute();
        cmdIndex++;
        cmdIndex %= cmdList.size();        
    }
    /*public void screenCapture() throws IOException{
        String format = "jpg";
        String fileName = "FullScreenshot." + format;             
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
        ImageIO.write(screenFullImage, format, new File(fileName));             
        System.out.println("A full screenshot saved!");
    }*/
    public void setUpdateCallBack(UpdateCallBack updateCallBack){
        this.updateCallBack = updateCallBack;
    }
    void startControl()  {
        stopControl(); 
         System.out.println("startControl\n");
        running = true;
        startClicking(); 
    }    
    private void startClicking()    { 
        clickingThread = new Thread( ()-> {performClicks();});
        clickingThread.start();
    }       

    void stopControl()  {
        if (running) {
            System.out.println("Stopping");
            running = false;
            try { 
                clickingThread.join(5000);
//                    observingThread.join(5000);
            } 
            catch (InterruptedException e)            { 
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } 
        } 
    } 
    private void performClicks(){
        while( running ){
            executeCommand();            
            count++;
            try  { 
                Thread.sleep(5000);
                System.out.printf("count:" + count+"\n");
                SwingUtilities.invokeLater(this);
            } 
            catch (InterruptedException e) { 
                e.printStackTrace();
                Thread.currentThread().interrupt();
                return; 
            } 
        }
    }
    @Override
    public void run(){
        updateCallBack.update(count);
    }
    
    class MouseClickCommand implements ICommand{
        int x;
        int y;
        MouseClickCommand( int x , int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public void execute(){
            robot.delay(1000);
            robot.mouseMove(x, y);
            robot.mousePress(InputEvent.BUTTON1_MASK);        
            robot.mouseRelease(InputEvent.BUTTON1_MASK);        
        }
    }
    class MouseKeyDownCommand implements ICommand{
        int key;
        MouseKeyDownCommand(){            
            key = KeyEvent.VK_A;
        }
        MouseKeyDownCommand(int key){            
            this.key = key;
        }
        @Override
        public void execute() {            
            robot.delay(1000);
            robot.keyPress(key);
            robot.delay(100);
            robot.keyPress(key);
        }        
    }
    class DelayCommand implements ICommand{
        int delay = 5000;
        DelayCommand(){            
        }
        DelayCommand( int delay ){            
            this.delay = delay;
        }

        @Override
        public void execute() {
            robot.delay(delay);
        }
        
    }
    
    private interface ICommand{
        void execute();
    }
    
     
    interface UpdateCallBack{
        void update(int count);
    }
}