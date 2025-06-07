package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class BurgerTest {

    private Burger burger;

    @Mock
    private Bun bun;

    @Mock
    private Ingredient sauceIngredient;

    @Mock
    private Ingredient fillingIngredient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        burger = new Burger();
    }

    @Parameterized.Parameters(name = "Булочка: {0}, Соус: {2}, Начинка: {4}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"black bun", 100f, "hot sauce", 200f, "beef", 300f},
                {"white bun", 50f, "chili sauce", 150f, "chicken", 250f},
                {"red bun", 75f, "sour cream", 125f, "pork", 175f}
        });
    }

    @Parameterized.Parameter(0) public String bunName;
    @Parameterized.Parameter(1) public float bunPrice;
    @Parameterized.Parameter(2) public String sauceName;
    @Parameterized.Parameter(3) public float saucePrice;
    @Parameterized.Parameter(4) public String fillingName;
    @Parameterized.Parameter(5) public float fillingPrice;

    @Test
    public void setBuns_ShouldCorrectlySetBun() {
        burger.setBuns(bun);
        assertSame(bun, burger.bun);
    }

    @Test
    public void addIngredient_ShouldAddToIngredientsList() {
        burger.addIngredient(sauceIngredient);
        assertEquals(1, burger.ingredients.size());
        assertTrue(burger.ingredients.contains(sauceIngredient));
    }

    @Test
    public void removeIngredient_ShouldRemoveFromList() {
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(fillingIngredient);
        burger.removeIngredient(0);
        assertEquals(1, burger.ingredients.size());
        assertFalse(burger.ingredients.contains(sauceIngredient));
    }

    @Test
    public void moveIngredient_ShouldChangePosition() {
        Ingredient thirdIngredient = mock(Ingredient.class);

        burger.addIngredient(sauceIngredient);
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(thirdIngredient);

        burger.moveIngredient(1, 0);
        assertEquals(List.of(fillingIngredient, sauceIngredient, thirdIngredient), burger.ingredients);
    }

    @Test
    public void getPrice_ShouldCalculateCorrectPrice() {
        when(bun.getPrice()).thenReturn(bunPrice);
        when(sauceIngredient.getPrice()).thenReturn(saucePrice);
        when(fillingIngredient.getPrice()).thenReturn(fillingPrice);

        burger.setBuns(bun);
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(fillingIngredient);

        float expectedPrice = bunPrice * 2 + saucePrice + fillingPrice;
        assertEquals(expectedPrice, burger.getPrice(), 0.001f);
    }

    @Test
    public void getReceipt_ShouldFormatCorrectly() {
        // Настройка моков
        when(bun.getName()).thenReturn(bunName);
        when(bun.getPrice()).thenReturn(bunPrice);

        when(sauceIngredient.getType()).thenReturn(IngredientType.SAUCE);
        when(sauceIngredient.getName()).thenReturn(sauceName);
        when(sauceIngredient.getPrice()).thenReturn(saucePrice);

        when(fillingIngredient.getType()).thenReturn(IngredientType.FILLING);
        when(fillingIngredient.getName()).thenReturn(fillingName);
        when(fillingIngredient.getPrice()).thenReturn(fillingPrice);

        // Сборка бургера
        burger.setBuns(bun);
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(fillingIngredient);

        // Проверка
        String receipt = burger.getReceipt();
        assertTrue(receipt.contains(String.format("(==== %s ====)", bunName)));
        assertTrue(receipt.contains(String.format("= sauce %s =", sauceName)));
        assertTrue(receipt.contains(String.format("= filling %s =", fillingName)));
        assertTrue(receipt.contains(String.format("Price: %f", bunPrice * 2 + saucePrice + fillingPrice)));
    }

    @Test
    public void getReceipt_WithNoIngredients_ShouldShowOnlyBuns() {
        when(bun.getName()).thenReturn(bunName);
        when(bun.getPrice()).thenReturn(bunPrice);

        burger.setBuns(bun);

        String receipt = burger.getReceipt();
        assertEquals(2, receipt.split(bunName).length - 1); // Должно быть 2 вхождения названия булочки
        assertTrue(receipt.contains(String.format("Price: %f", bunPrice * 2)));
        assertFalse(receipt.contains("= sauce "));
        assertFalse(receipt.contains("= filling "));
    }
}