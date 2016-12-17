package ru.ifmo.droid2016.lineball.Board;

public class Ball {

    Point pos;
    Point dir;
    double v = 5;

    boolean collision(Ball ball){
        Line l1 = new Line(pos, pos.sum(dir));
        Line l2 = new Line(ball.pos, ball.pos.sum(dir));
        //Paths intersect
        if (l1.intersect(l2)){
            Point p = l1.intersectPoint(l2);
            //Check for paths intersect
            if (p.sub(ball.pos).sp(ball.dir) < 0 || p.sub(pos).sp(dir) < 0)
                return false;
            //TODO Check for collision(Use speed)
        }
        //Moving on the same line
        if(l1.eq(l2)){
            //One follow another
            if(dir.sp(ball.dir) > 0){
                //TODO Use speed
            }
            //Moving to each other
            if (pos.sub(ball.pos).sp(ball.dir) < 0)
                return false;
            return true;

        }
        return false;
    }

    boolean collision(Wall wall){
        Line l1 = new Line(pos, pos.sum(dir));
        //Intersect with wall line
        if (l1.intersect(wall.l)){
            Point p = l1.intersectPoint(wall.l);
            if (p.sub(wall.p1).sp(wall.p2.sub(wall.p1)) * p.sub(wall.p2).sp(wall.p1.sub(wall.p2)) < 0 || p.sub(pos).sp(dir) < 0)
                return false;
            return true;
        }
        //Moving on wall line
        if(l1.eq(wall.l)){
            //Away from wall
            if(wall.p2.sub(pos).sp(dir) < 0 && wall.p1.sub(pos).sp(dir) < 0)
                return false;
            return true;
        }
        return false;
    }
}
