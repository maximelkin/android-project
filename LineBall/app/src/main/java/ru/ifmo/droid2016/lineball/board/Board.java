package ru.ifmo.droid2016.lineball.board;

import android.graphics.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import static ru.ifmo.droid2016.lineball.board.Who.THIS_USER;

public class Board {
    //maxX/maxY = 9/16
    public static float
            maxX = 576,
            maxY = 1024,
            maxXLocal,
            maxYLocal;

    static float eps = (float) 1e-2;

    private static final float
            dv = (float) 0.5,
            sqrt2 = (float) Math.sqrt(2),
            minSpeed = 4;

    static final RectF borders = new RectF(0, 0, 576, 1024);

    private final Paint thisUserPaint;
    private final Paint rivalPaint;

    private List<Wall> walls1 = new LinkedList<>(), walls2 = new LinkedList<>();
    private Ball ball1, ball2;
    private final boolean isGameMaster;

    public Board(int maxX, int maxY, int color, boolean isGameMaster) {
        maxXLocal = maxX;
        maxYLocal = maxY;
        thisUserPaint = new Paint();
        thisUserPaint.setStrokeWidth(10);
        thisUserPaint.setAntiAlias(true);

        rivalPaint = new Paint();
        rivalPaint.setStrokeWidth(10);
        rivalPaint.setAntiAlias(true);

        thisUserPaint.setColor((color == 0) ? Color.BLUE : Color.RED);
        rivalPaint.setColor((color == 0) ? Color.RED : Color.BLUE);

        ball1 = new Ball(new Point(30, 30), new Point(sqrt2, sqrt2), thisUserPaint);
        ball2 = new Ball(new Point(Board.maxX - 30, Board.maxY - 30), new Point(-sqrt2, -sqrt2), rivalPaint);

        if (isGameMaster) {
            Ball temp = ball1;
            ball1 = ball2;
            ball2 = temp;
        }
        this.isGameMaster = isGameMaster;
    }


    @Nullable
    public Who check() {

        if (ball1.outOfBoard()) {
            Log.e("CHECK:", "we out of board");
            return Who.RIVAL;
        }

        if (ball2.outOfBoard()) {
            Log.e("CHECK:", "rival out of board");
            return THIS_USER;
        }

        for (Wall wall : walls1) {
            boolean collisionWithWall1 = ball1.collisionWithWall(wall, false);
            boolean collisionWithWall2 = ball2.collisionWithWall(wall, false);

            if (collisionWithWall1) {
                Log.e("CHECK:", "blue hits blue wall");
                ball1.rotate(wall);
                ball1.speed -= dv;
                wall.hitPoints--;
            }

            if (collisionWithWall2) {
                Log.e("CHECK:", "red hits blue wall");
                ball2.rotate(wall);
                ball2.speed += dv;
                wall.hitPoints -= 2;
            }

            if (wall.hitPoints <= 0)
                walls1.remove(wall);
        }

        for (Wall wall : walls2) {
            boolean collisionWithWall1 = ball1.collisionWithWall(wall, false);
            boolean collisionWithWall2 = ball2.collisionWithWall(wall, false);

            if (collisionWithWall1) {
                Log.e("CHECK:", "blue hits red wall");
                ball1.rotate(wall);
                ball1.speed += dv;
                wall.hitPoints -= 2;
            }

            if (collisionWithWall2) {
                Log.e("CHECK:", "red hits red wall");
                ball2.rotate(wall);
                ball2.speed -= dv;
                wall.hitPoints--;
            }
            if (wall.hitPoints <= 0)
                walls2.remove(wall);
        }


        if (ball1.speed < minSpeed)
            return Who.RIVAL;

        if (ball2.speed < minSpeed)
            return THIS_USER;

        if (ball1.collisionWithBall(ball2)) {
            if (ball1.speed > ball2.speed + eps) {
                Log.e("CHECK:", "we are faster");
                return THIS_USER;

            } else if (ball2.speed > ball1.speed + eps) {
                Log.e("CHECK:", "we are slower");
                return Who.RIVAL;

            } else {
                Log.e("CHECK:", "need to reverse");
                //swap directions
                Point temp = ball1.direction;
                ball1.direction = ball2.direction;
                ball2.direction = temp;
                ball1.move();
                ball2.move();
                return null;
            }
        }

        ball1.move();
        ball2.move();
        return null;
    }

    public void setWall(String coord, @NonNull Who from) {
        String[] s = coord.split(" ");
        if (s.length != 4) {
            Log.e("board", "invalid wall coord");
            return;
        }
        float[] a = new float[s.length];
        for (int i = 0; i < s.length; i++) {
            a[i] = Float.parseFloat(s[i]);
        }

        Point p1 = new Point(a[0], a[1]),
                p2 = new Point(a[2], a[3]);
        Wall w = new Wall(p1, p2);

        if (ball1.collisionWithWall(w, true) || ball2.collisionWithWall(w, true))
            return;

        Log.e("Board:", "wall added");

        if (from == THIS_USER) {
            walls1.add(w);
        } else {
            walls2.add(w);
        }
    }

    public synchronized void drawBoard(Canvas canvas) {
        ball1.onDraw(canvas, isGameMaster);
        for (Wall wall : walls1) {
            wall.onDraw(canvas, thisUserPaint, isGameMaster);
        }

        ball2.onDraw(canvas, isGameMaster);
        for (Wall wall : walls2) {
            wall.onDraw(canvas, rivalPaint, isGameMaster);
        }
    }
}
