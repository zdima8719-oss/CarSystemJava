package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CarSystem {
    private List<Car> cars = new ArrayList<>();
    private int nextId = 1;
    private final Scanner scanner = new Scanner(System.in);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final String JSON_FILE = "cars.json";
    private static final String CSV_FILE = "cars.csv";

    public void start() {
        System.out.println("==================================================");
        System.out.println("   СИСТЕМА УЧЁТА АВТОМОБИЛЕЙ АВТОСАЛОНА");
        System.out.println("==================================================");

        loadFromJson();

        if (cars.isEmpty()) {
            cars.add(new Car(1, "Toyota", "Camry", 2022, 3500000.0, 15000, "Белый", 2.5, true));
            cars.add(new Car(2, "BMW", "X5", 2023, 5000000.0, 5000, "Чёрный", 3.0, false));
            nextId = 3;
            System.out.println("Добавлены тестовые автомобили");
        }

        System.out.println("\nЗагружено " + cars.size() + " автомобилей");

        int choice;
        do {
            System.out.println("\n==================================================");
            System.out.println("ГЛАВНОЕ МЕНЮ:");
            System.out.println("==================================================");
            System.out.println("1. Ввод/вывод учётных записей");
            System.out.println("2. Отобразить учётные записи");
            System.out.println("3. Найти учётную запись");
            System.out.println("4. Редактировать учётную запись");
            System.out.println("5. Удалить учётную запись");
            System.out.println("6. Агрегированные показатели");
            System.out.println("7. Выход");
            System.out.print("Ваш выбор (1-7): ");

            choice = readInt();

            switch (choice) {
                case 1: menuIO(); break;
                case 2: menuDisplay(); break;
                case 3: searchCar(); break;
                case 4: editCar(); break;
                case 5: deleteCar(); break;
                case 6: showAggregates(); break;
                case 7:
                    saveToJson();
                    System.out.println("Данные сохранены. До свидания!");
                    break;
                default: System.out.println("Ошибка: введите число от 1 до 7");
            }
        } while (choice != 7);
    }

    private void showAllCars() {
        if (cars.isEmpty()) {
            System.out.println("\nСписок автомобилей пуст");
            return;
        }
        System.out.println("\nСПИСОК АВТОМОБИЛЕЙ:");
        System.out.println("------------------------------------------------------------");
        for (Car car : cars) {
            System.out.println(car);
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Всего: " + cars.size() + " автомобилей");
    }

    private void addCar() {
        System.out.println("\n--- ДОБАВЛЕНИЕ НОВОГО АВТОМОБИЛЯ ---");

        System.out.print("Марка: ");
        String brand = scanner.nextLine().trim();
        if (brand.isEmpty()) { System.out.println("Ошибка: марка не может быть пустой"); return; }

        System.out.print("Модель: ");
        String model = scanner.nextLine().trim();
        if (model.isEmpty()) { System.out.println("Ошибка: модель не может быть пустой"); return; }

        System.out.print("Год выпуска (1950-2026): ");
        int year = readInt();
        if (year < 1950 || year > 2026) { System.out.println("Ошибка: введите корректный год"); return; }

        System.out.print("Цена (руб): ");
        double price = readDouble();
        if (price <= 0) { System.out.println("Ошибка: цена должна быть положительной"); return; }

        System.out.print("Пробег (км): ");
        int mileage = readInt();
        if (mileage < 0) { System.out.println("Ошибка: пробег не может быть отрицательным"); return; }

        System.out.print("Цвет: ");
        String color = scanner.nextLine().trim();
        if (color.isEmpty()) { System.out.println("Ошибка: цвет не может быть пустым"); return; }

        System.out.print("Объём двигателя (л): ");
        double engineVolume = readDouble();
        if (engineVolume <= 0) { System.out.println("Ошибка: объём должен быть положительным"); return; }

        System.out.print("В наличии? (true/false): ");
        boolean inStock = Boolean.parseBoolean(scanner.nextLine().trim());

        Car newCar = new Car(nextId, brand, model, year, price, mileage, color, engineVolume, inStock);
        cars.add(newCar);
        System.out.println("\nАвтомобиль успешно добавлен! ID: " + nextId);
        nextId++;
    }

    private void searchCar() {
        System.out.println("\n--- ПОИСК АВТОМОБИЛЯ ---");
        System.out.print("Введите марку для поиска: ");
        String query = scanner.nextLine().trim().toLowerCase();

        if (query.isEmpty()) { System.out.println("Введите марку машины"); return; }

        List<Car> results = cars.stream()
                .filter(c -> c.getBrand().toLowerCase().contains(query))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("\nАвтомобили марки \"" + query + "\" не найдены");
        } else {
            System.out.println("\nНайдено " + results.size() + " автомобилей:");
            System.out.println("------------------------------------------------------------");
            for (Car car : results) {
                System.out.println(car);
            }
            System.out.println("------------------------------------------------------------");
        }
    }

    private void editCar() {
        System.out.println("\n--- РЕДАКТИРОВАНИЕ АВТОМОБИЛЯ ---");
        if (cars.isEmpty()) { System.out.println("Список пуст"); return; }

        showAllCars();
        System.out.print("\nВведите ID автомобиля для редактирования: ");
        int id = readInt();

        Car car = cars.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        if (car == null) { System.out.println("Автомобиль с ID " + id + " не найден"); return; }

        System.out.println("\nТекущая информация: " + car);
        System.out.println("(Оставьте поле пустым, чтобы не менять)");

        System.out.print("Новая марка (" + car.getBrand() + "): ");
        String newBrand = scanner.nextLine().trim();
        if (!newBrand.isEmpty()) car.setBrand(newBrand);

        System.out.print("Новая модель (" + car.getModel() + "): ");
        String newModel = scanner.nextLine().trim();
        if (!newModel.isEmpty()) car.setModel(newModel);

        System.out.print("Новый год (" + car.getYear() + "): ");
        String yearStr = scanner.nextLine().trim();
        if (!yearStr.isEmpty()) car.setYear(Integer.parseInt(yearStr));

        System.out.print("Новая цена (" + car.getPrice() + "): ");
        String priceStr = scanner.nextLine().trim();
        if (!priceStr.isEmpty()) car.setPrice(Double.parseDouble(priceStr));

        System.out.print("Новый пробег (" + car.getMileage() + "): ");
        String mileageStr = scanner.nextLine().trim();
        if (!mileageStr.isEmpty()) car.setMileage(Integer.parseInt(mileageStr));

        System.out.print("Новый цвет (" + car.getColor() + "): ");
        String newColor = scanner.nextLine().trim();
        if (!newColor.isEmpty()) car.setColor(newColor);

        System.out.print("Новый объём (" + car.getEngineVolume() + "): ");
        String volumeStr = scanner.nextLine().trim();
        if (!volumeStr.isEmpty()) car.setEngineVolume(Double.parseDouble(volumeStr));

        System.out.print("В наличии? (" + car.isInStock() + "): ");
        String stockStr = scanner.nextLine().trim();
        if (!stockStr.isEmpty()) car.setInStock(Boolean.parseBoolean(stockStr));

        System.out.println("\nАвтомобиль успешно обновлён!");
    }

    private void deleteCar() {
        System.out.println("\n--- УДАЛЕНИЕ АВТОМОБИЛЯ ---");
        if (cars.isEmpty()) { System.out.println("Список пуст"); return; }

        showAllCars();
        System.out.print("\nВведите ID автомобиля для удаления: ");
        int id = readInt();

        boolean removed = cars.removeIf(c -> c.getId() == id);
        if (removed) {
            System.out.println("Автомобиль удалён.");
        } else {
            System.out.println("Автомобиль с ID " + id + " не найден");
        }
    }

    private void showAggregates() {
        System.out.println("\n--- АГРЕГИРОВАННЫЕ ПОКАЗАТЕЛИ ---");
        if (cars.isEmpty()) { System.out.println("Нет данных для анализа"); return; }

        double avgPrice = cars.stream().mapToDouble(Car::getPrice).average().orElse(0);
        double maxPrice = cars.stream().mapToDouble(Car::getPrice).max().orElse(0);
        double minPrice = cars.stream().mapToDouble(Car::getPrice).min().orElse(0);

        System.out.println("ОБЩАЯ СТАТИСТИКА:");
        System.out.println("--------------------------------------------------");
        System.out.println("Всего автомобилей: " + cars.size());
        System.out.println("В наличии: " + cars.stream().filter(Car::isInStock).count());
        System.out.println("Продано: " + cars.stream().filter(c -> !c.isInStock()).count());
        System.out.println("ЦЕНА:");
        System.out.printf("   Средняя: %,.0f руб.%n", avgPrice);
        System.out.printf("   Максимальная: %,.0f руб.%n", maxPrice);
        System.out.printf("   Минимальная: %,.0f руб.%n", minPrice);
    }

    private void menuIO() {
        int choice;
        do {
            System.out.println("\n--- ВВОД/ВЫВОД УЧЁТНЫХ ЗАПИСЕЙ ---");
            System.out.println("1. Добавить новую запись");
            System.out.println("2. Загрузить из JSON-файла");
            System.out.println("3. Загрузить из CSV-файла");
            System.out.println("4. Сохранить в JSON-файл");
            System.out.println("5. Сохранить в CSV-файл");
            System.out.println("6. Назад в главное меню");
            System.out.print("Ваш выбор (1-6): ");

            choice = readInt();
            switch (choice) {
                case 1: addCar(); break;
                case 2: loadFromJson(); break;
                case 3: loadFromCsv(); break;
                case 4: saveToJson(); break;
                case 5: saveToCsv(); break;
                case 6: System.out.println("Возврат в главное меню"); break;
                default: System.out.println("Ошибка: введите число от 1 до 6");
            }
        } while (choice != 6);
    }

    private void menuDisplay() {
        int choice;
        do {
            System.out.println("\n--- ОТОБРАЖЕНИЕ УЧЁТНЫХ ЗАПИСЕЙ ---");
            System.out.println("1. Отобразить все записи");
            System.out.println("2. Отсортировать записи");
            System.out.println("3. Отфильтровать записи");
            System.out.println("4. Назад в главное меню");
            System.out.print("Ваш выбор (1-4): ");

            choice = readInt();
            switch (choice) {
                case 1: showAllCars(); break;
                case 2: sortCars(); break;
                case 3: filterCars(); break;
                case 4: System.out.println("Возврат в главное меню"); break;
                default: System.out.println("Ошибка: введите число от 1 до 4");
            }
        } while (choice != 4);
    }

    private void sortCars() {
        System.out.println("\n--- СОРТИРОВКА АВТОМОБИЛЕЙ ---");
        System.out.println("1. По цене (возрастание)");
        System.out.println("2. По цене (убывание)");
        System.out.println("3. По году (новые сначала)");
        System.out.println("4. По году (старые сначала)");
        System.out.println("5. По пробегу (меньше сначала)");
        System.out.print("Ваш выбор: ");

        int choice = readInt();
        switch (choice) {
            case 1: cars.sort(Comparator.comparingDouble(Car::getPrice)); break;
            case 2: cars.sort(Comparator.comparingDouble(Car::getPrice).reversed()); break;
            case 3: cars.sort(Comparator.comparingInt(Car::getYear).reversed()); break;
            case 4: cars.sort(Comparator.comparingInt(Car::getYear)); break;
            case 5: cars.sort(Comparator.comparingInt(Car::getMileage)); break;
            default: System.out.println("Неверный выбор"); return;
        }
        System.out.println("Сортировка выполнена!");
        showAllCars();
    }

    private void filterCars() {
        System.out.println("\n--- ФИЛЬТРАЦИЯ АВТОМОБИЛЕЙ ---");
        System.out.println("1. Только в наличии");
        System.out.println("2. Только проданные");
        System.out.println("3. По марке");
        System.out.println("4. Сбросить фильтр");
        System.out.print("Ваш выбор: ");

        int choice = readInt();
        List<Car> filtered = new ArrayList<>();
        String filterName = "";

        switch (choice) {
            case 1:
                filtered = cars.stream().filter(Car::isInStock).collect(Collectors.toList());
                filterName = "В наличии";
                break;
            case 2:
                filtered = cars.stream().filter(c -> !c.isInStock()).collect(Collectors.toList());
                filterName = "Проданные";
                break;
            case 3:
                System.out.print("Введите марку: ");
                String brand = scanner.nextLine().trim().toLowerCase();
                filtered = cars.stream().filter(c -> c.getBrand().toLowerCase().equals(brand)).collect(Collectors.toList());
                filterName = "Марка: " + brand;
                break;
            case 4:
                showAllCars();
                return;
            default:
                System.out.println("Неверный выбор");
                return;
        }

        if (filtered.isEmpty()) {
            System.out.println("\nНет автомобилей по фильтру: " + filterName);
        } else {
            System.out.println("\nФИЛЬТР: " + filterName);
            System.out.println("Найдено: " + filtered.size() + " автомобилей");
            for (Car car : filtered) {
                System.out.println(car);
            }
        }
    }

    private void saveToJson() {
        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            gson.toJson(cars, writer);
            System.out.println("Сохранено " + cars.size() + " автомобилей в файл: " + JSON_FILE);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    private void loadFromJson() {
        File file = new File(JSON_FILE);
        if (!file.exists()) {
            System.out.println("Файл " + JSON_FILE + " не найден. Начинаем с пустого списка.");
            return;
        }

        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
            Type listType = new TypeToken<ArrayList<Car>>(){}.getType();
            List<Car> loadedCars = gson.fromJson(jsonString, listType);

            if (loadedCars != null) {
                cars.clear();
                cars.addAll(loadedCars);
                nextId = cars.isEmpty() ? 1 : cars.stream().mapToInt(Car::getId).max().getAsInt() + 1;
                System.out.println("Загружено " + cars.size() + " автомобилей из файла: " + JSON_FILE);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке: " + e.getMessage());
        }
    }

    private void saveToCsv() {
        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            writer.write("id,brand,model,year,price,mileage,color,engineVolume,inStock\n");
            for (Car car : cars) {
                writer.write(car.getId() + "," + car.getBrand() + "," + car.getModel() + "," +
                        car.getYear() + "," + car.getPrice() + "," + car.getMileage() + "," +
                        car.getColor() + "," + car.getEngineVolume() + "," + car.isInStock() + "\n");
            }
            System.out.println("Сохранено " + cars.size() + " автомобилей в файл: " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении CSV: " + e.getMessage());
        }
    }

    private void loadFromCsv() {
        File file = new File(CSV_FILE);
        if (!file.exists()) { System.out.println("Файл " + CSV_FILE + " не найден"); return; }

        try {
            List<String> lines = Files.readAllLines(Paths.get(CSV_FILE));
            if (lines.size() < 2) { System.out.println("Файл пуст"); return; }

            List<Car> loadedCars = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 9) {
                    Car car = new Car(
                            Integer.parseInt(parts[0]), parts[1], parts[2],
                            Integer.parseInt(parts[3]), Double.parseDouble(parts[4]),
                            Integer.parseInt(parts[5]), parts[6],
                            Double.parseDouble(parts[7]), Boolean.parseBoolean(parts[8])
                    );
                    loadedCars.add(car);
                }
            }

            if (!loadedCars.isEmpty()) {
                cars.clear();
                cars.addAll(loadedCars);
                nextId = cars.stream().mapToInt(Car::getId).max().getAsInt() + 1;
                System.out.println("Загружено " + cars.size() + " автомобилей из файла: " + CSV_FILE);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке CSV: " + e.getMessage());
        }
    }

    // Вспомогательные методы для безопасного ввода
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ошибка: введите целое число: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.print("Ошибка: введите число: ");
            }
        }
    }
}