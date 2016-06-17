package myfireworks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import static java.lang.Math.random;

public class Fireworks extends Parent {

	private final AnimationTimer timer;
	private final Canvas canvas;
	private final Paint[] colors;
	private final ImageView background;
	private final List<Particle> particles = new ArrayList<Particle>();//所有的烟花
	private int countdown = 40; //倒数计时，为零时将发射一枚烟花

	public Fireworks() {
		//初始化颜色
		colors = new Paint[181];
		initColors();

		//初始化画布和背景
		canvas = new Canvas(1024, 500);
		canvas.setBlendMode(BlendMode.ADD);
		canvas.setEffect(new Reflection(0, 0.4, 0.15, 0));
		background = new ImageView(getClass().getResource("bg1.jpg").toExternalForm());
		getChildren().addAll(background, canvas);
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				GraphicsContext gc = canvas.getGraphicsContext2D();
				//遮盖画布上透明度不足0.2的烟花
				gc.setFill(Color.rgb(0, 0, 0, 0.2));
				gc.fillRect(0, 0, 1024, 500);
				//画所有的烟花
				drawFireworks(gc);
				if (countdown == 0) {
					countdown = (int) (10 + random() * 30);
					//添加一枚新烟花
					addFireworks();
				}
				countdown--;
			}
		};
	}

	/**
	 * 画烟花
	 */
	private void drawFireworks(GraphicsContext gc) {
		Iterator<Particle> iterator = particles.iterator();
		List<Particle> newParticles = new ArrayList<Particle>();

		while (iterator.hasNext()) {
			Particle particle = iterator.next();
			//更新烟花位置
			if (particle.update()) {
				//烟火透明度过低，移除该烟花
				iterator.remove();
				
				//如果烟花可以爆炸
				if (particle.isShouldExplodeChildren()) {
					int size = particle.getSize();
					if (size == 9) {
						explodeCircle(particle, newParticles);
					} else if (size == 8) {
						explodeSmallCircle(particle, newParticles);
					}
				}
			}
			//画烟花
			particle.drow(gc);
		}
		//添加新的烟花
		particles.addAll(newParticles);
	}

	/**
	 * 简单烟花爆炸
	 */
	private void explodeSmallCircle(Particle particle,
			List<Particle> newParticles) {

		final double angle = (Math.PI * 2) / 4;
		for (int i = 4; i > 0; i--) {
			double randomVelocity = 2 + 2 * random();
			double particleAngle = i * angle;
			double velX = Math.cos(particleAngle) * randomVelocity;
			double velY = Math.sin(particleAngle) * randomVelocity;
			
			newParticles.add(new Particle(particle.getPosX(), particle.getPosY(),
					velX, velY, 0, 0,
					particle.getColor(), 4, true, false, false));
		}
	}

	/**
	 * 主烟花爆炸
	 */
	private void explodeCircle(Particle particle, List<Particle> newParticles) {
		//爆炸后的颗粒为20-80之间
		int count = (int) (20 + random() * 60);
		boolean shouldExplodeChildren = random() > 0.8;
		double angle = Math.PI * 2 / count;
		int colorIndex = (int) (colors.length * random());

		for (int i = count; i > 0; i--) {
			double randomVelocity = 4 + 4 * random();
			double particleAngle = i * angle;
			double velX = Math.cos(particleAngle) * randomVelocity;
			double velY = Math.sin(particleAngle) * randomVelocity;
			
			newParticles.add(new Particle(particle.getPosX(), particle.getPosY(),
					velX, velY, 0, 0,
					colors[colorIndex], 8, true, shouldExplodeChildren, true));
		}
	}

	/**
	 * 添加一个主烟花
	 */
	private void addFireworks() {
		double posX = canvas.getWidth() * 0.5;
		double posY = canvas.getHeight() + 10;

		//让烟花颗粒既可以去X的正版轴，也可以去X的负半轴
		double velX = Math.random() * 5 - 2.5;

		//当烟花到这个高度时，便会爆炸
		double targetY = 150 + Math.random() * 100;

		particles.add(new Particle(posX, posY, velX, 0, 0, targetY, colors[0],
				9, false, true, true));

	}

	/**
	 * 初始化颜色
	 */
	private void initColors() {

		//烟花的初始颜色
		colors[0] = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
				CycleMethod.NO_CYCLE, new Stop[] { new Stop(0, Color.WHITE),
						new Stop(0.2, Color.hsb(59, 0.38, 1)),
						new Stop(0.6, Color.hsb(59, 0.38, 1, 0.1)),
						new Stop(1, Color.hsb(59, 0.38, 1, 0)) });
		//烟花爆炸后的颜色
		for (int h = 0; h < 360; h += 2) {
			colors[1 + (h / 2)] = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
					CycleMethod.NO_CYCLE, new Stop[] {
							new Stop(0, Color.WHITE),
							new Stop(0.2, Color.hsb(h, 1, 1)),
							new Stop(0.6, Color.hsb(h, 1, 1, 0.1)),
							new Stop(1, Color.hsb(h, 1, 1, 0)) });
		}
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

}
