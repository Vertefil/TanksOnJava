package vertefil.tanks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.awt.*;

//Оружие
public class Weapon {

    //Задаём поля
    private TextureRegion texture; //texture
    private float firePeriod; //Период выстрела
    private int damage;         //Урон
    private float radius;  //Время жизни снаряда
    private float projectileSpeed; //Скорость
    private float projectileLifetime; //Время жизни

    //Getters
    public TextureRegion getTexture() {
        return texture;
    }

    public float getFirePeriod() {
        return firePeriod;
    }

    public int getDamage() {
        return damage;
    }

    public float getRadius() {
        return radius;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public float getProjectileLifetime() {
        return projectileLifetime;
    }

    public Weapon(TextureAtlas atlas) {
        this.texture = atlas.findRegion("simpleWeapon");
        this.firePeriod = 0.2f;
        this.damage = 1;
        this.radius = 300.0f;
        this.projectileSpeed = 320.0f;
        this.projectileLifetime = this.radius / this.projectileSpeed;
    }


}
