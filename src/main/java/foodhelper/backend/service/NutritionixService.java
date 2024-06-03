package foodhelper.backend.service;

import foodhelper.backend.dto.NutritionixRequest;
import foodhelper.backend.dto.NutritionixResponse;
import foodhelper.backend.dto.NutritionixWrapper;
import foodhelper.backend.model.Product;
import foodhelper.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class NutritionixService {
    private static final String X_APP_ID = "8d1bd058";
    private static final String X_APP_KEY = "ad6fcccb5c6262e88f20a8f9ee39bd02";
    private static final String URL = "https://trackapi.nutritionix.com/v2/natural/nutrients";

    private final ProductRepository productRepository;

    @Autowired
    public NutritionixService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void fetchProducts(String query) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-app-id", X_APP_ID);
        headers.set("x-app-key", X_APP_KEY);

        NutritionixRequest nutritionixRequest = new NutritionixRequest();

        nutritionixRequest.setQuery(query);

        System.out.println("FETCHING PRODUCTS - " + new Object(){}.getClass().getEnclosingMethod().getName());

        final HttpEntity<NutritionixRequest> entity = new HttpEntity<>(nutritionixRequest, headers);
        RestTemplate restTemplate = new RestTemplate();

        NutritionixWrapper nutritionixWrapper = restTemplate.postForObject(URL, entity, NutritionixWrapper.class);
        if (nutritionixWrapper != null) {
            for (Product product : mapToProducts(nutritionixWrapper)) {
                productRepository.save(product);
            }
        }
    }

    public List<Product> mapToProducts(NutritionixWrapper nutritionixWrapper) {
        List<Product> products = new ArrayList<>();

        for (NutritionixResponse food : nutritionixWrapper.getFoods()) {
            Product product = new Product();
            product.setName(food.getFood_name());
            product.setProducer(food.getBrand_name());
            product.setCalories(food.getNf_calories());
            product.setCarbohydrates(food.getNf_total_carbohydrate());
            product.setFat(food.getNf_total_fat());
            product.setGrams(food.getServing_weight_grams());
            product.setProtein(food.getNf_protein());
            products.add(product);
        }
        return products;
    }
}
