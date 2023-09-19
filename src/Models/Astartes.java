package Models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Astartes представляет космических морских пехотинцев,
 * также известных как Space Marines.
 */
public class Astartes implements Serializable {
    //region Поля
    private int id;
    private String title;
    private Category category;
    //endregion

    //region Конструкторы
    public Astartes(int id, Category category) {
        this.setId(id);
        this.setCategory(category);
        this.setTitle(category.toString());
    }

    public Astartes(int id, String category) throws IllegalArgumentException {
        this.setId(id);
        this.setCategory(category);
    }

    public Astartes(Category category) {
        this(0, category);
    }

    public Astartes(String category) {
        this(0, category);
    }

    //endregion

    //region Методы
    public void setCategory(String category) throws IllegalArgumentException {
        try {
            this.setCategory(Category.valueOf(category.toUpperCase()));
            this.setTitle(category);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Несуществующая категория: " + category);
        }
    }
    public boolean isCategory(Category category) {
        return this.getCategory() == category;
    }

    public boolean isCategory(String category) throws IllegalArgumentException {
        try {
            Category enumCategory = Category.valueOf(category.toUpperCase());
            return this.getCategory() == enumCategory;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Несуществующая категория: " + category);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Astartes astartes = (Astartes) o;
        return getId() == astartes.getId() &&
                Objects.equals(getTitle(), astartes.getTitle()) &&
                getCategory() == astartes.getCategory();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getCategory());
    }

    @Override
    public String toString() {
        return "Astartes{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", category=" + getCategory() +
                '}';
    }

    public static boolean isCategory(Astartes astartes, Category category) {
        return astartes.getCategory() == category;
    }

    public static boolean isCategory(Astartes astartes, String category) throws IllegalArgumentException {
        try {
            Category enumCategory = Category.valueOf(category.toUpperCase());
            return astartes.getCategory() == enumCategory;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Несуществующая категория: " + category);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    //endregion
}