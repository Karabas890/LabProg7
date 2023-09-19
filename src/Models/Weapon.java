package Models;

import java.io.Serializable;
import java.util.Objects;

public class Weapon implements Serializable {
    private int id;
    private String title;
    private WeaponType type;

    public Weapon(int id, String title, WeaponType type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public Weapon(int id, String title) throws IllegalArgumentException {
        this.id = id;
        setWeaponType(title);
        this.title = title;
    }

    public Weapon(int id, WeaponType type) {
        this.id = id;
        this.type = type;
        this.title = type.toString();
    }

    public Weapon(String title) {
        this(0, title);
    }

    public Weapon(WeaponType type) {
        this(0, type);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public WeaponType getType() {
        return type;
    }

    public void setWeaponType(String title) throws IllegalArgumentException {
        try {
            this.type = WeaponType.valueOf(title.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Несуществующий тип оружия: " + title);
        }
    }

    public boolean isWeaponType(WeaponType type) {
        return this.type == type;
    }

    public boolean isWeaponType(String title) throws IllegalArgumentException {
        try {
            WeaponType enumType = WeaponType.valueOf(title.toUpperCase());
            return this.type == enumType;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Несуществующий тип оружия: " + title);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weapon weapon = (Weapon) o;
        return id == weapon.id &&
                Objects.equals(title, weapon.title) &&
                type == weapon.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, type);
    }

    @Override
    public String toString() {
        return "Weapon{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                '}';
    }
}
