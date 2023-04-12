package order;

import java.util.ArrayList;
import java.util.List;

public class CreateOrder {
    private List<String> ingredients= new ArrayList<>(0);

    public CreateOrder(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public CreateOrder() {
    }
    public void addIngredients(String ingredient){
        ingredients.add(ingredient);
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}
