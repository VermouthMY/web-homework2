package myfireworks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import static java.lang.Math.*;

public class Particle {

	//这个是重力加速度
	//这里只是下降的时候会用到，上升的时候没有用
	private static final double GRAVITY = 0.06;

	private double alpha;//透明度
	private double fade;//淡化

	private double posX;//当前X坐标
	private double posY;//当前Y坐标
	private double velX;//X轴方向的速率
	private double velY;//Y轴方向的速率
	private final double targetX;//目标位置的X坐标
	private final double targetY;//目标位置的Y坐标
	private final Paint color;//粒子的颜色
	private final int size;//粒子的大小
	private final boolean usePhysics;//是否加入物理现象
	private final boolean shouldExplodeChildren;//是否会爆炸
	private final boolean hasTail;//是否有运动轨迹
	private double lastPosX;//上一次的X坐标
	private double lastPosY;//上一次的Y坐标
	
	public double getTargetX() {
		return targetX;
	}

	public boolean isShouldExplodeChildren() {
		return shouldExplodeChildren;
	}

	public int getSize() {
		return size;
	}

	public Paint getColor() {
		return color;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public Particle(double posX, double posY, double velX, double velY,
			double targetX, double targetY, Paint color, int size,
			boolean usePhysics, boolean shouldExplodeChildren, boolean hasTail) {
		this.posX = posX;
		this.posY = posY;
		this.velX = velX;
		this.velY = velY;
		this.targetX = targetX;
		this.targetY = targetY;
		this.color = color;
		this.size = size;
		this.usePhysics = usePhysics;
		this.shouldExplodeChildren = shouldExplodeChildren;
		this.hasTail = hasTail;

		this.alpha = 1;
		this.fade = random() * 0.1;
	}

	public boolean update() {
		lastPosX = posX;
		lastPosY = posY;
		
		if (this.usePhysics) {
			velY += GRAVITY;
			posY += velY;
			//10帧之后颗粒消失
			alpha -= fade;
		} else {
			double distance = targetY - posY;
			//每一帧上升目标点和当前点垂直距离的0.03
			posY += distance * (0.02 * random() + 0.03);
			//根据某个计算规则得出透明度
			alpha = min(distance * distance * 0.00005, 1);
		}
		posX += velX;
		//当透明度小于0.05时，将会移除这个点
		//根据我的计算规则，将目标点和当前点的垂直距离为30左右的时候，就会消失
		return alpha < 0.005;
	}

	public void drow(GraphicsContext gc) {
		final double x = Math.round(posX);
		final double y = Math.round(posY);
		final double xVel = (x - lastPosX) * -5;
		final double yVel = (y - lastPosY) * -5;

		gc.setGlobalAlpha(Math.random() * this.alpha);
		gc.setFill(color);
		gc.fillOval(x - size, y - size, size + size, size + size);
		
		if (hasTail) {
			gc.setFill(Color.rgb(255, 255, 255, 0.2));
			gc.fillPolygon(new double[] { posX + 1.5, posX + xVel,posX - 1.5 }, new double[] { posY, posY + yVel, posY },3);
		}
	}

}
