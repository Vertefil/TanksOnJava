package vertefil.tanks.utils;
//Направления
public enum Direction {

    //Задаём направление путём изменения вектора движения
    UP(0,1, 90.0f),DOWN(0,-1, 270.0f),LEFT(-1,0, 180.0f),RIGHT(1,0, 0.0f);

    //Вектора
    private int vx;
    private int vy;
    private float angle;

    //Геттеры
    public int getVx() {
        return vx;
    }

    public int getVy() {
        return vy;
    }

    public float getAngle() {
        return angle;
    }

    //Конструктор
    Direction(int vx, int vy,float angle) {
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
    }
}
