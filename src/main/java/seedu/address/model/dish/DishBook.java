package seedu.address.model.dish;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.UniqueItemList;

public class DishBook implements ReadOnlyDishBook {
    private final UniqueItemList<Dish> dishes;
    {
        dishes = new UniqueItemList<Dish>();
    }

    public DishBook() {}

    public DishBook(ReadOnlyDishBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes.setItems(dishes);
    }

    public void resetData(ReadOnlyDishBook newData) {
        requireNonNull(newData);

        setDishes(newData.getDishList());
    }

    public boolean hasDish(Dish dish) {
        requireNonNull(dish);

        return dishes.contains(dish);
    }

    public void addDish(Dish o) {
        dishes.add(o);
    }

    public void setDish(Dish target, Dish editedDish) {
        requireNonNull(editedDish);
        dishes.setItem(target, editedDish);
    }

    public void removeDish(Dish key) {
        dishes.remove(key);
    }

    @Override
    public String toString() {

        return dishes.asUnmodifiableObservableList().size() + " Dishs";
        // TODO: refine later
    }

    @Override
    public ObservableList<Dish> getDishList() {
        return dishes.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DishBook // instanceof handles nulls
                && dishes.equals(((DishBook) other).dishes));
    }

}
