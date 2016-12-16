package ru.ifmo.droid2016.lineball.Board;

public class Ball {

    Point pos;
    Point dir;
    double v;

    boolean collision(Ball ball){
        Line l1 = new Line(pos, pos.sum(dir));
        Line l2 = new Line(ball.pos, ball.pos.sum(dir));
        //Траектории пересекаются
        if (l1.intersect(l2)){
            Point p = l1.intersectPoint(l2);
            //Проверка на пересечение путей
            if (p.sub(ball.pos).sp(ball.dir) < 0 || p.sub(pos).sp(dir) < 0)
                return false;
            //TODO Проверка на столкновения(учесть скорость)
        }
        //Движутся вдоль одной прямой
        if(l1.eq(l2)){
            //Один летит за другим
            if(dir.sp(ball.dir) > 0){
                //TODO Учесть скорость
            }
            //Летят навстречу друг другу
            if (pos.sub(ball.pos).sp(ball.dir) < 0)
                return false;
            return true;

        }
        return false;
    }

    boolean collision(Wall wall){
        Line l1 = new Line(pos, pos.sum(dir));
        //Траектория пересекается с линией стены
        if (l1.intersect(wall.l)){
            Point p = l1.intersectPoint(wall.l);
            if (p.sub(wall.p1).sp(wall.p2.sub(wall.p1)) * p.sub(wall.p2).sp(wall.p1.sub(wall.p2)) < 0 || p.sub(pos).sp(dir) < 0)
                return false;
            return true;
        }
        //Движется по линии стены
        if(l1.eq(wall.l)){
            //Летит от стены
            if(wall.p2.sub(pos).sp(dir) < 0 && wall.p1.sub(pos).sp(dir) < 0)
                return false;
            return true;
        }
        return false;
    }
}
