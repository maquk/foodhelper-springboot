package foodhelper.backend.service;

import foodhelper.backend.dto.NutrientDTO;
import foodhelper.backend.dto.RecipeDTO;
import foodhelper.backend.exception.EntityNotFoundException;
import foodhelper.backend.exception.NutrientValueNotFoundException;
import foodhelper.backend.model.ProductConsumed;
import foodhelper.backend.model.Recipe;
import foodhelper.backend.repository.RecipeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, ModelMapper modelMapper) {
        this.recipeRepository = recipeRepository;
        this.modelMapper = modelMapper;
    }

    public List<RecipeDTO> findAll() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeDTO.class)).collect(Collectors.toList());

    }

    public RecipeDTO findByName(String name) {
        Recipe recipe = recipeRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Recipe with name " + name + " not found"));

        return modelMapper.map(recipe, RecipeDTO.class);
    }

    public RecipeDTO findById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe with id " + id + " not found"));
        return modelMapper.map(recipe, RecipeDTO.class);
    }

    public void save(RecipeDTO recipeDTO) {
        Recipe recipe = modelMapper.map(recipeDTO, Recipe.class);
        recipeRepository.save(recipe);
    }

    @Transactional
    public void update(RecipeDTO recipeDTO) {
        Recipe recipe = modelMapper.map(recipeDTO, Recipe.class);
        deleteByName(recipe.getName());
        recipeRepository.save(modelMapper.map(recipeDTO, Recipe.class));
    }

    public void deleteById(Long id) {
        recipeRepository.deleteById(id);
    }

    @Transactional
    public void deleteByName(String name) {
        recipeRepository.deleteByName(name);
    }

    public List<RecipeDTO> findRecipeByNutrientValues(NutrientDTO nutrientDTO){
        List<Recipe> recipes = recipeRepository
                .findRecipeByNutrientValues(nutrientDTO.getFat(), nutrientDTO.getProtein(), nutrientDTO.getCarbohydrates());
        switch (nutrientDTO.getNutrientValue()) {
            case FAT:
                Collections.sort(recipes, (o1, o2) -> {
                    BigDecimal sum1 = BigDecimal.ZERO;
                    for (ProductConsumed product : o1.getProducts()) {
                        sum1 = sum1.add(product.getProduct().getFat()
                                .multiply(product.getGrams().divide(product.getProduct().getGrams(), 2, RoundingMode.HALF_UP)));
                    }
                    BigDecimal sum2 = BigDecimal.ZERO;
                    for (ProductConsumed product : o2.getProducts()) {
                        sum2 = sum2.add(product.getProduct().getFat()
                                .multiply(product.getGrams().divide(product.getProduct().getGrams(), 2, RoundingMode.HALF_UP)));
                    }
                    return sum2.compareTo(sum1);
                });
                break;
            case PROTEIN:
                Collections.sort(recipes, (o1, o2) -> {
                    BigDecimal sum1 = BigDecimal.ZERO;
                    for (ProductConsumed product : o1.getProducts()) {
                        sum1 = sum1.add(product.getProduct().getProtein()
                                .multiply(product.getGrams().divide(product.getProduct().getGrams(), 2, RoundingMode.HALF_UP)));
                    }
                    BigDecimal sum2 = BigDecimal.ZERO;
                    for (ProductConsumed product : o2.getProducts()) {
                        sum2 = sum2.add(product.getProduct().getProtein()
                                .multiply(product.getGrams().divide(product.getProduct().getGrams(), 2, RoundingMode.HALF_UP)));
                    }
                    return sum2.compareTo(sum1);
                });
                break;
            case CARBOHYDRATES:
                Collections.sort(recipes, (o1, o2) -> {
                    BigDecimal sum1 = BigDecimal.ZERO;
                    for (ProductConsumed product : o1.getProducts()) {
                        sum1 = sum1.add(product.getProduct().getCarbohydrates()
                                .multiply(product.getGrams().divide(product.getProduct().getGrams(), 2, RoundingMode.HALF_UP)));
                    }
                    BigDecimal sum2 = BigDecimal.ZERO;
                    for (ProductConsumed product : o2.getProducts()) {
                        sum2 = sum2.add(product.getProduct().getCarbohydrates()
                                .multiply(product.getGrams().divide(product.getProduct().getGrams(), 2, RoundingMode.HALF_UP)));
                    }
                    return sum2.compareTo(sum1);
                });
                break;
            default:
                throw new NutrientValueNotFoundException("Nutrient Value with name " +
                        nutrientDTO.getNutrientValue() +
                        " not found");
        }
        List<RecipeDTO> recipesDTOS = recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeDTO.class)).collect(Collectors.toList());
        return recipesDTOS;
    }
}
