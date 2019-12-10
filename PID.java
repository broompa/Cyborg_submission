import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.Group;	
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class PID extends Application{
	private Group g ;
	private  static PID reference;
	private  int desired;
	private  Body ball ;
	private  double kp , kd,ki;
	private  long prevTime ;
	private  double errorSum ;
	private  double prevError;
	private  boolean flag ;
	private  int groundLine ;
	private  Vector gravity ; // vector represents gravitational acceleration
	
	public static PID getRef(){return  reference;}

	@Override
	public void start(Stage stage) throws Exception{
		reference = this;
		desired = 400;
		groundLine = 1000; // According to resolution of my pc
		kp = 100f;
		kd = 40f;
		ki = 10f;
		errorSum = 0f;
		prevError = 0f;
		flag = false;
		gravity = new Vector(0,100);
		g =  new Group();
		Line l1 = new Line(0,desired,1920 , desired);
		Line l2 = new Line(0,groundLine,1920,groundLine);
		g.getChildren().add(l1);
		g.getChildren().add(l2);
		ball = new Body(10f, new Circle (0,0,10f));
		g.getChildren().add(ball.getView());
		AnimationTimer anim = new AnimationTimer(){
		
			@Override
			public void handle(long now) {
				PID.getRef().error1();
			}
		};
		anim.start();

		Pane root = new Pane(g);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();




	}

	public static void main(String[] args){
		launch(args);
	}

	public void error(){ // without gravity 
		double error =  (double)(desired - ball.getYPos());
		if (!flag ){
			prevTime = System.nanoTime();
			flag = true ;
			prevError = error;
		}
		//PID
		
		System.out.println("Error  :"+ error);
		double dt = (System.nanoTime() - prevTime)/1000000000f;
		prevTime = System.nanoTime();
		System.out.println("D- term :" +(error - prevError)/dt);
		errorSum += error*dt;
		
		double output = error*kp+kd*(error - prevError)/dt + errorSum*ki;
		
		System.out.println("Output :" +output);
		Vector force = new Vector(0,output);
		prevError =error;
		//
		ball.update(force);
	}
	
	public void error1(){ // with gravity 
		double error =  (double)(desired - ball.getYPos());
		if (!flag ){
			prevTime = System.nanoTime();
			flag = true ;
			prevError = error;
		}
		//PID
		
		System.out.println("Error  :"+ error);
		double dt = (System.nanoTime() - prevTime)/1000000000f;
		prevTime = System.nanoTime();
		System.out.println("D- term :" +(error - prevError)/dt);
		errorSum += error*dt;
		
		double output = error*kp+kd*(error - prevError)/dt + errorSum*ki;
		
		System.out.println("Output :" +output);
		Vector force = new Vector(0,output);
		prevError =error;
		//
		force = Vector.add(gravity,force.scale(1/ball.getMass())).scale(ball.getMass());
		ball.update(force);
	}

}
class Vector {
	protected double magnitude ;
	protected double angle ; //  radians
	protected double xComponent ;
	protected double yComponent ;
	public Vector(double x , double y ){
		xComponent =x ;
		yComponent = y;
	}
	public double getMagnitude(){return magnitude;}
    public double getAngle() {return angle;}
	public double getXComp(){return xComponent;}
	public double getYComp() {return yComponent;}  

	public void setMagnitude(double mag ){this.magnitude = mag;}
	public void setAngle (double ang ){this.angle = ang;}
	public void setXComp (double xComp){this.xComponent = xComp;}
	public void setYComp(double yComp ){this.yComponent = yComp;}
	public void updateMag(){
		// to be written 
	}
	public static Vector add(Vector one , Vector sec ){
		return new Vector(one.xComponent + sec.xComponent , one.yComponent + sec.yComponent);
	}
	public Vector scale(double factor ){
		return new Vector(xComponent*factor , yComponent*factor);
	}
	public String toString(){return "(X,Y): ("+ xComponent+","+yComponent+")";}

	
}

class Body{
	private Group view ;
	private double mass;
	private double xPos ;
	private double yPos ;
	private Vector velocity ;
//	private Vector acceleration;
	private long lastUpdateTime; 
	public Body (double mass ,Node n){
		view = new Group(n);
		view.setTranslateX(500);
		view.setTranslateY(800);
		this.mass =  mass;
		lastUpdateTime = System.nanoTime() ;
		velocity = new Vector (0,0);
		xPos = view.getTranslateX();
		yPos = view.getTranslateY();
		System.out.println("X-POS:"+xPos);
		System.out.println("Y-POS:"+yPos);
//		acceleration = new Vector(0,0);
	}

	public double getMass(){return mass;}
	public double getXPos(){return xPos;}
	public double getYPos() {return yPos;}
	public Node getView(){
		return view; 
	}

	public void update(Vector force){
		System.out.println("X-POS:"+ xPos);
		System.out.println("Y-POS:" + yPos);	
		double dt = (System.nanoTime() - lastUpdateTime)/1000000000f;
		lastUpdateTime = System.nanoTime() ;
		
		velocity = Vector.add(velocity, force.scale(dt/mass));
		xPos = xPos + velocity.getXComp()*dt;
		
		yPos = yPos + velocity.getYComp()*dt;
		System.out.println("velocity :"+ velocity);
		updateCoordiantes();
	}
	public void updateCoordiantes(){
		if (xPos <0){
			xPos = 0;
		}
		else if (xPos> 1500){
			xPos = 1500;
		}
		if (yPos <0){
			yPos =0;
		}
		else if (yPos> 1000){
			yPos = 1000;
		}
		view.setTranslateX(xPos);
		view.setTranslateY(yPos);
		
	}

}

