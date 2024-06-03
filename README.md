FoodHelper app is a mobile app designed to help you track nutrients from foods you consumed. It has easy to use UI, very easy input form. After running the program just answer the question with what u ate, any form u want! U can list multiple products and the program will return nutrient values.

# IMPORTANT:

I made it so my API access keys are not public, so if a user wants to use this app he has to get his own keys. They are available on this website: https://developer.nutritionix.com/admin/access_details

# Features

- providing nutrient values of any listed food, singular or multiple form,
- doing the same, but with multiple products

# Running

Suggested running in IDE, preferably IntellIJ.

Setup DB

    docker run -p 5432:5432 -it --rm -e POSTGRES_PASSWORD=password server_name

Check DB state

    docker exec -it postgres given name psql -U server_name

    select * from food;

Drop table from DB

    docker exec -it postgres given name psql -U server_name

    drop table food;

After setting up DB, start FoodHelper's main method in IntellIJ.