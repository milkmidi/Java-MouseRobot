package robot;
import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
/**
 *
 * @author milkmidi
 */
public class MouseRobot {
    public static void main(String[] args) throws AWTException {        
        MouseRobot entry = new MouseRobot();
    }
    public static void log(String value){
        System.out.printf( value + "\n");    
    }
    
    RobotControl control;
    boolean isStart = false;
    JButton startButton;
    JButton stopButton;
    JLabel label;
    MouseRobot() throws AWTException{
        log("Entry Constructor");    
        createAndShowGUI();
        control = new RobotControl();      
//        control.setUpdateCallBack(this);
//        robot.
    }    
    public void update(int count){
        label.setText(count+"");
    }
    private void startRobot(){
        log("startRobot");
        isStart = true;        
        updateLayoutState();        
        control.startControl();              
    }
    private void stopRobot(){
        log("stopRobot");
        isStart = false;
        control.stopControl();        
        updateLayoutState();
    }
    private void updateLayoutState(){
        startButton.setEnabled(!isStart);
        stopButton.setEnabled(isStart);
    }
    /**/
    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
//        frame.setSize(360, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(boxlayout);
        panel.setBorder(new EmptyBorder(new Insets(20, 50, 20, 50)));
        

        //Add the ubiquitous "Hello World" label.
        label = new JLabel("Hello World");
        label.setText("0");
        panel.add(label);

        startButton = new JButton("Start");
        startButton.addActionListener((ActionEvent event)->{            
            startRobot();            
        });
        panel.add(startButton);
        
        
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener((ActionEvent event)->{            
            stopRobot();            
        });
        panel.add(stopButton);

        //Display the window.
        frame.add( panel );
        frame.pack();
        frame.setVisible(true);
    }
    
    static interface UpdateCallBack{
        void update(int count);
    }
    
    class RobotControl implements Runnable{
        private final Robot robot; 
        private Thread clickingThread;
        boolean running = false;
        int count = 0;
        UpdateCallBack updateCallBack;
        RobotControl() {             
            try { 
                robot = new Robot();
            } 
            catch (AWTException e){ 
                e.printStackTrace();
                throw new RuntimeException(e);
            }        
            
        } 
        public void setUpdateCallBack(UpdateCallBack updateCallBack){
            this.updateCallBack = updateCallBack;
        }
        void startControl()  {
            stopControl(); 
            log("startControl");
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
                robot.delay(3000);
                robot.mouseMove(200, 200);        
                leftClick();
                keyDown(); 
                count++;
                try  { 
                    Thread.sleep(5000);
                    log("count:" + count);
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
            
        }
        private void keyDown(){
            robot.delay(200);
            robot.keyPress(KeyEvent.VK_A);
        }
        private void leftClick()  {
            //log("leftClick");
            robot.delay(200);
            robot.mousePress(InputEvent.BUTTON1_MASK);        
            robot.mouseRelease(InputEvent.BUTTON1_MASK);        
    //        robot.delay(200);
        }
    }
}
