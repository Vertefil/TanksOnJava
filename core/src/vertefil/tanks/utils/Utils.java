package vertefil.tanks.utils;

//Просчитывание физики поворота пушки
public class Utils {
    //Const
    public static final float pi = 90;
    public static final float pi2 = 180;
    public static final float piD2 = 45;
    public static final float pi3D2 = 270;


    //Метод получение угла между двумя точками через арктангенс
    public static float getAngle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.toDegrees((float) Math.atan2(dy, dx));
    }

    //Есть angle, хотим повернуться в angleTo, поварачиваем с скоростью rotationSpeed, времени прошло dt
    public static float makeRotation(float angle, float angleTo, float rotationSpeed, float dt) {
        //Логика поворота пушки

        //Текущий Угол меньше нужного
        if (angle < angleTo) {
            //Если нужный угол < 180, то +, иначе -
            if (Math.abs(angle - angleTo) < 180) {
                angle += rotationSpeed * dt;
            } else {
                angle -= rotationSpeed * dt;
            }
        }
        //Текущий Угол бульше нужного
        if (angle > angleTo) {
            //Если нужный угол < 180, то -, иначе +
            if (Math.abs(angle - angleTo) < 180) {
                angle -= rotationSpeed * dt;
            } else {
                angle += rotationSpeed * dt;
            }
        }
        //Исключаем дрожание пушки, путём округления
        if (Math.abs(angle - angleTo) < 1.5f * rotationSpeed * dt) {
            angle = angle = angleTo;
        }
        return angle;
    }

    //Метод удержания угла от -Pi до Pi
    public static float angleToFromNegPiToPosPi(float ang) {
        while (ang < -180 || ang > 180) {
            if (ang > 180) {
                ang -= 360;
            }
            if (ang < -180) {
                ang += 360;
            }
        }
        return ang;
    }


}
